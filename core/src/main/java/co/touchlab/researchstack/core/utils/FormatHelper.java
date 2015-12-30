package co.touchlab.researchstack.core.utils;
import java.text.DateFormat;

import co.touchlab.researchstack.core.ui.step.layout.ConsentSignatureStepLayout;

public class FormatHelper
{
    public static final int NONE = - 1;

    // TODO find a better place for this, maybe only use it for Bridge
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Helper method to return a formatter suitable for
     * {@link ConsentSignatureStepLayout}
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
        if (isStyle(dateStyle) && isStyle(timeStyle))
        {
            return DateFormat.getDateTimeInstance(dateStyle, timeStyle);
        }

        // Date format
        else if (isStyle(dateStyle) && !isStyle(timeStyle))
        {
            return DateFormat.getDateInstance(dateStyle);
        }

        // Time format
        else if (!isStyle(dateStyle) && isStyle(timeStyle))
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
