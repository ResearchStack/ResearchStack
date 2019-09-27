package org.researchstack.backbone.utils;

import android.content.Context;
import java.text.DateFormat;
import java.util.Date;

public class DateUtils {
    /**
     * Reformats a date string into the locale of the device
     *
     * @param selectedDate the date returned by the server
     *
     */
    public static String reformatDateFormLong(Context context, Long selectedDate) {
        String reformattedDate = null;
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, LocaleUtils.getLocaleFromString(LocaleUtils.getPreferredLocale(context)));
        Date date = new Date(selectedDate);
        reformattedDate = df.format(date);
        return reformattedDate;
    }
}
