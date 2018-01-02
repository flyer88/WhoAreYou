package io.dove.whoareyou;

import java.util.Stack;

/**
 * Created by flyer on 29/12/2017.
 */

public class TaskStack {
    int id;
    Stack<ActivityInstanceInfo> mActivityInstanceInfoStack;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stack<ActivityInstanceInfo> getActivityInstanceInfoStack() {
        return mActivityInstanceInfoStack;
    }

    public void setActivityInstanceInfoStack(Stack<ActivityInstanceInfo> activityInstanceInfoStack) {
        mActivityInstanceInfoStack = activityInstanceInfoStack;
    }
}
