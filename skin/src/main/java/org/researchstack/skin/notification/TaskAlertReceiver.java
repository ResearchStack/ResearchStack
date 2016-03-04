package org.researchstack.skin.notification;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.storage.database.TaskRecord;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.schedule.ScheduleHelper;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import rx.Observable;


public class TaskAlertReceiver extends BroadcastReceiver
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Intent Actions
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final String ALERT_CREATE     = "org.researchstack.skin.notification.ALERT_CREATE";
    public static final String ALERT_DELETE     = "org.researchstack.skin.notification.ALERT_DELETE";
    public static final String ALERT_CREATE_ALL = "org.researchstack.skin.notification.ALERT_CREATE_ALL";

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Intent Keys
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    public static final String KEY_NOTIFICATION    = "CreateAlertReceiver.KEY_NOTIFICATION";
    public static final String KEY_NOTIFICATION_ID = "CreateAlertReceiver.KEY_NOTIFICATION_ID";

    public void onReceive(Context context, Intent intent)
    {
        Log.i("CreateAlertReceiver", "onReceive() _ " + intent.getAction());

        switch(intent.getAction())
        {
            case ALERT_DELETE:
                Observable.create(subscriber -> {
                    int taskNotificationId = intent.getIntExtra(KEY_NOTIFICATION_ID, - 1);

                    if(taskNotificationId != - 1)
                    {
                        NotificationHelper.getInstance(context)
                                .deleteTaskNotification(taskNotificationId);
                        LogExt.d(TaskAlertReceiver.class,
                                "TaskNotification[id." + taskNotificationId + "] Deleted");
                    }
                    else
                    {
                        LogExt.e(TaskAlertReceiver.class,
                                "TaskNotification Delete failed, unknown id " + taskNotificationId);
                    }
                }).subscribe();
                break;

            case ALERT_CREATE:
                TaskNotification taskNotification = (TaskNotification) intent.getSerializableExtra(
                        KEY_NOTIFICATION);
                createAlert(context, taskNotification);
                break;

            case ALERT_CREATE_ALL:
                //TODO this can be better with Rx, learn how (flatMap)
                Observable.create(subscriber -> {
                    subscriber.onNext(NotificationHelper.getInstance(context)
                            .loadTaskNotifications());
                })
                        .compose(ObservableUtils.applyDefault())
                        .map(o -> (List<TaskNotification>) o)
                        .subscribe(list -> {
                            if(list != null)
                            {
                                for(TaskNotification notification : list)
                                {
                                    createAlert(context, notification);
                                }
                            }
                        });

                break;
        }
    }

    private void createAlert(Context context, TaskNotification notification)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Create out intent that is sent to TaskNotificationReceiver
        Intent taskNotificationIntent = new Intent(context,
                UiManager.getInstance().getTaskNotificationReceiver());
        taskNotificationIntent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, notification.id);

        // Remove pending intent if one already exists
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                taskNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        // Generate the time the intent will fire
        Date nextExecuteTime = ScheduleHelper.nextSchedule(notification.chronTime,
                notification.endDate);

        // Add Alarm
        alarmManager.set(AlarmManager.RTC,
                nextExecuteTime.getTime(),
                pendingIntent);

        DateFormat format = FormatHelper.getFormat(DateFormat.LONG, DateFormat.LONG);
        LogExt.i(getClass(), "Alarm " + notification.id + " Created. It will execute on or near " +
                format.format(nextExecuteTime));
    }

    public static Intent createDeleteIntent(int notificationId)
    {
        Intent deleteTaskIntent = new Intent(TaskAlertReceiver.ALERT_DELETE);
        deleteTaskIntent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION_ID, notificationId);
        return deleteTaskIntent;
    }
}
