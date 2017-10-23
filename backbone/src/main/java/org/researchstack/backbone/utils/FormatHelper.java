package org.researchstack.backbone.utils;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatHelper {

    private static final String COUNTRY_US      = "US";
    private static final String COUNTRY_LIBERIA = "LR";
    private static final String COUNTRY_BURMA   = "MM";

    private static final double FEET_PER_METER = 3.28084;

    public static final int NONE = -1;
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601,
            Locale.getDefault());
    public static final String DATE_FORMAT_SIMPLE_DATE = "yyyy-MM-dd";
    public static final SimpleDateFormat SIMPLE_FORMAT_DATE = new SimpleDateFormat(
            DATE_FORMAT_SIMPLE_DATE,
            Locale.getDefault());
    private FormatHelper() {
    }

    /**
     * Helper method to return a formatter suitable for
     * {@link ConsentSignatureStepLayout}
     *
     * @return DateFormat that is a DateInstance (only formats y, m, and d attributes)
     */
    public static DateFormat getSignatureFormat() {
        return getFormat(DateFormat.SHORT, NONE);
    }

    /**
     * Returns a DateFormat object based on the dateStyle and timeStyle params
     *
     * @param dateStyle style for the date defined by static constants within {@link DateFormat}
     * @param timeStyle style for the time defined by static constants within {@link DateFormat}
     * @return DateFormat object
     */
    public static DateFormat getFormat(int dateStyle, int timeStyle) {
        // Date & Time format
        if (isStyle(dateStyle) && isStyle(timeStyle)) {
            return DateFormat.getDateTimeInstance(dateStyle, timeStyle);
        }

        // Date format
        else if (isStyle(dateStyle) && !isStyle(timeStyle)) {
            return DateFormat.getDateInstance(dateStyle);
        }

        // Time format
        else if (!isStyle(dateStyle) && isStyle(timeStyle)) {
            return DateFormat.getTimeInstance(timeStyle);
        }

        // Else crash since the styles are invalid
        else {
            throw new IllegalArgumentException("dateStyle and timeStyle cannot both be ");
        }
    }

    public static boolean isStyle(int style) {
        return style >= DateFormat.FULL && style <= DateFormat.SHORT;
    }

    public static String localizeDistance(Context context, double distanceInMeters, Locale currentLocale) {
        String countryCode = currentLocale.getCountry();
        switch (countryCode) {
            case COUNTRY_US:
            case COUNTRY_LIBERIA:
            case COUNTRY_BURMA: // in feet
                return String.format(currentLocale, "%.01f %s", (distanceInMeters * FEET_PER_METER),
                        context.getString(R.string.rsb_distance_feet));
            default:   // in meters
                return String.format(currentLocale, "%.01f %s", distanceInMeters,
                        context.getString(R.string.rsb_distance_meters));
        }
    }
}
