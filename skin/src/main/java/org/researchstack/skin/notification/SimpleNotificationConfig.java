package org.researchstack.skin.notification;

import android.content.Context;

import org.researchstack.skin.R;

public class SimpleNotificationConfig extends NotificationConfig {
    @Override
    public int getSmallIcon() {
        return R.drawable.rss_ic_notification_24dp;
    }

    @Override
    public int getLargeIconBackgroundColor(Context context) {
        return context.getResources().getColor(R.color.rsb_error);
    }

    @Override
    public CharSequence getTickerText(Context context) {
        return "Survey Reminder";
    }

    @Override
    public CharSequence getContentTitle(Context context) {
        return "Survey Reminder";
    }

    @Override
    public CharSequence getContentText(Context context) {
        return "You have a new survey to complete.";
    }

}
