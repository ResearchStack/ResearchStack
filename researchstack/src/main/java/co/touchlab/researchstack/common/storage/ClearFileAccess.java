package co.touchlab.researchstack.common.storage;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import co.touchlab.researchstack.utils.FileUtils;

/**
 * Created by kgalligan on 11/24/15.
 */
public class ClearFileAccess extends BaseFileAccess
{
    @Override @MainThread
    public void initFileAccess(Context context)
    {
        //Local unencrypted files should always be ready
        notifyReady();
    }

    @Override @WorkerThread
    public void writeData(Context context, String path, byte[] data)
    {
        try
        {
            File localFile = findLocalFile(context, path);
            File tempFile = makeTempFile(localFile);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(data);
            fileOutputStream.close();
            tempFile.renameTo(localFile);
        }
        catch(IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override @WorkerThread
    public byte[] readData(Context context, String path)
    {
        try
        {
            File localFile = findLocalFile(context, path);
            return FileUtils.readAll(localFile);
        }
        catch(IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override @WorkerThread
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
    private File makeTempFile(File localFile)
    {
        return new File(localFile.getParentFile(), localFile.getName() + ".temp");
    }

    @NonNull
    private File findLocalFile(Context context, String path)
    {
        checkPath(path);
        return new File(context.getFilesDir(), path.substring(1));
    }
}
