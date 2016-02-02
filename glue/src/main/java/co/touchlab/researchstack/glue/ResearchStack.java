package co.touchlab.researchstack.glue;

import android.content.Context;

import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.file.EncryptionProvider;
import co.touchlab.researchstack.core.storage.file.FileAccess;

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
                    "Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
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
                .init(concreteResearchStack.getEncryptionProvider(context),
                        concreteResearchStack.createFileAccessImplementation(context),
                        concreteResearchStack.createAppDatabaseImplementation(context));
    }

    protected abstract AppDatabase createAppDatabaseImplementation(Context context);

    protected abstract EncryptionProvider getEncryptionProvider(Context context);

    protected abstract FileAccess createFileAccessImplementation(Context context);

    protected abstract ResourceManager createResourceManagerImplementation(Context context);

    protected abstract UiManager createUiManagerImplementation(Context context);

    protected abstract DataProvider createDataProviderImplementation(Context context);

}
