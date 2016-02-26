package co.touchlab.researchstack.skin;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.touchlab.researchstack.skin.ui.fragment.SettingsFragment;

public class AppPrefs
{
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Statics
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
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
     * @return time in milliseconds
     */
    public long getAutoLockTime()
    {
        boolean isAutoLocked = prefs.getBoolean(SettingsFragment.KEY_AUTO_LOCK_ENABLED, true);

        String time = prefs.getString(SettingsFragment.KEY_AUTO_LOCK_TIME, "1");
        int autoLockMins = isAutoLocked ? Integer.parseInt(time) : 60 * 24 * 365;
        return autoLockMins * 60 * 1000;
    }

}