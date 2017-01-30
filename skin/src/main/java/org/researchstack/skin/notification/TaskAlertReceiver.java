package org.researchstack.skin.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.researchstack.backbone.storage.NotificationHelper;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.schedule.ScheduleHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;


public class TaskAlertReceiver extends BroadcastReceiver {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Intent Actions
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final String ALERT_CREATE = "org.researchstack.skin.notification.ALERT_CREATE";
    public static final String ALERT_CREATE_ALL = "org.researchstack.skin.notification.ALERT_CREATE_ALL";
    public static final String ALERT_DELETE = "org.researchstack.skin.notification.ALERT_DELETE";
    public static final String ALERT_DELETE_ALL = "org.researchstack.skin.notification.ALERT_DELETE_ALL";

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Intent Keys
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final String KEY_NOTIFICATION = "CreateAlertReceiver.KEY_NOTIFICATION";
    public static final String KEY_NOTIFICATION_ID = "CreateAlertReceiver.KEY_NOTIFICATION_ID";

    public static Intent createDeleteIntent(int notificationId) {
        Intent deleteTaskIntent = new Intent(TaskAlertReceiver.ALERT_DELETE);
        deleteTaskIntent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, notificationId);
        return deleteTaskIntent;
    }

    public void onReceive(Context context, Intent intent) {
        Log.i("CreateAlertReceiver", "onReceive() _ " + intent.getAction());

        switch (intent.getAction()) {
            case ALERT_CREATE:
                TaskNotification taskNotification = (TaskNotification) intent.getSerializableExtra(
                        KEY_NOTIFICATION);
                postNotificationToAlertManager(context, taskNotification);
                break;

            case ALERT_CREATE_ALL:
                Observable.defer(() -> Observable.from(NotificationHelper.getInstance(context)
                        .loadTaskNotifications()))
                        .compose(ObservableUtils.applyDefault())
                        .subscribe(notification -> {
                            postNotificationToAlertManager(context, notification);
                        });

                break;

            case ALERT_DELETE:
                int taskNotificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, -1);
                deleteNotificationAndRemoveAlert(context, taskNotificationId);
                break;

            case ALERT_DELETE_ALL:
                Observable.create(subscriber -> {
                    subscriber.onNext(NotificationHelper.getInstance(context)
                            .loadTaskNotifications());
                })
                        .compose(ObservableUtils.applyDefault())
                        .map(o -> (List<TaskNotification>) o)
                        .flatMap(notifications -> Observable.from(notifications))
                        .subscribe(notification -> {
                            deleteNotificationAndRemoveAlert(context, notification.id);
                        });
                break;
        }
    }

    private void deleteNotificationAndRemoveAlert(Context context, int taskNotificationId) {
        Observable.create(subscriber -> {
            if (taskNotificationId != -1) {
                // Delete from database
                NotificationHelper.getInstance(context).deleteTaskNotification(taskNotificationId);

                LogExt.d(TaskAlertReceiver.class,
                        "TaskNotification[id." + taskNotificationId + "] Deleted");

                // Call onNext and remove alert
                subscriber.onNext(null);
            } else {
                LogExt.e(TaskAlertReceiver.class,
                        "TaskNotification Delete failed, invalid id " + taskNotificationId);
            }
        }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
            cancelAlert(context, taskNotificationId);
        });
    }

    private void postNotificationToAlertManager(Context context, TaskNotification taskNotification) {
        // Get our pending intent wrapper for our taskNotification
        PendingIntent pendingIntent = createNotificationIntent(context, taskNotification.id);

        // Generate the time the intent will fire
        Date nextExecuteTime = ScheduleHelper.nextSchedule(taskNotification.chronTime,
                taskNotification.endDate);

        // Create alert
        createAlert(context, pendingIntent, nextExecuteTime);
    }

    private PendingIntent createNotificationIntent(Context context, int taskNotificationId) {
        // Create out intent that is sent to TaskNotificationReceiver
        Intent taskNotificationIntent = new Intent(context,
                UiManager.getInstance().getTaskNotificationReceiver());
        taskNotificationIntent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, taskNotificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                taskNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private void createAlert(Context context, PendingIntent pendingIntent, Date nextExecuteTime) {
        // Add Alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextExecuteTime.getTime(), pendingIntent);

        DateFormat format = FormatHelper.getFormat(DateFormat.LONG, DateFormat.LONG);
        LogExt.i(getClass(),
                "Alarm  Created. Executing on or near " + format.format(nextExecuteTime));
    }

    private void cancelAlert(Context context, int taskNotificationId) {
        // Get our pending intent wrapper for our taskNotification
        PendingIntent pendingIntent = createNotificationIntent(context, taskNotificationId);

        // Remove pending intent if one already exists
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
