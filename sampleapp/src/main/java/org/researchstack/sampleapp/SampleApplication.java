package org.researchstack.sampleapp;

import android.app.Application;

import org.researchstack.skin.ResearchStack;

public class SampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        ResearchStack.init(this, new SampleResearchStack());
    }
}
