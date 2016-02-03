package co.touchlab.researchstack.skin;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by kgalligan on 11/24/15.
 */
public class AppPrefs
{

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Flags
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    //Flag indicates user has entered a pin, and we can attempt to re-auth access
    public static final String APP_PIN_ENCODED = "APP_PIN_ENCODED";

    //TODO Desc
    public static final String AUTO_LOCK_TIME = "AUTO_LOCK_TIME";

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
     * @return time in milliseconds
     */
    public long getAutoLockTime()
    {
        int autoLockMins = prefs.getInt(AUTO_LOCK_TIME, 1);
        return autoLockMins * 60 * 1000;
    }

}