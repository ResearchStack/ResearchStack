package org.researchstack.backbone.utils;

import android.content.Context;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.helpers.LogExt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by bradleymcdermott on 12/2/15.
 */
public class ResUtils
{
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public static String getApplicationName(Context context)
    {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    /**
     * TODO Determine if method should exists in class
     * Should this be here or should {@link StorageAccess} have the
     * ability to write files to SDCard
     *
     * @return of SD-Card storage folder name (used to save and share consent-PDF)
     */
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
        return getDrawableResourceId(context, name, 0);
    }

    public static int getDrawableResourceId(Context context, String name, int defaultResId)
    {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return resId != 0 ? resId : defaultResId ;
    }

    public static int getRawResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "raw", context.getPackageName());
    }

    public static byte[] getResource(Context context, int id)
    {
        InputStream is = context.getResources().openRawResource(id);
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        byte[] readBuffer = new byte[4 * 1024];

        try
        {
            int read;
            do
            {
                read = is.read(readBuffer, 0, readBuffer.length);
                if(read == - 1)
                {
                    break;
                }
                byteOutput.write(readBuffer, 0, read);
            }
            while(true);

            return byteOutput.toByteArray();
        }
        catch(IOException e)
        {
            LogExt.e(ResUtils.class, e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                LogExt.e(ResUtils.class, e);
            }
        }
        return null;
    }

    public static String getStringResource(Context ctx, int id)
    {
        return new String(getResource(ctx, id), Charset.forName("UTF-8"));
    }
}
