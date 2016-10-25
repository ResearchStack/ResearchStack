package org.researchstack.backbone;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;

import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.StorageAccessException;
import org.researchstack.backbone.storage.file.StorageAccessListener;
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
public class StorageAccess
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Assert Constants
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    private static final boolean CHECK_THREADS = false;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Static Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

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
     * Initializes the storage access singleton with the provided dependencies. It is best to call
     * this method inside your {@link Application#onCreate()} method.
     *
     * @param pinCodeConfig      an instance of the pin code configuration for your app
     * @param encryptionProvider an encryption provider
     * @param fileAccess         an implementation of FileAccess
     * @param appDatabase        an implementation of AppDatabase
     */
    public void init(PinCodeConfig pinCodeConfig, EncryptionProvider encryptionProvider, FileAccess fileAccess, AppDatabase appDatabase)
    {
        this.pinCodeConfig = pinCodeConfig;
        this.appDatabase = appDatabase;
        this.fileAccess = fileAccess;
        this.encryptionProvider = encryptionProvider;
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
     * Returns the pin code configuration for the app
     *
     * @return the pin code configuration
     */
    public PinCodeConfig getPinCodeConfig()
    {
        return pinCodeConfig;
    }

    /**
     * Returns whether the user has created a pin code or not
     *
     * @param context android context
     * @return a boolean indicating if the user has created a pin code
     */
    public boolean hasPinCode(Context context)
    {
        return encryptionProvider.hasPinCode(context);
    }


    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Storage Access request and notification
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    /**
     * Checks and inits storage access. Once storage access is ready, registered listeners will be
     * notified via the {@link StorageAccessListener}. This is true whether there is an auth flow or
     * if the system is already prepared. The callback will happen on the main thread looper, after
     * all scheduled events, which will include (in the standard case) onCreate/onStart/onResume,
     * and the equivalent Fragment callbacks. This is to ensure one path, and reduce edge case race
     * conditions.
     *
     * @param context Calling context. If you are calling from an Activity, its best to send it and
     *                not the application context, because we may want to start new activities, and
     *                the application context can only do so with a new task, which will possibly
     *                complicate the back stack. We won't do anything weird that causes memory
     *                leaks. Promise ;)
     */
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

    /**
     * Registers a listener. If you want to read/write data, you'll need to implement this to know
     * when file access is ready.
     *
     * @param storageAccessListener
     */
    @MainThread
    public final void register(StorageAccessListener storageAccessListener)
    {
        if(CHECK_THREADS)
        {
            UiThreadContext.assertUiThread();
        }
        if(listeners.contains(storageAccessListener))
        {
            throw new StorageAccessException("Listener already registered");
        }

        listeners.add(storageAccessListener);
    }

    /**
     * Guess what this does. Yes, you'll need to call it if you called register or you'll have
     * memory leaks and possibly crashes on callbacks to dead clients.
     *
     * @param storageAccessListener
     */
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

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Encryption-only methods
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    /**
     * Logs the last access time in order to time out the pin code after the amount of time set in
     * the {@link PinCodeConfig}.
     * <p>
     * An example of how this should be used is in {@link PinCodeActivity#onPause()}. This will
     * ensure that it updates frequently while the user is still inside of the app so as not to
     * trigger a reset, but will reset if the user has been outside of the app for too long.
     */
    public void logAccessTime()
    {
        encryptionProvider.logAccessTime();
    }

    /**
     * Attempts authenticating with the provided pin. On an invalid pin, it will throw {@link
     * StorageAccessException}.
     *
     * @param context android context
     * @param pin     string of the pin to attempt authentication
     * @throws StorageAccessException
     */
    public void authenticate(Context context, String pin)
    {
        encryptionProvider.startWithPassphrase(context, pin);
        injectEncrypter();
    }

    /**
     * Creates a master key encrypted by the pin code provided. This can only happen once and will
     * throw a {@link StorageAccessException} on subsequent calls.
     *
     * @param context android context
     * @param pin     the user's pin, this should already be validated (enter + confirm)
     * @throws StorageAccessException if there is already a pin code
     */
    public void createPinCode(Context context, String pin)
    {
        if(hasPinCode(context))
        {
            throw new StorageAccessException("Attempted to create a pin when one already exists");
        }

        encryptionProvider.createPinCode(context, pin);
        injectEncrypter();
    }

    /**
     * Changes the pin code if the provided old pin matches, otherwise throws a {@link
     * StorageAccessException}.
     *
     * @param context android context
     * @param oldPin  the old pin
     * @param newPin  the new pin, which should already be validated (enter + confirm)
     * @throws StorageAccessException
     */
    public void changePinCode(Context context, String oldPin, String newPin)
    {
        encryptionProvider.changePinCode(context, oldPin, newPin);
        injectEncrypter();
    }

    /**
     * Removes the pin code if one exists.
     * @param context android context
     */
    public void removePinCode(Context context) {
        encryptionProvider.removePinCode(context);
    }

    private void injectEncrypter()
    {
        fileAccess.setEncrypter(encryptionProvider.getEncrypter());
        appDatabase.setEncryptionKey(encryptionProvider.getEncrypter().getDbKey());
    }
}
