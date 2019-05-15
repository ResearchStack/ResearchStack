package org.researchstack.backboneexampleapp;

import android.app.Application;

import org.researchstack.feature.authentication.pincode.PinCodeConfig;
import org.researchstack.feature.storage.StorageAccess;
import org.researchstack.feature.storage.database.AppDatabase;
import org.researchstack.feature.storage.database.sqlite.DatabaseHelper;
import org.researchstack.feature.storage.file.EncryptionProvider;
import org.researchstack.feature.storage.file.FileAccess;
import org.researchstack.feature.storage.file.SimpleFileAccess;
import org.researchstack.feature.storage.file.UnencryptedProvider;


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

        StorageAccess.getInstance().init(pinCodeConfig, encryptionProvider, fileAccess, database);
    }
}
