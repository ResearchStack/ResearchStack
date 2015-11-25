package co.touchlab.researchstack;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kgalligan on 11/24/15.
 */
public class AppPrefs
{
    private static AppPrefs          instance;
    private final  SharedPreferences prefs;

    //Flag indicates user has entered a pin, and we can attempt to re-auth access
    public static final String APP_PIN_ENCODED = "APP_PIN_ENCODED";

    public static synchronized AppPrefs getInstance(Context context)
    {
        if(instance == null)
        {
            instance = new AppPrefs(context);
        } return instance;
    }


    AppPrefs(Context context)
    {
        prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
    }

    public boolean isAppPinEncoded()
    {
        return prefs.getBoolean(APP_PIN_ENCODED, false);
    }

    public void setAppPinEncoded(boolean b)
    {
        prefs.edit().putBoolean(APP_PIN_ENCODED, b).apply();
    }
}