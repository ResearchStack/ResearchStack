package co.touchlab.researchstack.core.utils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils
{
    /**
     * This constant is meant for ignoring a specific portion of the format. Passing constant value
     * {@link #STYLE_NONE} through {@link #getDateStyle(int)} or {@link #getTimeStyle(int)} will
     * return null
     */
    public static final int STYLE_NONE = 0;

    /**
     * Medium style format. Results should look like the following (using Locale.US)
     * Date Example: 11/23/37
     * Time Example: 3:30 PM
     */
    public static final int STYLE_SHORT = 1;

    /**
     * Medium style format. Results should look like the following (using Locale.US)
     * Date Example: Nov 23, 1937
     * Time Example: 3:30:32 PM
     */
    public static final int STYLE_MEDIUM = 2;

    /**
     * Medium style format. Results should look like the following (using Locale.US)
     * Date Example: November 23, 1937
     * Time Example: 3:30:32 PM PST
     */
    public static final int STYLE_LONG = 3;

    /**
     * Medium style format. Results should look like the following (using Locale.US)
     * Date Example: Tuesday, April 12, 1952 AD
     * Time Example: 3:30:42 PM Pacific Standard Time
     */
    public static final int STYLE_FULL = 4;

    /**
     * Helper method to help format {@link Date} objects used in
     * {@link co.touchlab.researchstack.core.ui.scene.ConsentReviewSignatureScene}
     *
     * @param date Date object to be formatted
     * @return Returns a string representing the date passed in formatted as "M/d/yyyy"
     */
    public static String formatSignature(Date date)
    {
        return format(date, STYLE_SHORT, STYLE_NONE);
    }

    /**
     * Formats a {@link Date} object based on style params. This method can return an empty
     * string if both styles params have a value {@link #STYLE_NONE}
     *
     * @param date Date object to be formatted
     * @param dateStyle style for the date defined by static constants within {@link FormatUtils}
     * @param timeStyle style for the time defined by static constants within {@link FormatUtils}
     * @return formatted string, delimited by a space if date and time formats are not null
     */
    public static String format(Date date, int dateStyle, int timeStyle)
    {
        String [] parts = new String[] {
                getDateStyle(dateStyle),
                getTimeStyle(timeStyle)
        };

        String dateTimeFormat = StringUtils.join(parts, " ").trim();

        return new SimpleDateFormat(dateTimeFormat).format(date);
    }

    /**
     * Returns a date pattern
     *
     * @param style a static constants defined within {@link FormatUtils}
     * @return date pattern
     */
    public static String getDateStyle(int style)
    {
        switch(style)
        {
            case STYLE_NONE:
                return null;
            case STYLE_SHORT:
                return "M/d/yyyy";
            case STYLE_MEDIUM:
                return "MMM d, yyyy";
            case STYLE_LONG:
                return "MMMMM d, yyyy";
            case STYLE_FULL:
                return "EEEEE, MMMMM d, yyyy GG";
            default:
                throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns a time pattern
     *
     * @param style a static constant defined within {@link FormatUtils}
     * @return time pattern
     */
    public static String getTimeStyle(int style)
    {
        switch(style)
        {
            case STYLE_NONE:
                return null;
            case STYLE_SHORT:
                return "h:mm aaa";
            case STYLE_MEDIUM:
                return "h:mm:ss aaa";
            case STYLE_LONG:
                return "h:mm:ss aaa zzz";
            case STYLE_FULL:
                return "h:mm:ss aaa zzzz";
            default:
                throw new UnsupportedOperationException();
        }
    }
}
