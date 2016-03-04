package org.researchstack.skin.notification;
import android.content.Context;
import android.support.annotation.DrawableRes;

public abstract class NotificationConfig
{
    private static NotificationConfig instance;

    NotificationConfig()
    {
    }

    public static void init(NotificationConfig manager)
    {
        NotificationConfig.instance = manager;
    }

    public static NotificationConfig getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "NotificationConfig instance is null. Make sure to init a concrete " +
                            "implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    @DrawableRes
    public abstract int getSmallIcon();

    public abstract int getLargeIconBackgroundColor(Context context);

    public abstract CharSequence getTickerText(Context context);

    public abstract CharSequence getContentTitle(Context context);

    public abstract CharSequence getContentText(Context context);
}
