package io.dove.whoareyou;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.List;
import java.util.Stack;

/**
 * Created by flyer on 28/12/2017.
 */

public class DoveMonitor implements Application.ActivityLifecycleCallbacks {

    private List<TaskStack> mTaskStacks = new Stack<>();

    private Context mContext;
    private NotificationMonitor mNotificationMonitor;

    public DoveMonitor(Context context){
        this.mContext = context;
        mNotificationMonitor = new NotificationMonitor(mContext);
    }

    private void notification(StackEvent stackEvent,ActivityInstanceInfo activityInstanceInfo){
        RxBus.getRxBusInstance().post(stackEvent);
        mNotificationMonitor.notification(activityInstanceInfo,stackEvent);
    }

    private ActivityInstanceInfo findActivityInstanceInfo(boolean newInstance,int id,String name){
        for (TaskStack taskStack : mTaskStacks) {
            if (id == taskStack.getId()){
                Stack<ActivityInstanceInfo> activityInstanceInfoStack = taskStack.getActivityInstanceInfoStack();
                for (ActivityInstanceInfo activityInstanceInfo : activityInstanceInfoStack) {
                    if (name.equals(activityInstanceInfo.getName())){
                        if (newInstance){
                            return new ActivityInstanceInfo(id,name,activityInstanceInfo.getActivityCls());
                        } else {
                            return activityInstanceInfo;
                        }
                    }
                }
            }
        }
        return null;
    }

    private TaskStack findTaskStack(int id){
        for (TaskStack taskStack : mTaskStacks) {
            if (id == taskStack.getId()){
                return taskStack;
            }
        }
        return null;
    }

    private void notifyActivityChanges(Activity activity, ActivityInstanceInfo.Action action){
        ActivityInstanceInfo instanceInfo = findActivityInstanceInfo(false,activity.getTaskId(),activity.toString());
        TaskStack taskStack = findTaskStack(activity.getTaskId());
        if (instanceInfo == null) return;
        instanceInfo.setAction(action);
        notification(new StackEvent(StackEvent.NONE,taskStack),instanceInfo);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        int id = activity.getTaskId();
        ActivityInstanceInfo activityInstanceInfo = new ActivityInstanceInfo(id,activity.toString(),activity.getClass());
        activityInstanceInfo.setAction(ActivityInstanceInfo.Action.ON_CREATED);
        for (TaskStack taskStack : mTaskStacks) {
            if (taskStack.getId() == id){
                taskStack.getActivityInstanceInfoStack().push(activityInstanceInfo);
                notification(new StackEvent(StackEvent.PUT,taskStack),activityInstanceInfo);
                return;
            }
        }
        TaskStack taskStack = new TaskStack();
        taskStack.setActivityInstanceInfoStack(new Stack<ActivityInstanceInfo>());
        taskStack.getActivityInstanceInfoStack().push(activityInstanceInfo);
        taskStack.setId(id);
        mTaskStacks.add(taskStack);
        notification(new StackEvent(StackEvent.PUT,taskStack),activityInstanceInfo);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        notifyActivityChanges(activity, ActivityInstanceInfo.Action.ON_STARTED);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        notifyActivityChanges(activity, ActivityInstanceInfo.Action.ON_RESUMED);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        notifyActivityChanges(activity, ActivityInstanceInfo.Action.ON_PAUSED);
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        notifyActivityChanges(activity, ActivityInstanceInfo.Action.ON_STOPPED);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        TaskStack removeFrom = null;
        for (TaskStack taskStack : mTaskStacks) {
            if (taskStack.getId() == activity.getTaskId()){
                removeFrom = taskStack;
                break;
            }
        }
        if (removeFrom == null) return;
        if (removeFrom.getActivityInstanceInfoStack() == null) return;

        removeFrom.getActivityInstanceInfoStack().pop();

        ActivityInstanceInfo activityInstanceInfo = new ActivityInstanceInfo(
                activity.getTaskId(),
                activity.toString(),
                activity.getClass()
        );
        activityInstanceInfo.setAction(ActivityInstanceInfo.Action.ON_DESTROYED);
        notification(new StackEvent(StackEvent.REMOVE,removeFrom), activityInstanceInfo);
    }

    public List<TaskStack> getTaskStacks() {
        return mTaskStacks;
    }


    public static class StackEvent{
        public static final int PUT = 0;
        public static final int REMOVE = 1;
        public static final int NONE = -1;

        private int mType = PUT;
        private TaskStack mTaskStack;
        public StackEvent(int type,TaskStack taskStack){
            this.mType = type;
            this.mTaskStack = taskStack;

        }

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            mType = type;
        }

        public TaskStack getTaskStack() {
            return mTaskStack;
        }

        public void setTaskStack(TaskStack taskStack) {
            mTaskStack = taskStack;
        }
    }
}
