package co.touchlab.researchstack.coreapp;

import android.app.Application;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.storage.database.AppDatabase;
import co.touchlab.researchstack.backbone.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.backbone.storage.file.EncryptionProvider;
import co.touchlab.researchstack.backbone.storage.file.FileAccess;
import co.touchlab.researchstack.backbone.storage.file.SimpleFileAccess;
import co.touchlab.researchstack.backbone.storage.file.aes.AesProvider;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;

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
