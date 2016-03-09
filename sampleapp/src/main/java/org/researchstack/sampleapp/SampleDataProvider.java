package org.researchstack.sampleapp;
import android.content.Context;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.bridge.BridgeDataProvider;

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
        // TODO
    }

    @Override
    protected int getPublicKeyResId()
    {
        return R.raw.bridge_key;
    }
}
