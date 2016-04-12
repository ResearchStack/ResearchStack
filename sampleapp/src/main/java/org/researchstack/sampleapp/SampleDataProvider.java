package org.researchstack.sampleapp;
import android.content.Context;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.sampleapp.bridge.BridgeDataProvider;

/**
 * Created by bradleymcdermott on 3/8/16.
 */
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
    protected int getPublicKeyResId()
    {
        return R.raw.bridge_key;
    }

    @Override
    protected int getTasksAndSchedulesResId()
    {
        return R.raw.tasks_and_schedules;
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
    protected String getUserAgent()
    {
        return BuildConfig.STUDY_NAME + "/" + BuildConfig.VERSION_CODE;
    }
}
