package co.touchlab.touchkit.rk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.common.task.Task;
import co.touchlab.touchkit.rk.dev.DevUtils;
import co.touchlab.touchkit.rk.ui.fragment.BooleanQuestionStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.StepFragment;
import co.touchlab.touchkit.rk.ui.fragment.TextQuestionStepFragment;

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

        task = (OrderedTask) getIntent().getSerializableExtra(EXTRA_TASK);
        taskResult = new TaskResult(task.getIdentifier(), null, null);

        pager = (StepViewPager) findViewById(R.id.pager);
        StepPagerAdapter adapter = new StepPagerAdapter(getSupportFragmentManager(), task.getSteps());
        pager.setAdapter(adapter);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            int prev = pager.getCurrentItem() - 1;
            pager.setCurrentItem(prev);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    private void saveAndFinish()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT,
                taskResult);
        setResult(RESULT_OK,
                resultIntent);
        finish();
    }

    @Override
    public void onNextPressed(Step step)
    {
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

    @Override
    public void onStepResultChanged(Step step, StepResult result)
    {
        taskResult.setStepResultForStepIdentifier(step.getIdentifier(), result);
    }

    @Override
    public void onSkipStep(Step step)
    {
        onStepResultChanged(step, null);
        onNextPressed(step);
    }

    @Override
    public StepResult getResultStep(String stepId)
    {
        return taskResult.getStepResultForStepIdentifier(stepId);
    }

    private static class StepPagerAdapter extends FragmentStatePagerAdapter
    {
        private final List<Step> steps;

        public StepPagerAdapter(FragmentManager fragmentManager, List<Step> steps)
        {
            super(fragmentManager);
            this.steps = steps;
        }

        @Override
        public Fragment getItem(int position)
        {
            Step step = steps.get(position);
            if (step instanceof QuestionStep){
                if(((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.SingleChoice)
                {
                    return BooleanQuestionStepFragment.newInstance((QuestionStep) step);
                }
                else if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.Text)
                {
                    return TextQuestionStepFragment.newInstance((QuestionStep) step);
                }
                DevUtils.throwUnsupportedOpException();
                return null;
            }
            else {
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
