package org.researchstack.skin.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.researchstack.skin.ui.MainActivity;


public class TaskNotificationReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.i("TaskNotifReceiver", "onReceive()");

        int notificationId = intent.getIntExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, 0);

        // Create pending intent wrapper that will open MainActivity
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, notificationId);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create pending intent wrapper that will delete the TaskNotification from our
        // TaskNotification database
        Intent deleteTaskIntent = new Intent(TaskAlertReceiver.ALERT_DELETE);
        deleteTaskIntent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, notificationId);
        PendingIntent deleteIntent = PendingIntent.getBroadcast(context,
                0,
                deleteTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationConfig config = NotificationConfig.getInstance();
        Notification notification = new NotificationCompat.Builder(context).setContentIntent(
                contentIntent)
                .setDeleteIntent(deleteIntent)
                .setSmallIcon(config.getSmallIcon())
                .setColor(config.getLargeIconBackgroundColor(context))
                .setTicker(config.getTickerText(context))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(config.getContentTitle(context))
                .setContentText(config.getContentText(context))
                .build();

        // Execute Notification
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                notificationId,
                notification);
    }
}
