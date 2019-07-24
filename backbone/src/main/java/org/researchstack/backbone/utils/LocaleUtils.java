package org.researchstack.backbone.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleUtils {

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

}
