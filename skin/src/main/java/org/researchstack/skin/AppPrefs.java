package org.researchstack.skin;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.researchstack.skin.ui.fragment.SettingsFragment;

import java.util.Date;

public class AppPrefs
{
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Statics
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private static final String KEY_ONBOARDING_COMPLETE = "settings_onboarding_complete";
    private static final String KEY_ONBOARDING_SKIPPED  = "settings_onboarding_skipped";
    private static final String KEY_ONBOARDING_COMPLETE_TIME = "settings_onboarding_complete_time";
    private static final String KEY_EMAIL_VERIFIED = "settings_email_verified";
    private static final String KEY_EMAIL_VERIFIED_TIME = "settings_email_verified_time";
    private static AppPrefs instance;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Field Vars
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private final SharedPreferences prefs;

    AppPrefs(Context context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized AppPrefs getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new AppPrefs(context);
        }
        return instance;
    }

    /**
     * If auto lock is disabled, default the time to a year.
     *
     * @return time in milliseconds
     */
    public long getAutoLockTime()
    {
        boolean isAutoLocked = prefs.getBoolean(SettingsFragment.KEY_AUTO_LOCK_ENABLED, true);

        String time = prefs.getString(SettingsFragment.KEY_AUTO_LOCK_TIME, "1");
        int autoLockMins = isAutoLocked ? Integer.parseInt(time) : 60 * 24 * 365;
        return autoLockMins * 60 * 1000;
    }

    public void setSkippedOnboarding(boolean skipped)
    {
        prefs.edit().putBoolean(KEY_ONBOARDING_SKIPPED, skipped).apply();
    }

    public boolean skippedOnboarding()
    {
        return prefs.getBoolean(KEY_ONBOARDING_SKIPPED, false);
    }

    /**
     * Method to set if onboarding is completed. This preference is used within Skin.SplashActivity
     * to see if we should show onboarding or proceed to MainActivity.
     *
     * @param complete true if onboading is complete
     */
    public void setOnboardingComplete(boolean complete)
    {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply();
        if(complete) prefs.edit().putLong(KEY_ONBOARDING_COMPLETE_TIME, new Date().getTime());
        else prefs.edit().putLong(KEY_ONBOARDING_COMPLETE_TIME, 0);
    }

    /**
     * @return true if onboading is complete
     */
    public boolean isOnboardingComplete()
    {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    /**
     * @return the time when the onboarding was completed
     */
    public Date getOnboardingCompleteTime()
    {
        return new Date(prefs.getLong(KEY_ONBOARDING_COMPLETE_TIME, 0));
    }

    /**
     * Method to set if email is verified.
     *
     * @param verified true if email is verified
     */
    public void setEmailVerfied(boolean verified)
    {
        prefs.edit().putBoolean(KEY_EMAIL_VERIFIED, verified).apply();
        if(verified) prefs.edit().putLong(KEY_EMAIL_VERIFIED_TIME, new Date().getTime());
        else prefs.edit().putLong(KEY_EMAIL_VERIFIED_TIME, 0);
    }

    /**
     * @return true if email has been verified
     */
    public boolean isEmailVerified()
    {
        return prefs.getBoolean(KEY_EMAIL_VERIFIED, false);
    }

    /**
     * @return the time when the email was verified
     */
    public Date getEmailVerifiedTime()
    {
        return new Date(prefs.getLong(KEY_EMAIL_VERIFIED_TIME, 0));
    }

    public void setTaskReminderComplete(boolean enabled)
    {
        prefs.edit().putBoolean(SettingsFragment.KEY_REMINDERS, enabled).apply();
    }

    public boolean isTaskReminderEnabled()
    {
        return prefs.getBoolean(SettingsFragment.KEY_REMINDERS, false);
    }
}