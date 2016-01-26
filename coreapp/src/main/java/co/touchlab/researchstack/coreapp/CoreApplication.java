package co.touchlab.researchstack.coreapp;

import android.app.Application;

import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.BaseFileAccess;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;

/**
 * Created by bradleymcdermott on 12/2/15.
 */
public class CoreApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        StorageAccess.getInstance()
                .init(new PinCodeConfig(), new BaseFileAccess(), DatabaseHelper.getInstance(this));
    }
}
