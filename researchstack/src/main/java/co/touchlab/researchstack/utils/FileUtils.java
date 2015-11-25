package co.touchlab.researchstack.utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by kgalligan on 11/24/15.
 */
public class FileUtils
{
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
}
