package org.researchstack.backbone.utils;

import android.util.Pair;

import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public final class FormatHelper {
    public static final int NONE = -1;

    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * DateFormat is not thread safe.
     *
     * @deprecated use {@link #getDefaultFormat()} ()} instead.
     */
    @Deprecated
    public static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601,
            Locale.getDefault());

    public static SimpleDateFormat getDefaultFormat() {
        return DEFAULT_FORMAT_THREAD_LOCAL.get();
    }

    private static final ThreadLocal<SimpleDateFormat> DEFAULT_FORMAT_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601, Locale.getDefault());
        }
    };

    public static final String DATE_FORMAT_SIMPLE_DATE = "yyyy-MM-dd";

    /**
     * DateFormat is not thread safe.
     *
     * @deprecated use {@link #getDefaultDateFormat()} ()} instead.
     */
    @Deprecated
    public static final SimpleDateFormat SIMPLE_FORMAT_DATE = new SimpleDateFormat(
            DATE_FORMAT_SIMPLE_DATE,
            Locale.getDefault());

    public static SimpleDateFormat getDefaultDateFormat() {
        return SIMPLE_DATE_FORMAT_THREAD_LOCAL.get();
    }

    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat(
                            DATE_FORMAT_SIMPLE_DATE,
                            Locale.getDefault());
                }
            };

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
        Pair<Integer, Integer> style = new Pair<>(dateStyle, timeStyle);

        DateFormat format = DATE_FORMATS.get().get(style);
        if (format != null) {
            return format;
        }
        format = getFormat(style);
        DATE_FORMATS.get().put(style, format);
        return format;
    }

    private static final ThreadLocal<Map<Pair<Integer, Integer>, DateFormat>> DATE_FORMATS = new ThreadLocal() {
        @Override
        protected Map initialValue() {
            return null;
        }
    };

    private static DateFormat getFormat(Pair<Integer, Integer> style) {
        int dateStyle = style.first;
        int timeStyle = style.second;

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

    // prevent instatiation of util class
    private FormatHelper() {
    }

}
