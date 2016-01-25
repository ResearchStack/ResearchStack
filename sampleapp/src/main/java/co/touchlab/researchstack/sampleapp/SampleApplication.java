package co.touchlab.researchstack.sampleapp;

import android.app.Application;

import co.touchlab.researchstack.glue.ResearchStack;

public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        ResearchStack.init(this, new SampleResearchStack());
    }

}
