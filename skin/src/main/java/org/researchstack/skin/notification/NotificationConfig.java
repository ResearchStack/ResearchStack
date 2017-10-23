package org.researchstack.skin.notification;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

/**
 * Configuration class for framework notifciations
 */
public abstract class NotificationConfig {
    private static NotificationConfig instance;

    /**
     * Default Constructor
     */
    NotificationConfig() {
    }

    /**
     * Initializes the NotificationConfig singleton. It is best to call this method inside your
     * {@link Application#onCreate()} method.
     *
     * @param manager an implementation of NotificationConfig
     */
    public static void init(NotificationConfig manager) {
        NotificationConfig.instance = manager;
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static NotificationConfig getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "NotificationConfig instance is null. Make sure to init a concrete " +
                            "implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * Returns the drawable resource for the notification icon
     *
     * @return the drawable resource for notification icon
     */
    @DrawableRes
    public abstract int getSmallIcon();

    /**
     * Returns the color that the large notification icon should be
     *
     * @param context android context
     * @return color int of the large icon in the notification shade
     */
    @ColorInt
    public abstract int getLargeIconBackgroundColor(Context context);

    /**
     * Return ticker text for notification
     *
     * @param context android application
     * @return text for notification ticket
     */
    public abstract CharSequence getTickerText(Context context);

    /**
     * Returns text for notification title
     *
     * @param context android application
     * @return text for notification title
     */
    public abstract CharSequence getContentTitle(Context context);

    /**
     * Returns text for notification body
     *
     * @param context android application
     * @return text for notification body
     */
    public abstract CharSequence getContentText(Context context);
}
