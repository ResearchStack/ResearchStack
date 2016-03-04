package co.touchlab.researchstack.sampleapp;

import android.content.Context;

import co.touchlab.researchstack.backbone.storage.database.AppDatabase;
import co.touchlab.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import co.touchlab.researchstack.backbone.storage.file.EncryptionProvider;
import co.touchlab.researchstack.backbone.storage.file.FileAccess;
import co.touchlab.researchstack.backbone.storage.file.SimpleFileAccess;
import co.touchlab.researchstack.backbone.storage.file.aes.AesProvider;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.skin.AppPrefs;
import co.touchlab.researchstack.skin.DataProvider;
import co.touchlab.researchstack.skin.ResearchStack;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.TaskProvider;
import co.touchlab.researchstack.skin.UiManager;
import co.touchlab.researchstack.skin.notification.NotificationConfig;
import co.touchlab.researchstack.skin.notification.SimpleNotificationConfig;

public class SampleResearchStack extends ResearchStack
{

    @Override
    protected AppDatabase createAppDatabaseImplementation(Context context)
    {
        return SqlCipherDatabaseHelper.getInstance(context);
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

}
