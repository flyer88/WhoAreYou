package io.dove.whoareyou;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;


/**
 * Created by flyer on 29/12/2017.
 */

public class NotificationMonitor {

    private Context mContext;
    private NotificationCompat.Builder mBuilder;

    private int id = 1;
    private RemoteViews mRemoteViews;

    public NotificationMonitor(Context context){
        this.mContext = context;
        this.mRemoteViews = new RemoteViews(mContext.getPackageName(),R.layout.layout_notification);
    }

    private void setRemoteViews(ActivityInstanceInfo activityInstanceInfo,DoveMonitor.StackEvent stackEvent){
        mRemoteViews.setTextViewText(R.id.task_id_tv,"task id: " + activityInstanceInfo.getTaskId());

//        mRemoteViews.setTextViewText(R.id.action_tv,
//                activityInstanceInfo.getAction().value + ": " + activityInstanceInfo.getActivityCls().getSimpleName());
        TaskStack taskStack = stackEvent.getTaskStack();
        ActivityInstanceInfo currentActivityInstanceInfo = taskStack.getActivityInstanceInfoStack().peek();
        if (currentActivityInstanceInfo != null) {
            mRemoteViews.setTextViewText(R.id.current_activity_name_tv, "当前 Activity: " + currentActivityInstanceInfo.getName());
        }
    }

    public void notification(ActivityInstanceInfo activityInstanceInfo, DoveMonitor.StackEvent stackEvent) {
        if (stackEvent == null || stackEvent.getTaskStack() == null
                || stackEvent.getTaskStack().getActivityInstanceInfoStack() == null
                || stackEvent.getTaskStack().getActivityInstanceInfoStack().empty()){
            return;
        }

        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(mContext, mContext.getPackageName());
        }
        setRemoteViews(activityInstanceInfo,stackEvent);
        mBuilder.setCustomContentView(mRemoteViews);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);

        if (activityInstanceInfo.getActivityCls() != MonitorActivity.class){
            Intent intent = new Intent(mContext, MonitorActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
            mBuilder.setContentIntent(pIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) mNotificationManager.notify(id, mBuilder.build());
    }
}
