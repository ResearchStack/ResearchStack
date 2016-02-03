package co.touchlab.researchstack.backbone.storage.file.aes;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import co.touchlab.researchstack.backbone.storage.file.EncryptionProvider;
import co.touchlab.researchstack.backbone.storage.file.FileAccessException;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.backbone.utils.FileUtils;

public class AesProvider implements EncryptionProvider
{
    private Encrypter encrypter;

    private PinCodeConfig codeConfig;

    private long lastAuthTime;

    public AesProvider(PinCodeConfig codeConfig)
    {
        this.codeConfig = codeConfig;
    }

    @Override
    public Encrypter getEncrypter()
    {
        return encrypter;
    }

    private void validateKeyForTimeOut()
    {
        long now = System.currentTimeMillis();

        boolean isPastMinIgnoreTime = now - lastAuthTime > codeConfig.getPinAutoLockTime();

        if(isPastMinIgnoreTime)
        {
            encrypter = null;
            // TODO de-auth fileaccess and appdatabase
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
        encrypter = createEncrypter(masterKey);
        logAccessTime();
    }

    private Encrypter createEncrypter(AesCbcWithIntegrity.SecretKeys masterKey)
    {
        return new AesEncrypter(masterKey);
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

    public boolean needsAuth(Context context)
    {
        validateKeyForTimeOut();

        // TODO why ok with no pincode?
        return encrypter == null && hasPinCode(context);
    }

    public boolean ready()
    {
        return encrypter != null;
    }
}