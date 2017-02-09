package org.researchstack.backbone.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.researchstack.backbone.result.logger.DataLoggerManager;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.task.Task;

/**
 * Created by TheMDP on 2/8/17.
 */

public class ActiveTaskActivity extends ViewTaskActivity {

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ActiveTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            init();  // for the first time
        }
    }

    protected void init() {
        DataLoggerManager.initialize(this);
        DataLoggerManager.getInstance().deteleAllDirtyFiles();
    }

    @Override
    protected void showStep(Step step, boolean alwaysReplaceView) {
        super.showStep(step, alwaysReplaceView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(!(step instanceof ActiveStep));
    }
}
