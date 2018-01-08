package io.dove.whoareyou;

/**
 * Created by flyer on 28/12/2017.
 */
public class ActivityInstanceInfo {

    private int taskId;// 所处的栈
    private String name;// 实例名字
    private String simpleName;// 短名字
    private Class activityCls;// activity 类名
    private Action mAction;


    public ActivityInstanceInfo(int taskId, String name,Class activityCls,Action action){
        this.taskId = taskId;
        this.name = name;
        this.activityCls = activityCls;
        this.mAction = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Class getActivityCls() {
        return activityCls;
    }

    public void setActivityCls(Class activityCls) {
        this.activityCls = activityCls;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public Action getAction() {
        return mAction;
    }

    public void setAction(Action action) {
        mAction = action;
    }

    public static enum Action{
        ON_CREATED("onActivityCreated"),
        ON_STARTED("onActivityStarted"),
        ON_RESUMED("onActivityResumed"),
        ON_PAUSED("onActivityPaused"),
        ON_STOPPED("onActivityStopped"),
        ON_DESTROYED("onActivityDestroyed");

        String value;
        Action(String value){
            this.value = value;
        }
    }
}
