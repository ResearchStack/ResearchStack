package org.researchstack.skin.ui;

import android.content.Context;
import android.content.Intent;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;

public class ConsentTaskActivity extends ViewTaskActivity {
    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ConsentTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onDataAuth() {
        if (StorageAccess.getInstance().hasPinCode(this)) {
            super.onDataAuth();
        } else // consent doesn't require a pin code
        {
            onDataReady();
        }
    }
}
