package org.researchstack.molemapper;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.skin.ResearchStack;
import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;

/**
 * Replaces MoleMapperApplication for unit test purposes.
 */
public class TestMoleMapperApplication extends Application implements TestLifecycleApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        ResearchStack.init(this, new MoleMapperResearchStack() {
            @Override
            protected AppDatabase createAppDatabaseImplementation(Context context) {
                // Used to initialize ResearchStack using a mock AppDatabase. Roboelectric and
                // ORMLite do not play nicely
                return mock(AppDatabase.class);
            }

        });
    }

    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }
}
