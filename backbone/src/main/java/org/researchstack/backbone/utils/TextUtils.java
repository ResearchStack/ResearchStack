package org.researchstack.backbone.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import static android.content.Context.WINDOW_SERVICE;

public class TextUtils {
    private static final String LOG_TAG = TextUtils.class.getCanonicalName();

    public static final Pattern EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");

    private TextUtils() {
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the char sequence is a valid email address
     *
     * @param text the email address to be validated
     * @return a boolean indicating whether the email is valid
     */
    public static boolean isValidEmail(CharSequence text) {
        return !isEmpty(text) && EMAIL_ADDRESS.matcher(text).matches();
    }


    public static class AlphabeticFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetter(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }

    public static class NumericFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }

    public static class AlphanumericFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    }

    public static String urlEncode(String input) {
        String output = null;
        try
        {
            output = URLEncoder.encode(input, "UTF-8");
            return output;
        }
        catch(UnsupportedEncodingException uee)
        {
            LogExt.i(TextUtils.class, "Failed to url encode: " + uee.getMessage());
        }
        return input;
    }

        public static void adjustFontScale(Configuration configuration, Context context, float maxFontScale) {
        if (configuration.fontScale > maxFontScale) {
            Log.w(LOG_TAG, "fontScale=" + configuration.fontScale); //Custom Log class, you can use Log.w
            Log.w(LOG_TAG, "font too big. scale down..."); //Custom Log class, you can use Log.w
            configuration.fontScale = (float) maxFontScale;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }
}
