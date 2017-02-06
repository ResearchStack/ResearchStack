package org.researchstack.backbone.utils;

import android.content.Context;

import org.researchstack.backbone.StorageAccess;


public class ResUtils {
    /**
     * Since we cannot reference R.drawable.X integer references from JSON
     * The Drawable resource must also be available by String lookup
     */
    public static final String LOGO_DISEASE                 = "logo_disease";
    public static final String TWITTER_ICON                 = "rsb_ic_twitter_icon";
    public static final String FACEBOOK_ICON                = "rsb_ic_facebook_icon";
    public static final String EMAIL_ICON                   = "rsb_ic_email_icon";
    public static final String SMS_ICON                     = "rsb_ic_sms_icon";
    public static final String ERROR_ICON                   = "rsb_error";
    public static final String IC_FINGERPRINT               = "rsb_fingerprint";

    public static final String TREMOR_TEST_1                = "rsb_tremortest1";
    public static final String TREMOR_TEST_2                = "rsb_tremortest2";
    public static final String TREMOR_TEST_3                = "rsb_tremortest3";
    public static final String TREMOR_TEST_4                = "rsb_tremortest4";
    public static final String TREMOR_TEST_5                = "rsb_tremortest5";
    public static final String TREMOR_TEST_6                = "rsb_tremortest6";
    public static final String TREMOR_TEST_7                = "rsb_tremortest7";
    public static final String TREMOR_TEST_1_FLIPPED        = "rsb_tremortest1_flipped";
    public static final String TREMOR_TEST_2_FLIPPED        = "rsb_tremortest2_flipped";
    public static final String TREMOR_TEST_3_FLIPPED        = "rsb_tremortest3_flipped";
    public static final String TREMOR_TEST_4_FLIPPED        = "rsb_tremortest4_flipped";
    public static final String TREMOR_TEST_5_FLIPPED        = "rsb_tremortest5_flipped";
    public static final String TREMOR_TEST_6_FLIPPED        = "rsb_tremortest6_flipped";
    public static final String TREMOR_TEST_7_FLIPPED        = "rsb_tremortest7_flipped";

    // AnimatedVectorDrawable 's
    public static final String ANIMATED_CHECK_MARK_DELAYED  = "rsb_animated_check_delayed";
    public static final String ANIMATED_CHECK_MARK          = "rsb_animated_check";
    public static final String ANIMATED_FINGERPRINT         = "rsb_animated_fingerprint";

    private ResUtils() {
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    /**
     * Should this be here or should {@link StorageAccess} have the
     * ability to write files to SDCard
     *
     * @return of SD-Card storage folder name (used to save and share consent-PDF)
     */
    @Deprecated
    public static String getExternalSDAppFolder() {
        return "demo_researchstack";
    }

    @Deprecated
    public static String getHTMLFilePath(String docName) {
        return getRawFilePath(docName, "html");
    }

    @Deprecated
    public static String getPDFFilePath(String docName) {
        return getRawFilePath(docName, "pdf");
    }

    @Deprecated
    public static String getRawFilePath(String docName, String postfix) {
        return "file:///android_res/raw/" + docName + "." + postfix;
    }

    public static int getColorResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "color", context.getPackageName());
    }

    public static int getDrawableResourceId(Context context, String name) {
        return getDrawableResourceId(context, name, 0);
    }

    public static int getDrawableResourceId(Context context, String name, int defaultResId) {
        if (name == null || name.length() == 0) {
            return defaultResId;
        } else {
            int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            return resId != 0 ? resId : defaultResId;
        }
    }


    public static int getRawResourceId(Context context, String name) {
        return context.getResources().getIdentifier(name, "raw", context.getPackageName());
    }

}
