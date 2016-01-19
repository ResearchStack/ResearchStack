package co.touchlab.researchstack.sampleapp;

import android.app.Application;
import android.os.StrictMode;

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

        SampleResearchStack srs = new SampleResearchStack(this);
        srs.setDataProvider(new SampleDataProvider());
        ResearchStack.init(srs);

        if (BuildConfig.DEBUG)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                    .detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog().build());
        }

    }
}
