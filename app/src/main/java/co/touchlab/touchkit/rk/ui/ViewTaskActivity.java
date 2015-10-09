package co.touchlab.touchkit.rk.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.common.task.Task;
import co.touchlab.touchkit.rk.dev.DevUtils;
import co.touchlab.touchkit.rk.ui.fragment.QuestionStepFragment;

public class ViewTaskActivity extends AppCompatActivity
{

    public static final String EXTRA_TASK   = "ViewTaskActivity.ExtraTask";
    public static final int    REQUEST_CODE = 100;
    private OrderedTask task;

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

        task = getIntent().getParcelableExtra(EXTRA_TASK);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        StepPagerAdapter adapter = new StepPagerAdapter(getSupportFragmentManager(), task.getSteps());
        pager.setAdapter(adapter);
    }

    private class StepPagerAdapter extends FragmentPagerAdapter
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
                return QuestionStepFragment.newInstance((QuestionStep) step);
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
