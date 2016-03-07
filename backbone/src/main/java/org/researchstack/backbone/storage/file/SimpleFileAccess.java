package org.researchstack.backbone.storage.file;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.researchstack.backbone.storage.file.aes.Encrypter;
import org.researchstack.backbone.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;


/**
 * Created by kgalligan on 11/25/15.
 */

public class SimpleFileAccess implements FileAccess
{
    private Encrypter encrypter;

    @Override
    @WorkerThread
    public void writeData(Context context, String path, byte[] data)
    {
        try
        {
            File localFile = findLocalFile(context, path);
            FileUtils.writeSafe(localFile, encrypter.encrypt(data));
        }
        catch(GeneralSecurityException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override
    @WorkerThread
    public byte[] readData(Context context, String path)
    {
        try
        {
            File localFile = findLocalFile(context, path);
            return encrypter.decrypt(FileUtils.readAll(localFile));
        }
        catch(IOException | GeneralSecurityException e)
        {
            throw new FileAccessException(e);
        }
    }

    @NonNull
    private File findLocalFile(Context context, String path)
    {
        checkPath(path);
        return new File(context.getFilesDir(), path.substring(1));
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

    @Override
    public void setEncrypter(Encrypter encrypter)
    {
        this.encrypter = encrypter;
    }

    public void checkPath(String path)
    {
        if(! path.startsWith("/"))
        {
            throw new FileAccessException("Path must be absolute (ie start with '/')");
        }
    }
}
