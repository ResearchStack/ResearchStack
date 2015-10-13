package co.touchlab.touchkit.rk.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.common.task.Task;
import co.touchlab.touchkit.rk.dev.DevUtils;
import co.touchlab.touchkit.rk.ui.fragment.QuestionStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.StepFragment;

public class ViewTaskActivity extends AppCompatActivity implements StepFragment.StepCallbacks
{

    public static final String EXTRA_TASK   = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT   = "ViewTaskActivity.ExtraTaskResult";
    public static final int    REQUEST_CODE = 100;
    private OrderedTask task;
    private TaskResult taskResult;
    private StepViewPager pager;

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context, ViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK,
                task);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_fragment);
        super.setResult(RESULT_CANCELED);

        task = getIntent().getParcelableExtra(EXTRA_TASK);
        taskResult = new TaskResult(task.getIdentifier(), null, null);

        pager = (StepViewPager) findViewById(R.id.pager);
        StepPagerAdapter adapter = new StepPagerAdapter(getSupportFragmentManager(), task.getSteps(), taskResult);
        pager.setAdapter(adapter);
    }

    @Override
    public void onNextPressed(Step step, StepResult result)
    {
        taskResult.setStepResultForStepIdentifier(step.getIdentifier(), result);
        int next = pager.getCurrentItem() + 1;

        if(next >= pager.getAdapter().getCount())
        {
            saveAndFinish();
        }
        else
        {
            pager.setCurrentItem(next);
        }
    }

    private void saveAndFinish()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private static class StepPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Step> steps;
        private final TaskResult taskResult;

        public StepPagerAdapter(FragmentManager fragmentManager, List<Step> steps, TaskResult taskResult)
        {
            super(fragmentManager);
            this.steps = steps;
            this.taskResult = taskResult;
        }

        @Override
        public Fragment getItem(int position)
        {
            Step step = steps.get(position);
            if (step instanceof QuestionStep){
                return QuestionStepFragment.newInstance((QuestionStep) step, taskResult.getStepResultForStepIdentifier(step.getIdentifier()));
            } else {
                DevUtils.throwUnsupportedOpException();
                return null;
            }
        }

        @Override
        public int getCount()
        {
            return steps.size();
        }
    }
}
