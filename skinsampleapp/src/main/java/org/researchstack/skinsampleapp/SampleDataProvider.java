package org.researchstack.skinsampleapp;
import android.content.Context;

import org.researchstack.foundation.components.singletons.ResourcePathManager;
import org.researchstack.foundation.core.models.result.TaskResult;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skinsampleapp.bridge.BridgeDataProvider;


public class SampleDataProvider extends BridgeDataProvider
{
    public SampleDataProvider()
    {
        super();
    }

    @Override
    public void processInitialTaskResult(Context context, TaskResult taskResult)
    {
        // handle result from initial task (save profile info to disk, upload to your server, etc)
    }

    @Override
    protected ResourcePathManager.Resource getPublicKeyResId()
    {
        return new SampleResourceManager.PemResource("bridge_key");
    }

    @Override
    protected ResourcePathManager.Resource getTasksAndSchedules()
    {
        return ResourceManager.getInstance().getTasksAndSchedules();
    }

    @Override
    protected String getBaseUrl()
    {
        return BuildConfig.STUDY_BASE_URL;
    }

    @Override
    protected String getStudyId()
    {
        return BuildConfig.STUDY_ID;
    }

    @Override
    protected String getStudyName() {
        return BuildConfig.STUDY_NAME;
    }

    @Override
    protected int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }

}
