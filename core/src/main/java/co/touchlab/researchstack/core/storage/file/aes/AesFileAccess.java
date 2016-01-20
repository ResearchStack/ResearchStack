package co.touchlab.researchstack.core.storage.file.aes;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import co.touchlab.researchstack.core.storage.file.BaseFileAccess;
import co.touchlab.researchstack.core.storage.file.FileAccessException;
import co.touchlab.researchstack.core.storage.file.FileAccessListener;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.storage.file.auth.AuthFileAccessListener;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.utils.UiThreadContext;

/**
 * Created by kgalligan on 11/24/15.
 */
public class AesFileAccess extends BaseFileAccess implements AuthDataAccess
{
    public static final String CHARSET_NAME = "UTF8";

    private PinCodeConfig codeConfig;

    private long minTimeToIgnorePassCode;

    private long lastAuthTime;

    private AesCbcWithIntegrity.SecretKeys key;

    public AesFileAccess(PinCodeConfig codeConfig)
    {
        this.codeConfig = codeConfig;
        this.minTimeToIgnorePassCode = codeConfig.getAutoLockTime();
    }

    @Override
    @MainThread
    public void initFileAccess(Context context)
    {
        UiThreadContext.assertUiThread();

        validateKeyForTimeOut();

        if(key != null || ! hasPinCode(context))
        {
            notifyReady();
        }
        else
        {
            notifySoftFail();
        }
    }

    @Override
    @WorkerThread
    public synchronized void writeData(Context context, String path, byte[] data)
    {
        try
        {
            File file = findLocalFile(context, path);
            writeSafe(file, encrypt(data, key));
        }
        catch(UnsupportedEncodingException | GeneralSecurityException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override
    @WorkerThread
    public synchronized byte[] readData(Context context, String path)
    {
        try
        {
            File file = findLocalFile(context, path);
            if(! file.exists())
            {
                throw new FileAccessException("Can't find " + file.getPath());
            }

            return decryptFile(file, key);
        }
        catch(IOException | GeneralSecurityException e)
        {
            throw new FileAccessException(e);
        }
    }

    @Override
    public boolean dataExists(Context context, String path)
    {
        File file = findLocalFile(context, path);
        return file.exists();
    }

    @Override
    public void clearData(Context context, String path)
    {
        File file = findLocalFile(context, path);
        file.delete();
    }

    public void startWithPassphrase(Context context, String passphrase)
    {
        try
        {
            File masterKeyFile = createMasterKeyFile(context);

            if(!masterKeyFile.exists())
            {
                throw new IllegalAccessException("Attempting to access AesFileAccess without having a " +
                        "pass-code. Implement DataAuthAccess interface on your FileAccess class " +
                        "and call DataAuthAccess.setPinCode(Context, String).");
            }

            AesCbcWithIntegrity.SecretKeys masterKey;

            // decrypt master key with key created from passphrase
            String masterKeyString = readMasterKey(context, masterKeyFile, passphrase);
            masterKey = AesCbcWithIntegrity.keys(masterKeyString);

            key = masterKey;
        }
        catch(IOException | IllegalAccessException | GeneralSecurityException e)
        {
            throw new FileAccessException(e);
        }
    }

    public boolean passphraseExists(Context context)
    {
        File masterKeyFile = createMasterKeyFile(context);
        return masterKeyFile.exists();
    }

    @NonNull
    private File createMasterKeyFile(Context context)
    {
        File secure = createSecureDirectory(context);
        return new File(secure, "__encrypted");
    }

    @NonNull
    private File createSaltFile(Context context)
    {
        File secure = createSecureDirectory(context);
        return new File(secure, "__sodium");
    }

    @NonNull
    private File createSecureDirectory(Context context)
    {
        File file = new File(context.getFilesDir(), "secure");
        file.mkdirs();
        return file;
    }

    public void updatePassphrase(Context context, String oldPassphrase, String newPassphrase)
    {
        try
        {
            File passphraseFile = createMasterKeyFile(context);
            String masterKeys = readMasterKey(context, passphraseFile, oldPassphrase);
            writeMasterKey(context,
                    passphraseFile,
                    AesCbcWithIntegrity.keys(masterKeys),
                    newPassphrase);
        }
        catch(IOException | GeneralSecurityException e)
        {
            throw new FileAccessException(e);
        }
    }

    @NonNull
    private String readMasterKey(Context context, File file, String passphrase) throws IOException, GeneralSecurityException
    {
        byte[] decrypted = decryptFile(file, generatePassphraseKey(context, passphrase));
        return new String(decrypted);
    }

    private void writeMasterKey(Context context, File file, AesCbcWithIntegrity.SecretKeys masterKey, String passphrase) throws GeneralSecurityException, IOException
    {
        byte[] encrypted = encrypt(masterKey.toString().getBytes(),
                generatePassphraseKey(context, passphrase));
        writeSafe(file, encrypted);
    }

    @NonNull
    private byte[] decryptFile(File file, AesCbcWithIntegrity.SecretKeys secretKeys) throws IOException, GeneralSecurityException
    {
        String encrypted = new String(readAll(file));
        AesCbcWithIntegrity.CipherTextIvMac cipherText = new AesCbcWithIntegrity.CipherTextIvMac(
                encrypted);
        return AesCbcWithIntegrity.decrypt(cipherText, secretKeys);
    }

    private byte[] encrypt(byte[] data, AesCbcWithIntegrity.SecretKeys secretKeys) throws UnsupportedEncodingException, GeneralSecurityException
    {
        return AesCbcWithIntegrity.encrypt(data, secretKeys).toString().getBytes();
    }

    @NonNull
    private AesCbcWithIntegrity.SecretKeys generatePassphraseKey(Context context, String passphrase) throws GeneralSecurityException, IOException
    {
        return AesCbcWithIntegrity.generateKeyFromPassword(passphrase, getSalt(context));
    }

    private byte[] getSalt(Context context) throws GeneralSecurityException, IOException
    {
        File salt = createSaltFile(context);
        if(! salt.exists())
        {
            byte[] saltBytes = AesCbcWithIntegrity.generateSalt();
            writeSafe(salt, saltBytes);
        }

        return readAll(salt);
    }

    @NonNull
    private File findLocalFile(Context context, String path)
    {
        checkPath(path);
        return new File(createSecureDirectory(context), path.substring(1));
    }

    protected void notifySoftFail()
    {
        new Handler().post(this :: notifyListenersSoftFail);
    }

    @MainThread
    public void notifyListenersSoftFail()
    {
        if(checkThreads)
        {
            UiThreadContext.assertUiThread();
        }

        for(FileAccessListener listener : listeners)
        {
            if (listener instanceof AuthFileAccessListener)
            {
                ((AuthFileAccessListener) listener).dataAuth(codeConfig);
            }
        }
    }

    private void validateKeyForTimeOut()
    {
        long now = System.currentTimeMillis();

        boolean isPastMinIgnoreTime = now - lastAuthTime > minTimeToIgnorePassCode;

        if(isPastMinIgnoreTime)
        {
            key = null;
        }
    }

    @Override
    public void logAccessTime()
    {
        lastAuthTime = System.currentTimeMillis();
    }

    // TODO Make pass-code auth more obvious that it throws an exception. Add throws list?
    @Override
    public void authenticate(Context context, String pin)
    {
        startWithPassphrase(context, pin);
        notifyReady();
    }

    @Override
    public boolean hasPinCode(Context context)
    {
        return passphraseExists(context);
    }

    @Override
    public void setPinCode(Context context, String pin)
    {
        try
        {
            File masterKeyFile = createMasterKeyFile(context);
            AesCbcWithIntegrity.SecretKeys masterKey;
            masterKey = AesCbcWithIntegrity.generateKey();
            writeMasterKey(context, masterKeyFile, masterKey, pin);
        }
        catch(IOException | GeneralSecurityException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PinCodeConfig getPinCodeConfig()
    {
        return codeConfig;
    }

}
