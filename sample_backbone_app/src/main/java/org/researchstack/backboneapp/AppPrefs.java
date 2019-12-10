package org.researchstack.backboneapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class AppPrefs
{
    public static final String HAS_CONSENTED     = "HAS_CONSENTED";
    public static final String CONSENT_NAME      = "CONSENT_NAME";
    public static final String CONSENT_SIGNATURE = "CONSENT_SIGNATURE";
    public static final String HAS_SURVEYED      = "HAS_SURVEYED";
    public static final String SURVEY_RESULT     = "SURVEY_RESULT";

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

    public boolean hasConsented()
    {
        return prefs.getBoolean(HAS_CONSENTED, false);
    }

    public void setHasConsented(boolean consented)
    {
        prefs.edit().putBoolean(HAS_CONSENTED, consented).apply();
    }

    public boolean hasSurveyed()
    {
        return prefs.getBoolean(HAS_SURVEYED, false);
    }

    public void setHasSurveyed(boolean surveyed)
    {
        prefs.edit().putBoolean(HAS_SURVEYED, surveyed).apply();
    }
}