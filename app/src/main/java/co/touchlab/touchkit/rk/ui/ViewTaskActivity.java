package co.touchlab.touchkit.rk.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.common.task.Task;

public class ViewTaskActivity extends AppCompatActivity
{

    public static final String EXTRA_TASK   = "ViewTaskActivity.ExtraTask";
    public static final int    REQUEST_CODE = 100;

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context, ViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_fragment);
        super.setResult(RESULT_CANCELED);

        if (savedInstanceState == null && getIntent() != null)
        {
            OrderedTask task = getIntent().getParcelableExtra(EXTRA_TASK);
            Log.i(ViewTaskActivity.class.getSimpleName(), task.getIdentifier());

            List<Step> steps = task.getSteps();
            Log.i(ViewTaskActivity.class.getSimpleName(), steps.get(0).getTitle());
        }

    }
}
