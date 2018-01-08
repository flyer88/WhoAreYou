package io.dove.whoareyou;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;
import java.util.Stack;

/**
 * Created by flyer on 04/01/2018.
 */

public class MessageHandler extends Thread{

    private static Handler mHandler;
    private final List<TaskStack> mTaskStacks;
    private Context mContext;
    private NotificationMonitor mNotificationMonitor;

    public MessageHandler(Context context, List<TaskStack> taskStacks){
        mTaskStacks = taskStacks;
        this.mContext = context;
        mNotificationMonitor = new NotificationMonitor(mContext);
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DoveMonitor.StackEvent.PUT:
                        MessageHandler.this.pushIntoStack(msg.arg1, (ActivityInstanceInfo) msg.obj);
                        break;
                    case DoveMonitor.StackEvent.REMOVE:
                        MessageHandler.this.popFromStack((ActivityInstanceInfo) msg.obj);
                        break;
                    default:
                        MessageHandler.this.notifyActivityChanges((ActivityInstanceInfo) msg.obj);
                        break;
                }
            }
        };
        Looper.loop();
    }

    public void handle(int flags,int type, ActivityInstanceInfo activityInstanceInfo){
        Message message = new Message();
        message.what = type;
        message.obj = activityInstanceInfo;
        message.arg1 = flags;
        mHandler.sendMessage(message);
    }


    private void notification(DoveMonitor.StackEvent stackEvent, ActivityInstanceInfo activityInstanceInfo){
        mNotificationMonitor.notification(activityInstanceInfo,stackEvent);
    }

    /**
     * 放入栈中
     * @param flags
     * @param activityInstanceInfo
     */
    private void pushIntoStack(int flags,ActivityInstanceInfo activityInstanceInfo){
        int taskId  = activityInstanceInfo.getTaskId();
        handleSpecialFlag(taskId,flags,activityInstanceInfo.getActivityCls());
        for (TaskStack taskStack : mTaskStacks) {
            if (taskStack.getId() == taskId){
                taskStack.getActivityInstanceInfoStack().push(activityInstanceInfo);
                notification(new DoveMonitor.StackEvent(DoveMonitor.StackEvent.PUT,taskStack),activityInstanceInfo);
                return;
            }
        }
        TaskStack taskStack = new TaskStack();
        taskStack.setActivityInstanceInfoStack(new Stack<ActivityInstanceInfo>());
        taskStack.getActivityInstanceInfoStack().push(activityInstanceInfo);
        taskStack.setId(taskId);
        mTaskStacks.add(taskStack);
        notification(new DoveMonitor.StackEvent(DoveMonitor.StackEvent.PUT,taskStack),activityInstanceInfo);
    }

    /**
     * 从栈中 pop 出来
     * @param activityInstanceInfo
     */
    private void popFromStack(ActivityInstanceInfo activityInstanceInfo){
        // TODO: 04/01/2018 把异步问题 fix，自己高线程，然后用 Looper 去搞
        int taskId = activityInstanceInfo.getTaskId();
        String activityName = activityInstanceInfo.getName();
        Class activityCls = activityInstanceInfo.getActivityCls();
        TaskStack removeFrom = null;
        for (TaskStack taskStack : mTaskStacks) {
            if (taskStack.getId() == taskId){
                removeFrom = taskStack;
                break;
            }
        }

        if (removeFrom == null
                || removeFrom.getActivityInstanceInfoStack() == null
                || removeFrom.getActivityInstanceInfoStack().empty())
            return;

        Stack<ActivityInstanceInfo> activityInstanceInfoStack = removeFrom.getActivityInstanceInfoStack();

        for (ActivityInstanceInfo instanceInfo : activityInstanceInfoStack) {
            if (activityName.equals(instanceInfo.getName())){
                activityInstanceInfoStack.remove(instanceInfo);
                break;
            }
        }

        ActivityInstanceInfo activityInstanceInfo2 = new ActivityInstanceInfo(
                taskId, activityName, activityCls,activityInstanceInfo.getAction()
        );
        notification(new DoveMonitor.StackEvent(DoveMonitor.StackEvent.REMOVE,removeFrom), activityInstanceInfo2);
    }


    /**
     * 根据 task id 和 Activity 的 name
     * 创建一个新的 {@link ActivityInstanceInfo}，并返回
     * @param id
     * @param name
     * @return
     */
    private ActivityInstanceInfo findActivityInstanceInfoWithNew(int id, String name){
        for (TaskStack taskStack : mTaskStacks) {
            if (id == taskStack.getId()){
                Stack<ActivityInstanceInfo> activityInstanceInfoStack = taskStack.getActivityInstanceInfoStack();
                for (ActivityInstanceInfo activityInstanceInfo : activityInstanceInfoStack) {
                    if (name.equals(activityInstanceInfo.getName())){
                        return new ActivityInstanceInfo(id,name,
                                activityInstanceInfo.getActivityCls(),
                                activityInstanceInfo.getAction());
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据 task id 找到 task 对象
     * @param id
     * @return
     */
    private TaskStack findTaskStack(int id){
        for (TaskStack taskStack : mTaskStacks) {
            if (id == taskStack.getId()){
                return taskStack;
            }
        }
        return null;
    }

    /**
     * 通知 Activity OnCreated 和 OnDestroyed 中间的修改
     * @param activityInstanceInfo
     */
    private void notifyActivityChanges(ActivityInstanceInfo activityInstanceInfo){
        ActivityInstanceInfo instanceInfo = findActivityInstanceInfoWithNew(activityInstanceInfo.getTaskId(),
                activityInstanceInfo.getName());
        TaskStack taskStack = findTaskStack(activityInstanceInfo.getTaskId());
        if (instanceInfo == null) return;
        instanceInfo.setAction(activityInstanceInfo.getAction());
        notification(new DoveMonitor.StackEvent(DoveMonitor.StackEvent.NONE,taskStack),instanceInfo);
    }

    /**
     * 处理 ClearTop flag 导致的 Activity 移除不出发回调
     * @param taskStack
     * @param cls
     * @return
     */
    private boolean clearTop(TaskStack taskStack,Class cls){
        Stack<ActivityInstanceInfo> activityInstanceInfoStack = taskStack.getActivityInstanceInfoStack();
        if (activityInstanceInfoStack == null || activityInstanceInfoStack.empty()) return false;
        int count = 0;
        boolean contains = false;
        for (int i = 0;i<activityInstanceInfoStack.size();i++) {
            count = count + 1;
            ActivityInstanceInfo activityInstanceInfo = activityInstanceInfoStack
                    .elementAt(activityInstanceInfoStack.size() - count);
            if (cls == activityInstanceInfo.getActivityCls()){
                contains = true;
                break;
            }
        }
        if (!contains) return false;
        for (int i=0; i<count; i++){
            activityInstanceInfoStack.pop();
        }
        return true;
    }

    /**
     * 处理特殊 flag，目前就 {@link Intent#FLAG_ACTIVITY_CLEAR_TOP}
     * @param taskId
     * @param flags
     * @param cls
     */
    private void handleSpecialFlag(int taskId,int flags,Class cls){
        if (flags == 0) return;
        TaskStack taskStack = findTaskStack(taskId);
        if (taskStack == null) return;
        if ((flags & Intent.FLAG_ACTIVITY_CLEAR_TOP) > 0){
            // 遍历栈中 Activity，然后清楚到对应的 Activity，包括对应的
            clearTop(taskStack,cls);
        }
    }
}
