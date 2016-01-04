package co.touchlab.researchstack.core.storage.file;
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

import co.touchlab.researchstack.core.utils.UiThreadContext;


/**
 * Created by kgalligan on 11/25/15.
 */

public abstract class BaseFileAccess implements FileAccess
{
    protected List<FileAccessListener> listeners    = Collections.synchronizedList(
            new ArrayList<>());
    protected boolean                  checkThreads = false;

    @Override
    @MainThread
    public final void register(FileAccessListener fileAccessListener)
    {
        if(checkThreads)
        {
            UiThreadContext.assertUiThread();
        }
        if(listeners.contains(fileAccessListener))
        {
            throw new FileAccessException("Listener already registered");
        }

        listeners.add(fileAccessListener);
    }

    @Override
    @MainThread
    public final void unregister(FileAccessListener fileAccessListener)
    {
        if(checkThreads)
        {
            UiThreadContext.assertUiThread();
        }
        listeners.remove(fileAccessListener);
    }

    @Override
    @WorkerThread
    public void writeString(Context context, String path, String data)
    {
        if(checkThreads)
        {
            UiThreadContext.assertBackgroundThread();
        }
        try
        {
            writeData(context, path, data.getBytes("UTF8"));
        }
        catch(UnsupportedEncodingException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override
    @WorkerThread
    public String readString(Context context, String path)
    {
        if(checkThreads)
        {
            UiThreadContext.assertBackgroundThread();
        }
        try
        {
            return new String(readData(context, path), "UTF8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new FileAccessException(e);
        }
    }

    @MainThread
    public void notifyListenersReady()
    {
        if(checkThreads)
        {
            UiThreadContext.assertUiThread();
        }
        //TODO: replace with lambda. Hey, if we're using them...
        for(FileAccessListener listener : listeners)
        {
            listener.dataReady();
        }
    }

    @MainThread
    public void notifyListenersFailed()
    {
        if(checkThreads)
        {
            UiThreadContext.assertUiThread();
        }
        //TODO: replace with lambda. Hey, if we're using them...
        for(FileAccessListener listener : listeners)
        {
            listener.dataAccessError();
        }
    }

    public void checkPath(String path)
    {
        if(! path.startsWith("/"))
        {
            throw new FileAccessException("Path must be absolute (ie start with '/')");
        }
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
