package co.touchlab.researchstack.core.storage.file.aes;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.storage.file.BaseFileAccess;
import co.touchlab.researchstack.core.storage.file.FileAccessException;
import co.touchlab.researchstack.core.utils.UiThreadContext;

/**
 * Created by kgalligan on 11/24/15.
 */
public class AesFileAccess extends BaseFileAccess
{
    public static final String CHARSET_NAME  = "UTF8";
    private final boolean     alphaNumeric;
    private final int         length;
    private       String      newPassCode;
    private       AlertDialog passcodeDialog;
    
    AesCbcWithIntegrity.SecretKeys key;

    public AesFileAccess(boolean alphaNumeric, int length)
    {
        this.alphaNumeric = alphaNumeric;
        this.length = length;
    }

    @Override
    @MainThread
    public void initFileAccess(Context context)
    {
        UiThreadContext.assertUiThread();
        if(key != null)
        {
            notifyReady();
        }
        else
        {
            initDialog(context);
        }
    }

    // This is a bit iffy. But it works :D
    private void initDialog(Context context)
    {
        PassCodeDialogBuilder.PassCodeStateListener listener = new PassCodeDialogBuilder.PassCodeStateListener()
        {
            @Override
            public boolean isPassCodeAlphaNumeric()
            {
                return alphaNumeric;
            }

            @Override
            public boolean isPassCodeExists()
            {
                return passphraseExists(context);
            }

            @Override
            public boolean isNewPassCodeCreated()
            {
                return ! TextUtils.isEmpty(newPassCode);
            }

            @Override
            public int getPassCodeLength()
            {
                return length;
            }
        };

        PassCodeDialogBuilder builder = new PassCodeDialogBuilder(context, R.style.Core_Dialog);
        builder.setPassCodeStateListener(listener);

        // When you have already created a passcode
        builder.setExistingState(new PassCodeState("Enter your passphrase", (pin) -> {
            startWithPassphrase(context, pin);
            notifyReady();
            passcodeDialog.dismiss();
            return false;
        }, (pin, e) -> Toast.makeText(context, "Incorrect Passcode", Toast.LENGTH_LONG).show()));

        // When you need to create a passcode yo
        builder.setCreationState(new PassCodeState("Create a passphrase", pin -> {
            newPassCode = pin;
            return true;
        }, (pin, e) -> Toast.makeText(context, "Wrong format", Toast.LENGTH_LONG).show()));

        // Reconfirm the passcode that you just entered
        builder.setConfirmState(new PassCodeState("Confirm passphrase", passcode -> {
            if(newPassCode.equals(passcode))
            {
                startWithPassphrase(context, passcode);
                notifyReady();
                passcodeDialog.dismiss();
                return false;
            }
            else
            {
                newPassCode = null;
                throw new IllegalStateException();
            }
        }, (pin, e) -> Toast.makeText(context, "Pins do not match", Toast.LENGTH_LONG).show()));

        passcodeDialog = builder.create();

        //If not an Activity, need system alert. Not sure how it wouldn't be, but...
        if(! (context instanceof Activity))
        {
            passcodeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        passcodeDialog.show();
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

    public boolean passphraseExists(Context context)
    {
        File passphraseFile = createPassphraseFile(context);
        return passphraseFile.exists();
    }

    public void startWithPassphrase(Context context, String passphrase)
    {
        try
        {
            File masterKeyFile = createMasterKeyFile(context);
            AesCbcWithIntegrity.SecretKeys masterKey;
            if(! passphraseExists(context))
            {
                // first time, generate master key and encrypt with key created from passphrase
                masterKey = AesCbcWithIntegrity.generateKey();
                writeMasterKey(context, masterKeyFile, masterKey, passphrase);
            }
            else
            {
                // decrypt master key with key created from passphrase
                String masterKeyString = readMasterKey(context, masterKeyFile, passphrase);
                masterKey = AesCbcWithIntegrity.keys(masterKeyString);
            }

            key = masterKey;
        }
        catch(IOException | GeneralSecurityException e)
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

}
