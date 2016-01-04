package co.touchlab.researchstack.glue.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import co.touchlab.researchstack.core.utils.ResUtils;

/**
 * Created by bradleymcdermott on 11/4/15.
 */
public class JsonUtils
{
    static Gson gson = new GsonBuilder().setDateFormat("MMM yyyy")
                                        .create();

    public static <T> T loadClass(Context context, Class<T> clazz, String filename)
    {
        //TODO /raw prefix needed?
        int rawFileId = ResUtils.getRawResourceId(context, "raw/" + filename);
        return loadClass(context, clazz, rawFileId);
    }

    public static <T> T loadClass(Context context, Class<T> clazz, int id)
    {
        InputStream stream = context.getResources()
                                    .openRawResource(id);
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(stream, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }

        return gson.fromJson(reader, clazz);
    }
}
