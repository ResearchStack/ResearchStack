package org.researchstack.backbone;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.utils.LogExt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public abstract class ResourcePathManager
{
    static Gson gson = new GsonBuilder().setDateFormat("MMM yyyy").create();

    private static ResourcePathManager instance;

    public static void init(ResourcePathManager manager)
    {
        ResourcePathManager.instance = manager;
    }

    public static ResourcePathManager getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "ResourceManager instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public abstract String generatePath(int type, String name);

    public String generateAbsolutePath(int type, String name)
    {
        return new StringBuilder("file:///android_asset/").append(generatePath(type, name))
                .toString();
    }

    public static class Resource
    {
        public static final int TYPE_HTML = 0;
        public static final int TYPE_JSON = 1;
        public static final int TYPE_PDF = 2;
        public static final int TYPE_MP4 = 3;

        private final int type;
        private final String dir;
        private final String name;
        private Class clazz;

        public Resource(int type, String dir, String name)
        {
            this(type, dir, name, null);
        }

        public Resource(int type, String dir, String name, Class clazz)
        {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.clazz = clazz;
        }

        public String getDir()
        {
            return dir;
        }

        public String getName()
        {
            return name;
        }

        public int getType()
        {
            return type;
        }

        public <T> T create(Context context)
        {
            String path = getRelativePath();
            return ResourcePathManager.getResourceAsClass(context, (Class<T>) clazz, path);
        }

        public InputStream open(Context context)
        {
            String path = getRelativePath();
            return getResouceAsInputStream(context, path);
        }

        public String getAbsolutePath()
        {
            return new StringBuilder("file:///android_asset/").append(getRelativePath())
                    .toString();
        }

        public String getRelativePath()
        {
            StringBuilder path = new StringBuilder();
            if (! TextUtils.isEmpty(dir))
            {
                path.append(dir).append("/");
            }

            return path.append(name)
                    .append(".")
                    .append(getFileExtension())
                    .toString();
        }

        public String getFileExtension()
        {
            return ResourcePathManager.getInstance().getFileExtension(type);
        }
    }

    public String getFileExtension(int type)
    {
        switch(type)
        {
            case Resource.TYPE_HTML:
                return "html";
            case Resource.TYPE_JSON:
                return "json";
            case Resource.TYPE_PDF:
                return "pdf";
            case Resource.TYPE_MP4:
                return "mp4";
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }
    }

    public static String getResourceAsString(Context context, String filePath)
    {
        return new String(getResourceAsBytes(context, filePath), Charset.forName("UTF-8"));
    }

    public static byte[] getResourceAsBytes(Context context, String filePath)
    {
        InputStream is = getResouceAsInputStream(context, filePath);
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
            LogExt.e(ResourcePathManager.class, e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                LogExt.e(ResourcePathManager.class, e);
            }
        }
        return null;
    }

    public static InputStream getResouceAsInputStream(Context context, String filePath)
    {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try
        {
            return assetManager.open(filePath);
        }
        catch(IOException e)
        {
           throw new RuntimeException(e);
        }
    }

    public static <T> T getResourceAsClass(Context context, Class<T> clazz, String filePath)
    {
        InputStream stream = getResouceAsInputStream(context, filePath);
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
