package org.researchstack.backbone.utils;

import android.content.Context;

import org.researchstack.backbone.StorageAccess;


public class ResUtils {

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
