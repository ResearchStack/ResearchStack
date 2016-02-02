package co.touchlab.researchstack.coreapp;

import android.app.Application;

import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.EncryptionProvider;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.SimpleFileAccess;
import co.touchlab.researchstack.core.storage.file.aes.AesProvider;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;

/**
 * Created by bradleymcdermott on 12/2/15.
 */
public class CoreApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Customize your pin code preferences
        PinCodeConfig pinCodeConfig = new PinCodeConfig();
        EncryptionProvider encryptionProvider = new AesProvider(pinCodeConfig);
        FileAccess fileAccess = new SimpleFileAccess();
        AppDatabase database = DatabaseHelper.getInstance(this);

        StorageAccess.getInstance().init(encryptionProvider, fileAccess, database);
    }
}
