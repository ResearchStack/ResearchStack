package co.touchlab.researchstack.core.helpers;

import android.util.Log;

public class LogExt
{
    private static String tagMe(Class c)
    {
        long threadId = Thread.currentThread().getId();
        String simpleName = c.getSimpleName();

        return simpleName + ":[" + threadId + "]";
    }

    public static void i(Class c, String s)
    {
        String tag = tagMe(c);
        Log.i(tag, s);
        crashlytics(Log.INFO, tag, s);
    }

    public static void i(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        Log.i(tag, s, t);
    }

    public static void e(Class c, String s)
    {
        String tag = tagMe(c);
        Log.e(tag, s);
        crashlytics(Log.ERROR, tag, s);
    }

    public static void e(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        Log.e(tag, s, t);
    }

    public static void e(Class c, Throwable t)
    {
        String tag = tagMe(c);
        Log.e(tag, null, t);
    }

    public static void d(Class c, String s)
    {
        String tag = tagMe(c);
        Log.d(tag, s);
        crashlytics(Log.DEBUG, tag, s);
    }

    public static void d(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        Log.d(tag, s, t);
    }

    public static void w(Class c, String s)
    {
        String tag = tagMe(c);
        Log.w(tag, s);
        crashlytics(Log.WARN, tag, s);
    }

    public static void w(Class c, String s, Throwable t)
    {
        String tag = tagMe(c);
        Log.w(tag, s, t);
    }

    public static void ua(Class c, String s)
    {
        String tag = tagMe(c);
        Log.d(tag, s);
        crashlytics(Log.DEBUG, tag, "ua:" + s);
    }

    public static void crashlytics(int priority, String tag, String msg)
    {
        //        if (! BuildConfig.DEBUG)
        //        {
        //            Crashlytics.log(priority, tag, msg);
        //        }
    }

}
