package org.researchstack.backbone.utils;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatHelper
{

    private FormatHelper() {}

    public static final int NONE = - 1;

    public static final String           DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"; //Java 7 and on, see http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
    public static final SimpleDateFormat DEFAULT_FORMAT       = new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601,
            Locale.getDefault());

    public static final String           DATE_FORMAT_SIMPLE_DATE = "yyyy-MM-dd";
    public static final SimpleDateFormat SIMPLE_FORMAT_DATE      = new SimpleDateFormat(
            DATE_FORMAT_SIMPLE_DATE,
            Locale.getDefault());

    /**
     * Helper method to return a formatter suitable for
     * {@link ConsentSignatureStepLayout}
     *
     * @return DateFormat that is a DateInstance (only formats y, m, and d attributes)
     */
    public static DateFormat getSignatureFormat()
    {
        return getFormat(DateFormat.SHORT, NONE);
    }

    /**
     * Returns a DateFormat object based on the dateStyle and timeStyle params
     *
     * @param dateStyle style for the date defined by static constants within {@link DateFormat}
     * @param timeStyle style for the time defined by static constants within {@link DateFormat}
     * @return DateFormat object
     */
    public static DateFormat getFormat(int dateStyle, int timeStyle)
    {
        // Date & Time format
        if(isStyle(dateStyle) && isStyle(timeStyle))
        {
            return DateFormat.getDateTimeInstance(dateStyle, timeStyle);
        }

        // Date format
        else if(isStyle(dateStyle) && ! isStyle(timeStyle))
        {
            return DateFormat.getDateInstance(dateStyle);
        }

        // Time format
        else if(! isStyle(dateStyle) && isStyle(timeStyle))
        {
            return DateFormat.getTimeInstance(timeStyle);
        }

        // Else crash since the styles are invalid
        else
        {
            throw new IllegalArgumentException("dateStyle and timeStyle cannot both be ");
        }
    }

    public static boolean isStyle(int style)
    {
        return style >= DateFormat.FULL && style <= DateFormat.SHORT;
    }

}
