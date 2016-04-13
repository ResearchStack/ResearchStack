package org.researchstack.backbone.utils;

import java.util.regex.Pattern;

public class TextUtils
{
    public static final Pattern EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str)
    {
        if(str == null || str.length() == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns true if the char sequence is a valid email address
     *
     * @param text the email address to be validated
     * @return a boolean indicating whether the email is valid
     */
    public static boolean isValidEmail(CharSequence text)
    {
        return ! isEmpty(text) && EMAIL_ADDRESS.matcher(text).matches();
    }
}
