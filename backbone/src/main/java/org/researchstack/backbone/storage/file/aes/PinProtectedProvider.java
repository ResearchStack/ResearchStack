package org.researchstack.backbone.storage.file.aes;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.StorageAccessException;
import org.researchstack.backbone.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * This abstract class allows for pin protection, whether or not the files are actually encrypted
 * and with what method is up to the implementation.
 */
public abstract class PinProtectedProvider implements EncryptionProvider
{
    private Encrypter encrypter;

    private long lastAuthTime;

    /**
     * Default constructor
     */
    public PinProtectedProvider()
    {
    }

    @Override
    public Encrypter getEncrypter()
    {
        return encrypter;
    }

    private void validateKeyForTimeOut(long autoLockTime)
    {
        long now = System.currentTimeMillis();

        boolean isPastMinIgnoreTime = now - lastAuthTime > autoLockTime;

        if(isPastMinIgnoreTime)
        {
            encrypter = null;
        }
    }

    @Override
    public void logAccessTime()
    {
        lastAuthTime = System.currentTimeMillis();
    }

    @Override
    public boolean hasPinCode(Context context)
    {
        return passphraseExists(context);
    }

    @Override
    public void createPinCode(Context context, String pin)
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
            throw new StorageAccessException(e);
        }
    }

    @Override
    public void changePinCode(Context context, String oldPin, String newPin)
    {
        try
        {
            File masterKeyFile = createMasterKeyFile(context);
            String masterKeyString = readMasterKey(context, masterKeyFile, oldPin);
            AesCbcWithIntegrity.SecretKeys masterKey = AesCbcWithIntegrity.keys(masterKeyString);

            writeMasterKey(context, masterKeyFile, masterKey, newPin);
            initWithMasterKey(masterKey);
        }
        catch(IOException | GeneralSecurityException e)
        {
            throw new StorageAccessException(e);
        }
    }

    public void removePinCode(Context context) {
        removePassphrase(context);
    }

    private void initWithMasterKey(AesCbcWithIntegrity.SecretKeys masterKey)
    {
        encrypter = createEncrypter(masterKey);
        logAccessTime();
    }

    protected abstract Encrypter createEncrypter(AesCbcWithIntegrity.SecretKeys masterKey);

    public void startWithPassphrase(Context context, String passphrase)
    {
        try
        {
            File masterKeyFile = createMasterKeyFile(context);

            if(! masterKeyFile.exists())
            {
                throw new IllegalAccessException("Master-key file does not exist. You should call" +
                        "createPinCode(String pin) to create a Master-key file and encrypt w/ pin-code");
            }

            AesCbcWithIntegrity.SecretKeys masterKey;

            // decrypt master key with key created from passphrase
            String masterKeyString = readMasterKey(context, masterKeyFile, passphrase);
            masterKey = AesCbcWithIntegrity.keys(masterKeyString);

            initWithMasterKey(masterKey);
        }
        catch(IOException | IllegalAccessException | GeneralSecurityException e)
        {
            throw new StorageAccessException(e);
        }
    }

    public boolean passphraseExists(Context context)
    {
        File masterKeyFile = createMasterKeyFile(context);
        return masterKeyFile.exists();
    }

    public void removePassphrase(Context context) {
        if (passphraseExists(context)) {
            removeMasterKeyFile(context);
        }
    }

    @NonNull
    private File createMasterKeyFile(Context context)
    {
        File secure = createSecureDirectory(context);
        return new File(secure, "__encrypted");
    }

    private void removeMasterKeyFile(Context context) {
        File masterKeyFile = createMasterKeyFile(context);
        if (masterKeyFile.exists()) {
            masterKeyFile.delete();
        }
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

    @Override
    public boolean needsAuth(Context context, PinCodeConfig codeConfig)
    {
        validateKeyForTimeOut(codeConfig.getPinAutoLockTime());

        return encrypter == null;
    }
}