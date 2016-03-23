package org.researchstack.backbone.utils;
import android.support.annotation.NonNull;

import org.researchstack.backbone.storage.file.FileAccessException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils
{
    public static void writeSafe(File file, byte[] data)
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
    private static File makeTempFile(File localFile)
    {
        return new File(localFile.getParentFile(), localFile.getName() + ".temp");
    }

    public static byte[] readAll(File file) throws IOException
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

    public static void copy(InputStream inputStream, File output) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new FileOutputStream(output);
            int read = 0;
            byte[] bytes = new byte[1024];
            while((read = inputStream.read(bytes)) != - 1)
            {
                outputStream.write(bytes, 0, read);
            }
        }
        finally
        {
            try
            {
                if(inputStream != null)
                {
                    inputStream.close();
                }
            }
            finally
            {
                if(outputStream != null)
                {
                    outputStream.close();
                }
            }
        }
    }
}
