package co.touchlab.researchstack.sampleapp;

import android.app.Application;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.storage.database.sqlite.DatabaseHelper;
import co.touchlab.researchstack.core.storage.file.aes.AesFileAccess;
import co.touchlab.researchstack.glue.ResearchStack;

/**
 * Created by bradleymcdermott on 12/2/15.
 */
public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        ResearchStack.init(new SampleResearchStack(this));
    }
}
