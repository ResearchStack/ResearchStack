package co.touchlab.researchstack.core.utils;

import android.content.Context;

/**
 * Created by bradleymcdermott on 12/2/15.
 */
public class ResUtils
{
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static String getExternalSDAppFolder()
    {
        return "demo_researchstack";
    }

    public static String getHTMLFilePath(String docName)
    {
        return getRawFilePath(docName, "html");
    }

    public static String getPDFFilePath(String docName)
    {
        return getRawFilePath(docName, "pdf");
    }

    public static String getRawFilePath(String docName, String postfix)
    {
        return "file:///android_res/raw/" + docName + "." + postfix;
    }

    public static int getDrawableResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
