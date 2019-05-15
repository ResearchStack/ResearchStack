package org.researchstack.skinsampleapp;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.feature.authentication.pincode.PinCodeConfig;
import org.researchstack.feature.storage.database.AppDatabase;
import org.researchstack.feature.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.feature.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.feature.storage.file.EncryptionProvider;
import org.researchstack.feature.storage.file.FileAccess;
import org.researchstack.feature.storage.file.SimpleFileAccess;
import org.researchstack.feature.storage.file.aes.AesProvider;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.PermissionRequestManager;
import org.researchstack.skin.ResearchStack;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.notification.NotificationConfig;
import org.researchstack.skin.notification.SimpleNotificationConfig;
import org.researchstack.skinsampleapp.bridge.BridgeEncryptedDatabase;

public class SampleResearchStack extends ResearchStack
{

    @Override
    protected AppDatabase createAppDatabaseImplementation(Context context)
    {
        SQLiteDatabase.loadLibs(context);
        return new BridgeEncryptedDatabase(context,
                SqlCipherDatabaseHelper.DEFAULT_NAME,
                null,
                SqlCipherDatabaseHelper.DEFAULT_VERSION,
                new UpdatablePassphraseProvider());
    }

    @Override
    protected FileAccess createFileAccessImplementation(Context context)
    {
        return new SimpleFileAccess();
    }

    @Override
    protected PinCodeConfig getPinCodeConfig(Context context)
    {
        long autoLockTime = AppPrefs.getInstance(context).getAutoLockTime();
        return new PinCodeConfig(autoLockTime);
    }

    @Override
    protected EncryptionProvider getEncryptionProvider(Context context)
    {
        return new AesProvider();
    }

    @Override
    protected ResourceManager createResourceManagerImplementation(Context context)
    {
        return new SampleResourceManager();
    }

    @Override
    protected UiManager createUiManagerImplementation(Context context)
    {
        return new SampleUiManager();
    }

    @Override
    protected DataProvider createDataProviderImplementation(Context context)
    {
        return new SampleDataProvider();
    }

    @Override
    protected TaskProvider createTaskProviderImplementation(Context context)
    {
        return new SampleTaskProvider(context);
    }

    @Override
    protected NotificationConfig createNotificationConfigImplementation(Context context)
    {
        return new SimpleNotificationConfig();
    }

    @Override
    protected PermissionRequestManager createPermissionRequestManagerImplementation(Context context)
    {
        return new SamplePermissionResultManager();
    }
}
