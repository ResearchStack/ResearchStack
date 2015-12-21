package co.touchlab.researchstack.core.utils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils
{
    private static final int STYLE_NONE = 0;  // None
    private static final int STYLE_SHORT = 1; // “11/23/37” or “3:30 PM”
    private static final int STYLE_MEDIUM = 2; // “Nov 23, 1937” or “3:30:32 PM”
    private static final int STYLE_LONG = 3; // “November 23, 1937” or “3:30:32 PM PST”
    private static final int STYLE_FULL = 4; // “Tuesday, April 12, 1952 AD” or “3:30:42 PM Pacific

    public static String formatSignature(Date date)
    {
        String format = StringUtils.join(" ", getDateStyle(STYLE_SHORT), getTimeStyle(STYLE_NONE));
        return new SimpleDateFormat(format).format(date);
    }

    public static String getDateStyle(int style)
    {
        switch(style)
        {
            case STYLE_NONE:
                return "";
            case STYLE_SHORT:
                return "M/d/yyyy";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getTimeStyle(int style)
    {
        switch(style)
        {
            case STYLE_NONE:
                return "";
            case STYLE_SHORT:
                return "h:mm aaa";
            default:
                throw new UnsupportedOperationException();
        }
    }
}
