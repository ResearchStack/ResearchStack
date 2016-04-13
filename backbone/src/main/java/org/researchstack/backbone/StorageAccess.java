package org.researchstack.backbone;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;

import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.FileAccessException;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.storage.file.auth.AuthDataAccess;
import org.researchstack.backbone.storage.file.auth.DataAccess;
import org.researchstack.backbone.storage.file.auth.PinCodeConfig;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.utils.UiThreadContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for providing access to the pin-protected file storage and database.
 * Before calling {@link #getFileAccess()} or {@link #getAppDatabase()}, make sure to call {@link
 * #register} and {@link #requestStorageAccess}. Once {@link StorageAccessListener#onDataReady()} is
 * called, then you may call these methods and read/write your data.
 * <p>
 * If {@link StorageAccessListener#onDataAuth()} is called, then you must prompt the user for their
 * pin and authenticate using {@link #authenticate}.
 * <p>
 * {@link org.researchstack.backbone.ui.PinCodeActivity} handles almost all of this for you,
 * including presenting the pin code screen to the user. PinCodeActivity should be used, extended,
 * or it's fuctionality copied to your application's own base Activity. Make sure to delay any data
 * access until {@link PinCodeActivity#onDataReady()} has been called.
 */
public class StorageAccess implements DataAccess, AuthDataAccess
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Assert Constants
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private static final boolean CHECK_THREADS = false;

    private static StorageAccess instance = new StorageAccess();

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private FileAccess         fileAccess;
    private PinCodeConfig      pinCodeConfig;
    private AppDatabase        appDatabase;
    private EncryptionProvider encryptionProvider;

    private Handler handler = new Handler(Looper.getMainLooper());

    private List<StorageAccessListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private StorageAccess()
    {
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static StorageAccess getInstance()
    {
        return instance;
    }

    /**
     * Returns the FileAccess singleton for this application. Should only be called after {@link
     * StorageAccessListener#onDataReady()}.
     *
     * @return the FileAccess singleton
     */
    public FileAccess getFileAccess()
    {
        return fileAccess;
    }

    /**
     * Returns the AppDatabase singleton for this application. Should only be called after {@link
     * StorageAccessListener#onDataReady()}.
     *
     * @return the AppDatabase singleton
     */
    public AppDatabase getAppDatabase()
    {
        return appDatabase;
    }

    /**
     * Returns whether the user has created a pin code or not
     * @param context android context
     * @return a boolean indicating if the user has created a pin code
     */
    public boolean hasPinCode(Context context)
    {
        return encryptionProvider.hasPinCode(context);
    }

    /**
     * Initializes the storage access singleton with the provided dependencies. It is best to call
     * this method inside your {@link Application#onCreate()} method.
     *
     * @param pinCodeConfig an instance of the pin code configuration for your app
     * @param encryptionProvider an encryption provider
     * @param fileAccess an implementation of FileAccess
     * @param appDatabase an implementation of AppDatabase
     */
    public void init(PinCodeConfig pinCodeConfig, EncryptionProvider encryptionProvider, FileAccess fileAccess, AppDatabase appDatabase)
    {
        this.pinCodeConfig = pinCodeConfig;
        this.appDatabase = appDatabase;
        this.fileAccess = fileAccess;
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    @MainThread
    public void requestStorageAccess(Context context)
    {
        UiThreadContext.assertUiThread();

        if(encryptionProvider.needsAuth(context, pinCodeConfig))
        {
            // just need to re-auth
            notifySoftFail();
        }
        else
        {
            notifyReady();
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

    private void notifyReady()
    {
        handler.post(this :: notifyListenersReady);
    }

    @MainThread
    private void notifyListenersReady()
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }

        for(StorageAccessListener listener : listeners)
        {
            listener.onDataReady();
        }
    }

    private void notifyHardFail()
    {
        handler.post(this :: notifyListenersHardFail);
    }

    @MainThread
    private void notifyListenersHardFail()
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }

        for(StorageAccessListener listener : listeners)
        {
            listener.onDataFailed();
        }
    }

    private void notifySoftFail()
    {
        handler.post(this :: notifyListenersSoftFail);
    }

    @MainThread
    private void notifyListenersSoftFail()
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }

        for(StorageAccessListener listener : listeners)
        {
            listener.onDataAuth();
        }
    }

    @Override
    public PinCodeConfig getPinCodeConfig()
    {
        return pinCodeConfig;
    }

    // Encryption-only methods

    @Override
    public void logAccessTime()
    {
        encryptionProvider.logAccessTime();
    }

    @Override
    public void authenticate(Context context, String pin)
    {
        encryptionProvider.startWithPassphrase(context, pin);
        injectEncrypter();
    }

    @Override
    public void setPinCode(Context context, String pin)
    {
        encryptionProvider.setPinCode(context, pin);
        injectEncrypter();
    }

    @Override
    public void changePinCode(Context context, String oldPin, String newPin)
    {
        encryptionProvider.changePinCode(context, oldPin, newPin);
        injectEncrypter();
    }

    private void injectEncrypter()
    {
        fileAccess.setEncrypter(encryptionProvider.getEncrypter());
        appDatabase.setEncryptionKey(encryptionProvider.getEncrypter().getDbKey());
    }

}
