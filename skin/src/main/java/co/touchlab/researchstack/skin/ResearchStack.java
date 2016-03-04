package co.touchlab.researchstack.skin;

import android.content.Context;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.storage.database.AppDatabase;
import co.touchlab.researchstack.backbone.storage.file.EncryptionProvider;
import co.touchlab.researchstack.backbone.storage.file.FileAccess;
import co.touchlab.researchstack.backbone.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.skin.notification.NotificationConfig;

public abstract class ResearchStack
{
    protected static ResearchStack instance;

    public ResearchStack()
    {
    }

    public synchronized static ResearchStack getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "ResearchStack instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public static void init(Context context, ResearchStack concreteResearchStack)
    {
        instance = concreteResearchStack;

        ResourceManager.init(concreteResearchStack.createResourceManagerImplementation(context));

        UiManager.init(concreteResearchStack.createUiManagerImplementation(context));

        DataProvider.init(concreteResearchStack.createDataProviderImplementation(context));

        StorageAccess.getInstance()
                .init(concreteResearchStack.getPinCodeConfig(context),
                        concreteResearchStack.getEncryptionProvider(context),
                        concreteResearchStack.createFileAccessImplementation(context),
                        concreteResearchStack.createAppDatabaseImplementation(context));

        TaskProvider.init(concreteResearchStack.createTaskProviderImplementation(context));

        NotificationConfig.init(concreteResearchStack.createNotificationConfigImplementation(context));
    }

    protected abstract AppDatabase createAppDatabaseImplementation(Context context);

    protected abstract PinCodeConfig getPinCodeConfig(Context context);

    protected abstract EncryptionProvider getEncryptionProvider(Context context);

    protected abstract FileAccess createFileAccessImplementation(Context context);

    protected abstract ResourceManager createResourceManagerImplementation(Context context);

    protected abstract UiManager createUiManagerImplementation(Context context);

    protected abstract DataProvider createDataProviderImplementation(Context context);

    protected abstract TaskProvider createTaskProviderImplementation(Context context);

    protected abstract NotificationConfig createNotificationConfigImplementation(Context context);

}
