package org.researchstack.backbone.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class LocaleUtils {

    public static final String LOCALE_PREFERENCES = "LocalePreferences";
    public static final String PREFERRED_LOCALE_FIELD = "PreferredLocale";

    public static ContextWrapper wrapLocaleContext(Context context, Locale locale) {
        Configuration config = context.getResources().getConfiguration();
        Locale.setDefault(locale);
        config.setLocale(locale);
        Context newContext = context.createConfigurationContext(config);
        return new ContextWrapper(newContext);
    }

    public static Locale getLocaleFromString(String localeString)
    {
        String[] parts;
        if (localeString.contains("_")) {
            parts = localeString.split("_");
        } else if (localeString.contains("-")) {
            parts = localeString.split("-");
        } else {
            parts = null;
        }

        if (parts != null && parts.length > 1) {
            return new Locale(parts[0], parts[1]);
        } else {
            return Locale.getDefault();
        }
    }

    public static String getLocalizedString(Context context, int stringId, Object... args) {
        String preferredLocale = getPreferredLocale(context);
        if (preferredLocale == null || context.getResources() == null
                || context.getResources().getConfiguration() == null) {
            return context.getString(stringId, args);
        }
        Locale locale = getLocaleFromString(preferredLocale);
        context.getResources().getConfiguration().setLocale(locale);
        return String.format(locale, context.createConfigurationContext(context.getResources().getConfiguration()).getResources().getString(stringId), args);
    }


    public static void setPreferredLocale(Context context, String newLocale) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOCALE_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PREFERRED_LOCALE_FIELD, newLocale);
        editor.apply();
    }

    public static String getPreferredLocale(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOCALE_PREFERENCES, MODE_PRIVATE);
        return sharedPrefs.getString(PREFERRED_LOCALE_FIELD, null);
    }
}
