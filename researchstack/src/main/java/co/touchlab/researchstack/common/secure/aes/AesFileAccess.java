package co.touchlab.researchstack.common.secure.aes;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.secure.FileAccess;
import co.touchlab.researchstack.common.secure.FileAccessException;
import co.touchlab.researchstack.utils.FileUtils;

/**
 * Created by kgalligan on 11/24/15.
 */
public class AesFileAccess implements FileAccess
{
    public static final String CHARSET_NAME = "UTF8";
    DataDecoder dataDecoder;
    DataEncoder dataEncoder;

    public void init(Context context, String passphrase)
    {
        try
        {
            File encrypted = createPassphraseFile(context);
            String uuid;
            if(! encrypted.exists())
            {
                uuid = UUID.randomUUID().toString();
                writePasskey(passphrase, encrypted, uuid);
            }
            else
            {
                uuid = readPasskey(passphrase, encrypted);
            }

            resetCodecs(uuid);
        }
        catch(InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    private void resetCodecs(String uuid) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        dataDecoder = new DataDecoder(uuid.toCharArray());
        dataEncoder = new DataEncoder(uuid.toCharArray());
    }

    @NonNull
    private File createPassphraseFile(Context context)
    {
        File secure = createSecureDirectory(context);
        return new File(secure, "__encrypted");
    }

    @NonNull
    private File createSecureDirectory(Context context)
    {
        return new File(context.getFilesDir(), "secure");
    }

    public void updatePassphrase(Context context, String oldPassphrase, String newPassphrase)
    {
        try
        {
            File encrypted = createPassphraseFile(context);
            String passkey = readPasskey(oldPassphrase, encrypted);
            writePasskey(newPassphrase, encrypted, passkey);
        }
        catch(InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    @NonNull
    private String readPasskey(String passphrase, File encrypted) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        String uuid;
        byte[] bytes = FileUtils.readAll(encrypted);
        DataDecoder dataDecoder = new DataDecoder(passphrase.toCharArray());
        uuid = new String(dataDecoder.decrypt(bytes), CHARSET_NAME);
        return uuid;
    }

    private void writePasskey(String passphrase, File encrypted, String uuid) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException
    {
        DataEncoder dataEncoder = new DataEncoder(passphrase.toCharArray());
        byte[] encrypt = dataEncoder.encrypt(uuid.getBytes(CHARSET_NAME));
        FileOutputStream fileOutputStream = new FileOutputStream(encrypted);
        fileOutputStream.write(encrypt);
        fileOutputStream.close();
    }

    @Override
    public synchronized void writeData(Context context, String path, byte[] data)
    {
        try
        {
            File file = new File(createSecureDirectory(context), path);
            byte[] encrypted = dataEncoder.encrypt(data);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(encrypted);
            fileOutputStream.close();
        }
        catch(BadPaddingException | IllegalBlockSizeException | IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override
    public synchronized byte[] readData(Context context, String path)
    {
        try
        {
            File file = new File(createSecureDirectory(context), path);
            if(!file.exists())
                throw new FileAccessException("Can't find "+ file.getPath());

            byte[] encryptedData = FileUtils.readAll(file);
            return dataDecoder.decrypt(encryptedData);
        }
        catch(IOException | BadPaddingException | IllegalBlockSizeException e)
        {
            throw new FileAccessException(e);
        }
    }
}
