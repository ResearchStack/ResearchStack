package co.touchlab.researchstack.backbone;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.touchlab.researchstack.backbone.storage.database.AppDatabase;
import co.touchlab.researchstack.backbone.storage.file.EncryptionProvider;
import co.touchlab.researchstack.backbone.storage.file.FileAccess;
import co.touchlab.researchstack.backbone.storage.file.FileAccessException;
import co.touchlab.researchstack.backbone.storage.file.StorageAccessListener;
import co.touchlab.researchstack.backbone.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.backbone.storage.file.auth.DataAccess;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.backbone.utils.UiThreadContext;

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

    private FileAccess fileAccess;

    private PinCodeConfig pinCodeConfig;
    private AppDatabase   appDatabase;

    private Handler handler = new Handler(Looper.getMainLooper());

    private List<StorageAccessListener> listeners = Collections.synchronizedList(new ArrayList<>());

    private EncryptionProvider encryptionProvider;

    private StorageAccess()
    {
    }

    public static StorageAccess getInstance()
    {
        return instance;
    }

    public FileAccess getFileAccess()
    {
        // TODO throw new AuthAccessException(); if not ready?
        return fileAccess;
    }

    public AppDatabase getAppDatabase()
    {
        // TODO throw new AuthAccessException(); if not ready?
        return appDatabase;
    }

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
            listener.onDataReady();
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
            listener.onDataFailed();
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

    // TODO Make pass-code auth more obvious that it throws an exception. Add throws list?
    @Override
    public void authenticate(Context context, String pin)
    {
        encryptionProvider.startWithPassphrase(context, pin);
        injectEncrypter();
    }

    private void injectEncrypter()
    {
        fileAccess.setEncrypter(encryptionProvider.getEncrypter());
        appDatabase.setEncryptionKey(encryptionProvider.getEncrypter().getDbKey());
    }

    // TODO this seems weird here
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

    // TODO need to figure out a better way to allow no auth when they haven't created a pin code yet
    public boolean hasPinCode(Context context)
    {
        return encryptionProvider.hasPinCode(context);
    }

    public static class AuthAccessException extends Exception
    {
    }
}
