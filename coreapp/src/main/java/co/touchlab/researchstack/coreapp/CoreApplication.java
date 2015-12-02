package co.touchlab.researchstack.coreapp;

import android.app.Application;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.ClearFileAccess;

/**
 * Created by bradleymcdermott on 12/2/15.
 */
public class CoreApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        StorageManager.init(new ClearFileAccess(), DatabaseHelper.getInstance(this));
    }
}
