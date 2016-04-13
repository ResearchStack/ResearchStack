package org.researchstack.skin.ui;
import android.content.Context;
import android.content.Intent;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.skin.TaskProvider;

public class ConsentTaskActivity extends ViewTaskActivity
{
    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context, ConsentTaskActivity.class);
        intent.putExtra(EXTRA_TASK, TaskProvider.getInstance().get(TaskProvider.TASK_ID_CONSENT));
        return intent;
    }

    @Override
    public void onDataAuth()
    {
        if(StorageAccess.getInstance().hasPinCode(this))
        {
            super.onDataAuth();
        }
        else // consent doesn't require a pin code
        {
            storageAccessUnregister();
            onDataReady();
        }
    }
}
