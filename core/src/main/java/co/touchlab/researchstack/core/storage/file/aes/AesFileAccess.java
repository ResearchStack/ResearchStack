package co.touchlab.researchstack.core.storage.file.aes;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    public static final String A_LITTLE_TEST = "ALittleTest";
    private final int     bitDepth;
    private final boolean alphaNumeric;
    private final int     length;
    DataDecoder dataDecoder;
    DataEncoder dataEncoder;
    String      key;

    public AesFileAccess(int bitDepth, boolean alphaNumeric, int length)
    {
        this.bitDepth = bitDepth;
        this.alphaNumeric = alphaNumeric;
        this.length = length;
    }

    @Override
    @MainThread
    public void initFileAccess(Context context)
    {
        UiThreadContext.assertUiThread();
        if(dataDecoder != null && dataEncoder != null)
        {
            notifyReady();
        }
        else
        {
            if(passphraseExists(context))
            {
                runPinDialog(context, "Enter your passphrase.", new PinOnClickListener(context)
                {
                    @Override
                    void onPin(Context context, String pin)
                    {
                        try
                        {
                            startWithPassphrase(context, pin);
                            notifyReady();
                        }
                        catch(Exception e)
                        {
                            initFileAccess(context);
                            Toast.makeText(context, "Wrong passphrase", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else
            {
                runPinDialog(context, "Create a passphrase.", new PinOnClickListener(context)
                {
                    @Override
                    void onPin(Context context, String pin)
                    {
                        try
                        {
                            confirmPin(context, pin);
                        }
                        catch(Exception e)
                        {
                            initFileAccess(context);
                            Toast.makeText(context, "Bad format", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    @WorkerThread
    public synchronized void writeData(Context context, String path, byte[] data)
    {
        try
        {
            File file = findLocalFile(context, path);
            byte[] encrypted = dataEncoder.encrypt(data);
            writeSafe(file, encrypted);
        }
        catch(BadPaddingException | IllegalBlockSizeException e)
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

            byte[] encryptedData = readAll(file);
            return dataDecoder.decrypt(encryptedData);
        }
        catch(IOException | BadPaddingException | IllegalBlockSizeException e)
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

    private void confirmPin(Context context, String firstPin)
    {
        runPinDialog(context, "Confirm passphrase.", new PinOnClickListener(context)
        {
            @Override
            void onPin(Context context, String pin)
            {
                try
                {
                    if(! firstPin.equals(pin))
                    {
                        Toast.makeText(context, "Pins do not match", Toast.LENGTH_LONG).show();
                        initFileAccess(context);
                    }
                    else
                    {
                        startWithPassphrase(context, pin);
                        notifyReady();
                    }
                }
                catch(Exception e)
                {
                    initFileAccess(context);
                    Toast.makeText(context, "Bad format", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void runPinDialog(Context context, String title, PinOnClickListener listener)
    {
        View customView = LayoutInflater.from(context)
                .inflate(alphaNumeric
                        ? R.layout.dialog_pin_entry_alphanumeric
                        : R.layout.dialog_pin_entry, null);
        EditText editText = (EditText) customView.findViewById(R.id.pinValue);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(length)});


        listener.setCustomView(customView);
        AlertDialog alertDialog = new AlertDialog.Builder(context).setView(customView)
                .setTitle(title).setOnCancelListener(dialog -> {

                    new Handler().post(AesFileAccess.this :: notifyListenersFailed);
                })
                        //                .setPositiveButton("OK", listener)
                .create();

        RxTextView.textChanges(editText)
                .filter(charSequence -> charSequence.length() == length)
                .subscribe(charSequence -> {
                    new Handler().postDelayed(() -> {
                        listener.onPin(context, charSequence.toString());
                        alertDialog.dismiss();
                    }, 300);
                });

        //If not an Activity, need system alert. Not sure how it wouldn't be, but...
        if(! (context instanceof Activity))
        {
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        alertDialog.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
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
            File passphraseFile = createPassphraseFile(context);
            File passphraseCheckFile = createPassphraseCheckFile(context);
            String uuid;
            if(! passphraseExists(context))
            {
                uuid = UUID.randomUUID().toString();
                writePasskey(passphrase, passphraseFile, uuid);
                writePasskey(passphrase, passphraseCheckFile, A_LITTLE_TEST);
            }
            else
            {
                String testString = readPasskey(passphrase, passphraseCheckFile);
                if(! testString.equals(A_LITTLE_TEST))
                {
                    throw new FileAccessException("Not the correct passphrase");
                }

                uuid = readPasskey(passphrase, passphraseFile);
            }

            resetCodecs(context, uuid);
        }
        catch(InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e)
        {
            throw new FileAccessException(e);
        }
    }

    private void resetCodecs(Context context, String uuid) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException
    {
        dataDecoder = new DataDecoder(uuid.toCharArray(), bitDepth);
        dataEncoder = new DataEncoder(uuid.toCharArray(), bitDepth);
        this.key = uuid;
    }

    public String getKey()
    {
        return key;
    }

    @NonNull
    private File createPassphraseFile(Context context)
    {
        File secure = createSecureDirectory(context);
        return new File(secure, "__encrypted");
    }

    @NonNull
    private File createPassphraseCheckFile(Context context)
    {
        File secure = createSecureDirectory(context);
        return new File(secure, "__encryptedcheck");
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
            File passphraseFile = createPassphraseFile(context);
            File passphraseCheckFile = createPassphraseCheckFile(context);
            String passkey = readPasskey(oldPassphrase, passphraseFile);
            writePasskey(newPassphrase, passphraseFile, passkey);
            writePasskey(newPassphrase, passphraseCheckFile, A_LITTLE_TEST);
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
        byte[] bytes = readAll(encrypted);
        DataDecoder dataDecoder = new DataDecoder(passphrase.toCharArray(), bitDepth);
        uuid = new String(dataDecoder.decrypt(bytes), CHARSET_NAME);
        return uuid;
    }

    private void writePasskey(String passphrase, File encrypted, String uuid) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException
    {
        DataEncoder dataEncoder = new DataEncoder(passphrase.toCharArray(), bitDepth);
        byte[] encrypt = dataEncoder.encrypt(uuid.getBytes(CHARSET_NAME));
        writeSafe(encrypted, encrypt);
    }

    @NonNull
    private File findLocalFile(Context context, String path)
    {
        checkPath(path);
        return new File(createSecureDirectory(context), path.substring(1));
    }

    private abstract class PinOnClickListener implements DialogInterface.OnClickListener
    {
        private final Context context;
        private       View    customView;

        public PinOnClickListener(Context context)
        {
            this.context = context;
        }

        public void setCustomView(View customView)
        {
            this.customView = customView;
        }

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            String passcode = ((EditText) customView.findViewById(R.id.pinValue)).getText()
                    .toString();
            onPin(context, passcode);
        }

        abstract void onPin(Context context, String pin);
    }
}
