package org.sagebionetworks.researchstack.sample_sage_backbone_app;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.sagebionetworks.researchstack.backbone.StorageAccess;
import org.sagebionetworks.researchstack.backbone.storage.database.AppDatabase;
import org.sagebionetworks.researchstack.backbone.storage.database.sqlite.DatabaseHelper;
import org.sagebionetworks.researchstack.backbone.storage.file.EncryptionProvider;
import org.sagebionetworks.researchstack.backbone.storage.file.FileAccess;
import org.sagebionetworks.researchstack.backbone.storage.file.PinCodeConfig;
import org.sagebionetworks.researchstack.backbone.storage.file.SimpleFileAccess;
import org.sagebionetworks.researchstack.backbone.storage.file.UnencryptedProvider;


public class BackboneApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Customize your pin code preferences
        PinCodeConfig pinCodeConfig = new PinCodeConfig(); // default pin config (4-digit, 1 min lockout)

        // Customize encryption preferences
        EncryptionProvider encryptionProvider = new UnencryptedProvider(); // No pin, no encryption

        // If you have special file handling needs, implement FileAccess
        FileAccess fileAccess = new SimpleFileAccess();

        // If you have your own custom database, implement AppDatabase
        AppDatabase database = new DatabaseHelper(this,
                DatabaseHelper.DEFAULT_NAME,
                null,
                DatabaseHelper.DEFAULT_VERSION);

        AndroidThreeTen.init(this);
        StorageAccess.getInstance().init(pinCodeConfig, encryptionProvider, fileAccess, database);
    }
}
