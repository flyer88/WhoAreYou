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
    private final MessageHandler mMessageHandler;
    private Context mContext;

    public DoveMonitor(Context context){
        this.mContext = context;
        mMessageHandler = new MessageHandler(mContext,mTaskStacks);
        mMessageHandler.start();
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        int flags = -1;
        if (activity.getIntent() != null) {
             flags = activity.getIntent().getFlags();
        }
        mMessageHandler.handle(flags,StackEvent.PUT,new ActivityInstanceInfo(
                activity.getTaskId(),activity.toString(),
                activity.getClass(), ActivityInstanceInfo.Action.ON_CREATED
        ));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mMessageHandler.handle(-1,StackEvent.NONE,new ActivityInstanceInfo(
                activity.getTaskId(),activity.toString(),
                activity.getClass(), ActivityInstanceInfo.Action.ON_STARTED
        ));

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mMessageHandler.handle(-1,StackEvent.NONE,new ActivityInstanceInfo(
                activity.getTaskId(),activity.toString(),
                activity.getClass(), ActivityInstanceInfo.Action.ON_RESUMED
        ));

    }

    @Override
    public void onActivityPaused(Activity activity) {
        mMessageHandler.handle(-1,StackEvent.NONE,new ActivityInstanceInfo(
                activity.getTaskId(),activity.toString(),
                activity.getClass(), ActivityInstanceInfo.Action.ON_PAUSED
        ));

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mMessageHandler.handle(-1,StackEvent.REMOVE,new ActivityInstanceInfo(
                activity.getTaskId(),activity.toString(),
                activity.getClass(), ActivityInstanceInfo.Action.ON_DESTROYED
        ));

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
