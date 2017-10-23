package org.researchstack.backbone.utils;

import android.util.Log;

public class LogExt {

    private LogExt() {
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Info Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void i(Class c, String s) {
        String tag = tagMe(c);
        i(tag, s, null);
    }

    public static void i(String tag, String s) {
        Log.i(tag, s);
    }

    public static void i(Class c, String s, Throwable t) {
        String tag = tagMe(c);
        i(tag, s, t);
    }

    public static void i(String tag, String s, Throwable t) {
        Log.i(tag, s, t);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Error Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void e(Class c, String s) {
        String tag = tagMe(c);
        e(tag, s);
    }

    public static void e(String tag, String s) {
        Log.e(tag, s);
    }

    public static void e(Class c, Throwable t) {
        String tag = tagMe(c);
        e(tag, null, t);
    }

    public static void e(Class c, String s, Throwable t) {
        String tag = tagMe(c);
        e(tag, s, t);
    }

    public static void e(String tag, String s, Throwable t) {
        Log.e(tag, s, t);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Debug Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void d(Class c, String s) {
        String tag = tagMe(c);
        d(tag, s);
    }

    public static void d(String tag, String s) {
        Log.d(tag, s);
    }

    public static void d(Class c, String s, Throwable t) {
        String tag = tagMe(c);
        d(tag, s, t);
    }

    public static void d(String tag, String s, Throwable t) {
        Log.d(tag, s, t);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Warning Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void w(Class c, String s) {
        String tag = tagMe(c);
        w(tag, s);
    }

    public static void w(String tag, String s) {
        Log.w(tag, s);
    }

    public static void w(Class c, String s, Throwable t) {
        String tag = tagMe(c);
        w(tag, s, t);
    }

    public static void w(String tag, String s, Throwable t) {
        Log.w(tag, s, t);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // UA Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void ua(Class c, String s) {
        String tag = tagMe(c);
        Log.d(tag, s);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Helper Methods
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    private static String tagMe(Class c) {
        long threadId = Thread.currentThread().getId();
        String simpleName = c.getSimpleName();

        return simpleName + ":[" + threadId + "]";
    }

}
