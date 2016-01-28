package co.touchlab.researchstack.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.FileAccessException;
import co.touchlab.researchstack.core.storage.file.StorageAccessListener;
import co.touchlab.researchstack.core.storage.file.aes.AesEncrypter;
import co.touchlab.researchstack.core.storage.file.aes.Encrypter;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.storage.file.auth.AuthStorageAccessListener;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.utils.FileUtils;
import co.touchlab.researchstack.core.utils.UiThreadContext;

public class StorageAccess implements AuthDataAccess
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Assert Constants
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private static final boolean CHECK_THREADS = false;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Statics
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private static final StorageAccess instance = new StorageAccess();

    private static FileAccess fileAccess;

    private static AppDatabase appDatabase;

    private static AesCbcWithIntegrity.SecretKeys key;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private Handler handler = new Handler(Looper.getMainLooper());

    private List<StorageAccessListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private PinCodeConfig codeConfig;

    private long lastAuthTime;

    private Encrypter encrypter;


    private StorageAccess()
    {
    }

    public static StorageAccess getInstance()
    {
        return instance;
    }

    public static FileAccess getFileAccess()
    {
        if(key == null)
        {
            //TODO throw new AuthAccessException();
        }

        return fileAccess;
    }

    public static AppDatabase getAppDatabase()
    {
        if(key == null)
        {
            //TODO throw new AuthAccessException();
        }

        return appDatabase;
    }

    public void init(PinCodeConfig pinCodeConfig, FileAccess fileAccess, AppDatabase appDatabase)
    {
        StorageAccess.appDatabase = appDatabase;
        StorageAccess.fileAccess = fileAccess;

        codeConfig = pinCodeConfig;
    }

    @Override
    @MainThread
    public void initStorageAccess(Context context)
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
    @MainThread
    public final void register(StorageAccessListener storageAccessListener)
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }
        if(listeners.contains(storageAccessListener))
        {
            throw new FileAccessException("Listener already registered");
        }

        listeners.add(storageAccessListener);
    }

    @Override
    @MainThread
    public final void unregister(StorageAccessListener storageAccessListener)
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }
        listeners.remove(storageAccessListener);
    }

    protected void notifyReady()
    {
        handler.post(this :: notifyListenersReady);
    }

    @MainThread
    public void notifyListenersReady()
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }

        for(StorageAccessListener listener : listeners)
        {
            listener.dataReady();
        }
    }

    protected void notifyHardFail()
    {
        handler.post(this :: notifyListenersHardFail);
    }

    @MainThread
    public void notifyListenersHardFail()
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }

        for(StorageAccessListener listener : listeners)
        {
            listener.dataAccessError();
        }
    }

    protected void notifySoftFail()
    {
        handler.post(this :: notifyListenersSoftFail);
    }

    @MainThread
    public void notifyListenersSoftFail()
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }

        for(StorageAccessListener listener : listeners)
        {
            if(listener instanceof AuthStorageAccessListener)
            {
                ((AuthStorageAccessListener) listener).dataAuth(codeConfig);
            }
        }
    }

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Auth
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private void validateKeyForTimeOut()
    {
        long now = System.currentTimeMillis();

        boolean isPastMinIgnoreTime = now - lastAuthTime > codeConfig.getPinAutoLockTime();

        if(isPastMinIgnoreTime)
        {
            key = null;
            // TODO de-auth fileaccess and appdatabase
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
            AesCbcWithIntegrity.SecretKeys masterKey = AesCbcWithIntegrity.generateKey();
            writeMasterKey(context, masterKeyFile, masterKey, pin);
            initWithMasterKey(masterKey);
        }
        catch(IOException | GeneralSecurityException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void initWithMasterKey(AesCbcWithIntegrity.SecretKeys masterKey)
    {
        key = masterKey;
        encrypter = new AesEncrypter(masterKey);
        fileAccess.setEncrypter(encrypter);
        //TODO  appDatabase.(masterKey.toString());
    }

    @Override
    public PinCodeConfig getPinCodeConfig()
    {
        return codeConfig;
    }

    public void startWithPassphrase(Context context, String passphrase)
    {
        try
        {
            File masterKeyFile = createMasterKeyFile(context);

            if(! masterKeyFile.exists())
            {
                throw new IllegalAccessException("Master-key file does not exist. You should call" +
                        "setPinCode(String pin) to create a Master-key file and encrypt w/ pin-code");
            }

            AesCbcWithIntegrity.SecretKeys masterKey;

            // decrypt master key with key created from passphrase
            String masterKeyString = readMasterKey(context, masterKeyFile, passphrase);
            masterKey = AesCbcWithIntegrity.keys(masterKeyString);

            initWithMasterKey(masterKey);
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
        FileUtils.writeSafe(file, encrypted);
    }

    @NonNull
    private byte[] decryptFile(File file, AesCbcWithIntegrity.SecretKeys secretKeys) throws IOException, GeneralSecurityException
    {
        String encrypted = new String(FileUtils.readAll(file));
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
            FileUtils.writeSafe(salt, saltBytes);
        }

        return FileUtils.readAll(salt);
    }

    public static class AuthAccessException extends Exception
    {
    }

}
