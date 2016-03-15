package org.researchstack.sampleapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.researchstack.skin.ResearchStack;

public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        ResearchStack.init(this, new SampleResearchStack());
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        // This is needed for android versions < 5.0 or you can extend MultiDexApplication
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
