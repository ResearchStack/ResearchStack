package co.touchlab.researchstack.backbone.helpers;

import android.util.Log;

public class LogExt
{

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Info Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void i(Class c, String s)
    {
        String tag = tagMe(c);
        i(tag, s, null);
    }

    public static void i(String tag, String s)
    {
        Log.i(tag, s);
        crashlytics(Log.INFO, tag, s);
    }

    public static void i(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        i(tag, s, t);
    }

    public static void i(String tag, String s, Throwable t)
    {
        Log.i(tag, s, t);
        crashlytics(Log.INFO, tag, s);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Error Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void e(Class c, String s)
    {
        String tag = tagMe(c);
        e(tag, s);
    }

    public static void e(String tag, String s)
    {
        Log.e(tag, s);
        crashlytics(Log.ERROR, tag, s);
    }

    public static void e(Class c, Throwable t)
    {
        String tag = tagMe(c);
        e(tag, null, t);
    }

    public static void e(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        e(tag, s, t);
    }

    public static void e(String tag, String s, Throwable t)
    {
        Log.e(tag, s, t);
        crashlytics(Log.ERROR, tag, s);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Debug Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void d(Class c, String s)
    {
        String tag = tagMe(c);
        d(tag, s);
    }

    public static void d(String tag, String s)
    {
        Log.d(tag, s);
        crashlytics(Log.DEBUG, tag, s);
    }

    public static void d(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        d(tag, s, t);
    }

    public static void d(String tag, String s, Throwable t)
    {
        Log.d(tag, s, t);
        crashlytics(Log.DEBUG, tag, s);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Warning Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void w(Class c, String s)
    {
        String tag = tagMe(c);
        w(tag, s);
    }

    public static void w(String tag, String s)
    {
        Log.w(tag, s);
        crashlytics(Log.WARN, tag, s);
    }

    public static void w(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        w(tag, s, t);
    }

    public static void w(String tag, String s, Throwable t)
    {
        Log.w(tag, s, t);
        crashlytics(Log.WARN, tag, s);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // UA Logging
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void ua(Class c, String s)
    {
        String tag = tagMe(c);
        Log.d(tag, s);
        crashlytics(Log.DEBUG, tag, "ua:" + s);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Helper Methods
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static void crashlytics(int priority, String tag, String msg)
    {
        //        TODO Enable Crash logging in crashlytics
        //        if (! BuildConfig.DEBUG)
        //        {
        //            Crashlytics.log(priority, tag, msg);
        //        }
    }

    private static String tagMe(Class c)
    {
        long threadId = Thread.currentThread().getId();
        String simpleName = c.getSimpleName();

        return simpleName + ":[" + threadId + "]";
    }

}
