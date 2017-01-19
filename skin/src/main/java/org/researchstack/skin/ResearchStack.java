package org.researchstack.skin;

import android.app.Application;
import android.content.Context;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.skin.notification.NotificationConfig;

/**
 * Research stack is a singleton which controls all the major components of the ResearchStack
 * framework. It is best to initialize within your Apps {@link Application#onCreate()} method.
 */
public abstract class ResearchStack {
    protected static ResearchStack instance;

    /**
     * Default Constructor
     */
    public ResearchStack() {
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public synchronized static ResearchStack getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "ResearchStack instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * The init method is responsible for intializing all singletons of the research stack
     * framework
     *
     * @param context               android context, preferably application context.
     * @param concreteResearchStack implementation of ResearchStack class
     */
    public static void init(Context context, ResearchStack concreteResearchStack) {
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

        PermissionRequestManager.init(concreteResearchStack.createPermissionRequestManagerImplementation(
                context));
    }

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link AppDatabase} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link AppDatabase}
     */
    protected abstract AppDatabase createAppDatabaseImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link PinCodeConfig} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link PinCodeConfig}
     */
    protected abstract PinCodeConfig getPinCodeConfig(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link EncryptionProvider} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link EncryptionProvider}
     */
    protected abstract EncryptionProvider getEncryptionProvider(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link FileAccess} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link FileAccess}
     */
    protected abstract FileAccess createFileAccessImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link ResourceManager} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link ResourceManager}
     */
    protected abstract ResourceManager createResourceManagerImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link UiManager} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link UiManager}
     */
    protected abstract UiManager createUiManagerImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link DataProvider} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link DataProvider}
     */
    protected abstract DataProvider createDataProviderImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link TaskProvider} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link TaskProvider}
     */
    protected abstract TaskProvider createTaskProviderImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link NotificationConfig} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link NotificationConfig}
     */
    protected abstract NotificationConfig createNotificationConfigImplementation(Context context);

    /**
     * Called within {@link #init(Context, ResearchStack)} to initialize {@link PermissionRequestManager} implementation
     *
     * @param context android Contenxt
     * @return concrete implementation of {@link PermissionRequestManager}
     */
    protected abstract PermissionRequestManager createPermissionRequestManagerImplementation(Context context);

}
