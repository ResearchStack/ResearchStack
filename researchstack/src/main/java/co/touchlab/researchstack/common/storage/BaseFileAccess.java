package co.touchlab.researchstack.common.storage;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kgalligan on 11/25/15.
 */

public abstract class BaseFileAccess implements FileAccess
{
    private List<FileAccessListener> listeners = Collections.synchronizedList(new ArrayList<>());

    @Override @MainThread
    public final void register(FileAccessListener fileAccessListener)
    {
        if(listeners.contains(fileAccessListener))
            throw new FileAccessException("Listener already registered");

        listeners.add(fileAccessListener);
    }

    @Override @MainThread
    public final void unregister(FileAccessListener fileAccessListener)
    {
        if(!listeners.contains(fileAccessListener))
            throw new FileAccessException("Listener not found");

        listeners.remove(fileAccessListener);
    }

    @MainThread
    protected void notifyListenersReady()
    {
        //TODO: replace with lambda. Hey, if we're using them...
        for(FileAccessListener listener : listeners)
        {
            listener.dataReady();
        }
    }

    @MainThread
    protected void notifyListenersFailed()
    {
        //TODO: replace with lambda. Hey, if we're using them...
        for(FileAccessListener listener : listeners)
        {
            listener.dataAccessError();
        }
    }

    @Override @WorkerThread
    public void writeString(Context context, String path, String data)
    {
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
        try
        {
            return new String(readData(context, path), "UTF8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new FileAccessException(e);
        }
    }

    protected void checkPath(String path)
    {
        if(!path.startsWith("/"))
            throw new FileAccessException("Path must be absolute (ie start with '/')");
    }
}
