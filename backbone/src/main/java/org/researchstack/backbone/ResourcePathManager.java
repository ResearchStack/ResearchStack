package org.researchstack.backbone;

import android.app.Application;
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

/**
 * This class is responsible for returning paths of resources defined in the assets folder. You
 * should call {@link #init(ResourcePathManager)} at the start of your application and pass in your
 * implementation of this class. Wihtin there you will have to implement {@link #generatePath(int,
 * String)} method and return the relative path based on what file type it is.
 * <p>
 * The necessity of defining a file type is needed to keep compatibility with assets define in
 * ResearchKit™ applications
 */
public abstract class ResourcePathManager {
    private static Gson gson = new GsonBuilder().setDateFormat("MMM yyyy").create();

    private static ResourcePathManager instance;

    /**
     * Initializes the ResourcePathManager singleton. It is best to call this method inside your
     * {@link Application#onCreate()} method.
     *
     * @param manager an implementation of ResourcePathManager
     */
    public static void init(ResourcePathManager manager) {
        ResourcePathManager.instance = manager;
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static ResourcePathManager getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "ResourceManager instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * Load resource from a file-path and turns contents to a String for consumption
     *
     * @param context  android context
     * @param filePath relative file path
     * @return String representation of the file
     */
    public static String getResourceAsString(Context context, String filePath) {
        return new String(getResourceAsBytes(context, filePath), Charset.forName("UTF-8"));
    }

    /**
     * Load resource from a file-path and turns contents to a byte[] for consumption
     *
     * @param context  android context
     * @param filePath relative file path
     * @return byte [] representation of the asset
     */
    public static byte[] getResourceAsBytes(Context context, String filePath) {
        InputStream is = getResouceAsInputStream(context, filePath);
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        byte[] readBuffer = new byte[4 * 1024];

        try {
            int read;
            do {
                read = is.read(readBuffer, 0, readBuffer.length);
                if (read == -1) {
                    break;
                }
                byteOutput.write(readBuffer, 0, read);
            }
            while (true);

            return byteOutput.toByteArray();
        } catch (IOException e) {
            LogExt.e(ResourcePathManager.class, e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LogExt.e(ResourcePathManager.class, e);
            }
        }
        return null;
    }

    /**
     * Load resource from a file-path and turns contents to a InputStream for consumption
     *
     * @param context  android context
     * @param filePath relative file path
     * @return InputStream representation of the asset
     */
    public static InputStream getResouceAsInputStream(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            return assetManager.open(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load resource from a file-path and turns contents to a objects, of type T, for consumption
     *
     * @param context  android context
     * @param clazz    the class of T
     * @param filePath relative file path
     * @return Class representation of the asset
     */
    public static <T> T getResourceAsClass(Context context, Class<T> clazz, String filePath) {
        InputStream stream = getResouceAsInputStream(context, filePath);
        Reader reader = null;
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return gson.fromJson(reader, clazz);
    }

    /**
     * The genereatePath method is abstract as the Framework does not know what the parent
     * directory of a file is. While it is possible to open a file using only the fileName, there are
     * many instances where we need the path of a file instead (like for WebView). This method is
     * used for that purpose and is used to get the path of a resource when the framework only knows
     * the name of the file and what type of file it is.
     *
     * @param type the type of file. This will be used for appending the file extension.
     * @param name the name of the file
     * @return a path relative to the asset folder
     */
    public abstract String generatePath(int type, String name);

    /**
     * Generates an absolute string file path
     *
     * @param type the type of file. This will be used for appending the file extension.
     * @param name the name of the file
     * @return an absolute path for a file
     */
    public String generateAbsolutePath(int type, String name) {
        return new StringBuilder("file:///android_asset/").append(generatePath(type, name))
                .toString();
    }

    /**
     * @param type the type of file. Supported file types are defined within the {@link Resource}
     *             class. You may override this method in your implementation and implement your own
     *             cases
     * @return file extension of a file type
     */
    public String getFileExtension(int type) {
        switch (type) {
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

    /**
     * Class represents one asset within the assets folder.
     */
    public static class Resource {
        public static final int TYPE_HTML = 0;
        public static final int TYPE_JSON = 1;
        public static final int TYPE_PDF = 2;
        public static final int TYPE_MP4 = 3;

        private final int type;
        private final String dir;
        private final String name;
        private Class clazz;

        /**
         * Initializes this Resource object
         *
         * @param type the type of file of the resource
         * @param dir  the sub directory of the fiel
         * @param name the name of the file (excluding extension)
         */
        public Resource(int type, String dir, String name) {
            this(type, dir, name, null);
        }

        /**
         * Initializes this Resource object
         *
         * @param type  the type of file of the resource
         * @param dir   the dir path of the file
         * @param name  the name of the file (excluding extension)
         * @param clazz the class file that this file is represented as
         */
        public Resource(int type, String dir, String name, Class clazz) {
            this.type = type;
            this.dir = dir;
            this.name = name;
            this.clazz = clazz;
        }

        /**
         * Returns the directroy path of this Resource
         *
         * @return The dir path of the file
         */
        public String getDir() {
            return dir;
        }

        /**
         * Returns the name of this Resource
         *
         * @return the name, excluding extension, of the file
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the file type of this Resource
         *
         * @return The dir path of the file
         */
        public int getType() {
            return type;
        }

        /**
         * Create this Resource into an Object of type T. This method will only work for Json
         * files.
         *
         * @param context android context
         * @return object of type T
         */
        public <T> T create(Context context) {
            String path = getRelativePath();
            return ResourcePathManager.getResourceAsClass(context, (Class<T>) clazz, path);
        }

        /**
         * Returns an inputstream for this Resource
         *
         * @param context android context
         * @return InputStream of the resource
         */
        public InputStream open(Context context) {
            String path = getRelativePath();
            return getResouceAsInputStream(context, path);
        }

        /**
         * Returns the absolute path of this Resource
         *
         * @return the absolute path of this Resource
         */
        public String getAbsolutePath() {
            return new StringBuilder("file:///android_asset/").append(getRelativePath()).toString();
        }

        /**
         * Returns the relative path of this Resource
         *
         * @return the relative path of this Resource
         */
        public String getRelativePath() {
            StringBuilder path = new StringBuilder();
            if (!TextUtils.isEmpty(dir)) {
                path.append(dir).append("/");
            }

            return path.append(name).append(".").append(getFileExtension()).toString();
        }

        /**
         * Returns the file extension of this Resource
         *
         * @return the file extension of this Resource
         */
        public String getFileExtension() {
            return ResourcePathManager.getInstance().getFileExtension(type);
        }
    }

}
