package io.dove.whoareyou;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by flyer on 29/12/2017.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder>{

    private List<TaskStack> mTaskStacks = new ArrayList<>();

    private final static int TYPE_TASK = 0;
    private final static int TYPE_ACTIVITY = 1;
    private Context mContext;

    public RvAdapter(Context context, List<TaskStack> taskStacks){
        if (taskStacks != null) {
            mTaskStacks = taskStacks;
        }
        this.mContext = context;
    }


    public void setTaskStacks(List<TaskStack> taskStacks){
        this.mTaskStacks = taskStacks;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        if (viewType == TYPE_TASK) {
            viewHolder = new ViewHolder(TYPE_TASK,LayoutInflater.from(mContext).inflate(R.layout.item_task, null));
        } else {
            viewHolder = new ViewHolder(TYPE_ACTIVITY,LayoutInflater.from(mContext).inflate(R.layout.item_activity, null));
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type){
            case TYPE_ACTIVITY:
                ActivityInstanceInfo activityInstanceInfo = getActivityInstanceInfo(position);
                if (activityInstanceInfo == null){
                    holder.mActivityName.setText("找不到 Activity，出错了");
                } else {
                    holder.mActivityName.setText(activityInstanceInfo.getName());
                }
                break;
            case TYPE_TASK:
                TaskStack taskStack = getTaskStack(position);
                if (taskStack == null){
                    holder.mTaskId.setText("找不到栈，出错了");
                } else {
                    holder.mTaskId.setText("task id: " + taskStack.getId());
                }
                break;
        }
    }

    private ActivityInstanceInfo getActivityInstanceInfo(int pos){
        if (pos <= 0) return null;
        pos = pos + 1;
        for (TaskStack taskStack : mTaskStacks) {
            pos = pos - 1;
            Stack<ActivityInstanceInfo> activityInstanceInfoList = taskStack.getActivityInstanceInfoStack();
            for (ActivityInstanceInfo activityInstanceInfo : activityInstanceInfoList) {
                pos = pos - 1;
                if(pos == 0){
                    return activityInstanceInfo;
                }
            }
        }
        return null;
    }

    private TaskStack getTaskStack(int pos){
        pos = pos + 1;
        for (TaskStack taskStack : mTaskStacks) {
            pos = pos - 1;
            if (pos == 0){
                return taskStack;
            }
            pos = pos - taskStack.getActivityInstanceInfoStack().size();
        }
        return null;
    }


    @Override
    public int getItemCount() {
        int count = 0;
        for (TaskStack taskStack : mTaskStacks) {
            count = count + taskStack.getActivityInstanceInfoStack().size();
            count = count + 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        position = position + 1;
        for (TaskStack taskStack : mTaskStacks) {
            position = position - 1;
            if (position == 0){
                return TYPE_TASK;
            }
            int count = taskStack.getActivityInstanceInfoStack().size();
            if (position > count) {
                position = position - count;
            } else {
                return TYPE_ACTIVITY;
            }
        }
        return TYPE_ACTIVITY;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTaskId;
        TextView mActivityName;
        private int mType;
        public ViewHolder(int type, View itemView) {
            super(itemView);
            this.mType = type;
            switch (mType){
                case TYPE_TASK:
                    mTaskId = itemView.findViewById(R.id.task_id_tv);
                    break;
                case TYPE_ACTIVITY:
                    mActivityName = itemView.findViewById(R.id.activity_name_tv);
                    break;
            }
        }

    }
}
