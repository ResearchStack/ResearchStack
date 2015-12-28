package co.touchlab.researchstack.sampleapp;

import android.app.Application;

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
