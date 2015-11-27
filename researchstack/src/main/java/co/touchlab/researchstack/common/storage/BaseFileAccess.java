package co.touchlab.researchstack.common.storage;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.touchlab.researchstack.utils.UiThreadContext;

/**
 * Created by kgalligan on 11/25/15.
 */

public abstract class BaseFileAccess implements FileAccess
{
    private List<FileAccessListener> listeners = Collections.synchronizedList(new ArrayList<>());

    @Override @MainThread
    public final void register(FileAccessListener fileAccessListener)
    {
        UiThreadContext.assertUiThread();
        if(listeners.contains(fileAccessListener))
            throw new FileAccessException("Listener already registered");

        listeners.add(fileAccessListener);
    }

    @Override @MainThread
    public final void unregister(FileAccessListener fileAccessListener)
    {
        UiThreadContext.assertUiThread();
        listeners.remove(fileAccessListener);
    }

    @MainThread
    public void notifyListenersReady()
    {
        UiThreadContext.assertUiThread();
        //TODO: replace with lambda. Hey, if we're using them...
        for(FileAccessListener listener : listeners)
        {
            listener.dataReady();
        }
    }

    @MainThread
    public void notifyListenersFailed()
    {
        UiThreadContext.assertUiThread();
        //TODO: replace with lambda. Hey, if we're using them...
        for(FileAccessListener listener : listeners)
        {
            listener.dataAccessError();
        }
    }

    @Override @WorkerThread
    public void writeString(Context context, String path, String data)
    {
        UiThreadContext.assertBackgroundThread();
        try
        {
            writeData(context, path, data.getBytes("UTF8"));
        }
        catch(UnsupportedEncodingException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override @WorkerThread
    public String readString(Context context, String path)
    {
        UiThreadContext.assertBackgroundThread();
        try
        {
            return new String(readData(context, path), "UTF8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new FileAccessException(e);
        }
    }

    public void checkPath(String path)
    {
        if(!path.startsWith("/"))
            throw new FileAccessException("Path must be absolute (ie start with '/')");
    }

    protected void notifyReady()
    {
        new Handler().post(this :: notifyListenersReady);
    }

    protected void writeSafe(File file, byte[] data)
    {
        try
        {
            File tempFile = makeTempFile(file);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(data);
            fileOutputStream.close();
            tempFile.renameTo(file);
        }
        catch(IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    @NonNull
    private File makeTempFile(File localFile)
    {
        return new File(localFile.getParentFile(), localFile.getName() + ".temp");
    }

    protected byte[] readAll(File file) throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buff = new byte[1024];
        int read;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
        while((read = fileInputStream.read(buff)) > 0)
        {
            byteArrayOutputStream.write(buff, 0, read);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
