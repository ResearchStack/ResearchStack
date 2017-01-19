package org.researchstack.skin;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.researchstack.skin.ui.fragment.SettingsFragment;

public class AppPrefs {
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Statics
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private static final String KEY_ONBOARDING_COMPLETE = "settings_onboarding_complete";
    private static final String KEY_ONBOARDING_SKIPPED = "settings_onboarding_skipped";
    private static AppPrefs instance;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Field Vars
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private final SharedPreferences prefs;

    AppPrefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized AppPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new AppPrefs(context);
        }
        return instance;
    }

    /**
     * If auto lock is disabled, default the time to a year.
     *
     * @return time in milliseconds
     */
    public long getAutoLockTime() {
        boolean isAutoLocked = prefs.getBoolean(SettingsFragment.KEY_AUTO_LOCK_ENABLED, true);

        String time = prefs.getString(SettingsFragment.KEY_AUTO_LOCK_TIME, "1");
        int autoLockMins = isAutoLocked ? Integer.parseInt(time) : 60 * 24 * 365;
        return autoLockMins * 60 * 1000;
    }

    public void setSkippedOnboarding(boolean skipped) {
        prefs.edit().putBoolean(KEY_ONBOARDING_SKIPPED, skipped).apply();
    }

    public boolean skippedOnboarding() {
        return prefs.getBoolean(KEY_ONBOARDING_SKIPPED, false);
    }

    /**
     * @return true if onboading is complete
     */
    public boolean isOnboardingComplete() {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false);
    }

    /**
     * Method to set if onboarding is completed. This preference is used within Skin.SplashActivity
     * to see if we should show onboarding or proceed to MainActivity.
     *
     * @param complete true if onboading is complete
     */
    public void setOnboardingComplete(boolean complete) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply();
    }

    public void setTaskReminderComplete(boolean enabled) {
        prefs.edit().putBoolean(SettingsFragment.KEY_REMINDERS, enabled).apply();
    }

    public boolean isTaskReminderEnabled() {
        return prefs.getBoolean(SettingsFragment.KEY_REMINDERS, false);
    }
}