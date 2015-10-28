package co.touchlab.touchkit.rk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.ConsentReviewStep;
import co.touchlab.touchkit.rk.common.step.ConsentSharingStep;
import co.touchlab.touchkit.rk.common.step.ConsentVisualStep;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.SignUpTask;
import co.touchlab.touchkit.rk.common.task.Task;
import co.touchlab.touchkit.rk.ui.fragment.BooleanQuestionStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.ConsentSharingFragment;
import co.touchlab.touchkit.rk.ui.fragment.ConsentStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.NotImplementedStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignInStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpAdditionalInfoStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpEligibleStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpGeneralInfoStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpInclusionCriteriaStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpIneligibleStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpPasscodeStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpPermissionsPrimingStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.SignUpPermissionsStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.StepFragment;
import co.touchlab.touchkit.rk.ui.fragment.TextQuestionStepFragment;

public class ViewTaskActivity extends AppCompatActivity implements StepFragment.StepCallbacks
{

    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final int REQUEST_CODE = 100;
    private Task task;
    private TaskResult taskResult;

    private Step currentStep;

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context,
                ViewTaskActivity.class);
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

        task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
        taskResult = new TaskResult(task.getIdentifier(), null, null);

        loadNextFragment();

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadNextFragment()
    {
        Step nextStep = task.getStepAfterStep(currentStep, taskResult);
        if(nextStep == null)
        {
            saveAndFinish();
        }
        else
        {
            showFragment(nextStep);
        }
    }

    private void loadPreviousFragment()
    {
        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if(previousStep == null)
        {
            onBackPressed();
        }
        else
        {
            showFragment(previousStep);
        }
    }

    private void showFragment(Step step)
    {
        Fragment fragment;

        if(step == null)
        {
            fragment = NotImplementedStepFragment.newInstance(new Step("NullStep"));
        }

        //TODO Implement Consent sharing & review fragments
        if (step instanceof ConsentVisualStep || step instanceof ConsentReviewStep)
        {
            fragment = ConsentStepFragment.newInstance((ConsentVisualStep) step);
        }
        else if (step instanceof ConsentSharingStep)
        {
            fragment = ConsentSharingFragment.newInstance((ConsentSharingStep) step);
        }
        else if (step instanceof QuestionStep)
        {
            if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.SingleChoice)
            {
                LogExt.d(getClass(),
                        "Single Choice Step");
                fragment = BooleanQuestionStepFragment.newInstance((QuestionStep) step);
            }
            else if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.Text)
            {
                LogExt.d(getClass(),
                        "Text Step");
                fragment = TextQuestionStepFragment.newInstance((QuestionStep) step);
            }
            else
            {
                fragment = NotImplementedStepFragment.newInstance(step);
            }
        }
        else
        {
            if (step.getIdentifier()
                    .equals(SignUpTask.SignUpInclusionCriteriaStepIdentifier))
            {
                fragment = SignUpInclusionCriteriaStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpIneligibleStepIdentifier))
            {
                fragment = SignUpIneligibleStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpEligibleStepIdentifier))
            {
                fragment = SignUpEligibleStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpPermissionsPrimingStepIdentifier))
            {
                fragment = SignUpPermissionsPrimingStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpGeneralInfoStepIdentifier))
            {
                fragment = SignUpGeneralInfoStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpMedicalInfoStepIdentifier))
            {
                fragment = SignUpAdditionalInfoStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpPasscodeStepIdentifier))
            {
                fragment = SignUpPasscodeStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpPermissionsStepIdentifier))
            {
                fragment = SignUpPermissionsStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignInStepIdentifier))
            {
                fragment = SignInStepFragment.newInstance(step);
            }
            else
            {
                LogExt.d(getClass(),
                        "No implementation for this step " + step.getIdentifier());
                fragment = NotImplementedStepFragment.newInstance(step);
            }
        }

        currentStep = step;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder,
                        fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            loadPreviousFragment();
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
        loadNextFragment();
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
}
