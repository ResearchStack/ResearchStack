package co.touchlab.researchstack.core.utils;
import java.text.DateFormat;
import java.util.Date;

public class FormatUtils
{
    public static final int NONE = -1;

    // TODO find a better place for this, maybe only use it for Bridge
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Helper method to help format {@link Date} objects used in
     * {@link co.touchlab.researchstack.core.ui.scene.ConsentReviewSignatureScene}
     *
     * @param date Date object to be formatted
     * @return Returns a string representing the date passed in formatted as "M/d/yyyy"
     */
    public static String formatSignature(Date date)
    {
        return format(date, DateFormat.SHORT, NONE);
    }

    /**
     * Formats a {@link Date} object based on style params. This method can return an empty
     * string if both styles params have a value {@link #NONE}
     *
     * @param date Date object to be formatted
     * @param dateStyle style for the date defined by static constants within {@link DateFormat}
     * @param timeStyle style for the time defined by static constants within {@link DateFormat}
     * @return formatted string, delimited by a space if date and time formats are not null
     */
    public static String format(Date date, int dateStyle, int timeStyle)
    {
        // Date & Time format
        if (isStyle(dateStyle) && isStyle(timeStyle))
        {
            return DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(date);
        }

        // Date format
        else if (isStyle(dateStyle) && !isStyle(timeStyle))
        {
            return DateFormat.getDateInstance(dateStyle).format(date);
        }

        // Time format
        else if (!isStyle(dateStyle) && isStyle(timeStyle))
        {
            return DateFormat.getTimeInstance(timeStyle).format(date);
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
