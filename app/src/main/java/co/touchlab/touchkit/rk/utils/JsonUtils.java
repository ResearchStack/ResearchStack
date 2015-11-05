package co.touchlab.touchkit.rk.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Created by bradleymcdermott on 11/4/15.
 */
public class JsonUtils
{
    static Gson gson = new Gson();

    public static <T> T loadClassFromJson(Context context, Class<T> clazz, String filename)
    {
        int rawFileId = context.getResources()
                .getIdentifier("raw/" + filename,
                        "raw",
                        context.getPackageName());
        return loadClassFromJson(context, clazz, rawFileId);
    }

    public static <T> T loadClassFromJson(Context context, Class<T> clazz, int id)
    {
        Gson gson = new GsonBuilder().setDateFormat("MMM yyyy").create();
        InputStream stream = context.getResources().openRawResource(id);
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(stream, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw  new RuntimeException(e);
        }

        return gson.fromJson(reader,
                clazz);
    }
}
