package co.touchlab.researchstack.core.storage.file;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.IOException;

import co.touchlab.researchstack.core.utils.UiThreadContext;


/**
 * Created by kgalligan on 11/24/15.
 */
public class ClearFileAccess extends BaseFileAccess
{
    @Override
    @MainThread
    public void initFileAccess(Context context)
    {
        UiThreadContext.assertUiThread();
        //Local unencrypted files should always be ready
        notifyReady();
    }

    @Override
    @WorkerThread
    public void writeData(Context context, String path, byte[] data)
    {
        File localFile = findLocalFile(context, path);
        writeSafe(localFile, data);
    }

    @Override
    @WorkerThread
    public byte[] readData(Context context, String path)
    {
        try
        {
            File localFile = findLocalFile(context, path);
            return readAll(localFile);
        }
        catch(IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override
    @WorkerThread
    public boolean dataExists(Context context, String path)
    {
        return findLocalFile(context, path).exists();
    }

    @Override
    public void clearData(Context context, String path)
    {
        File localFile = findLocalFile(context, path);
        localFile.delete();
    }

    @NonNull
    private File findLocalFile(Context context, String path)
    {
        checkPath(path);
        return new File(context.getFilesDir(), path.substring(1));
    }
}
