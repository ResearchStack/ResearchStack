package co.touchlab.researchstack.backboneapp;

import android.app.Application;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.storage.database.AppDatabase;
import co.touchlab.researchstack.backbone.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.backbone.storage.file.EncryptionProvider;
import co.touchlab.researchstack.backbone.storage.file.FileAccess;
import co.touchlab.researchstack.backbone.storage.file.SimpleFileAccess;
import co.touchlab.researchstack.backbone.storage.file.UnencryptedProvider;
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

        // TODO figure out a better pattern for pin/encryption configuration
        // Customize your pin code preferences
        PinCodeConfig pinCodeConfig = new PinCodeConfig(); // default pin config (4-digit, 1 min lockout)

        // Customize encryption preferences
        EncryptionProvider encryptionProvider = new UnencryptedProvider(); // No pin, no encryption
        // TODO pin/encryption only working properly in Skin right now, fix it (onDataReady called when no pincode has been create
        // EncryptionProvider encryptionProvider = new UnencryptedPinProvider(); // Pin, no encryption
        // EncryptionProvider encryptionProvider = new AesProvider(); // Pin + AES encryption

        // If you have special file handling needs, implement FileAccess
        FileAccess fileAccess = new SimpleFileAccess();

        // If you have your own custom database, implement AppDatabase
        AppDatabase database = DatabaseHelper.getInstance(this);

        StorageAccess.getInstance().init(pinCodeConfig, encryptionProvider, fileAccess, database);
    }
}
