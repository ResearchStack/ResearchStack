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
import co.touchlab.touchkit.rk.ui.fragment.ConsentReviewStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.ConsentSharingStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.ConsentVisualStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.IntegerQuestionStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.MultiChoiceQuestionStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.MultiSceneStepFragment;
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
import co.touchlab.touchkit.rk.ui.fragment.SingleChoiceQuestionStepFragment;
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
        intent.putExtra(EXTRA_TASK, task);
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeholder);
        if (fragment instanceof MultiSceneStepFragment)
        {
            MultiSceneStepFragment multiSceneStepFragment = (MultiSceneStepFragment) fragment;
            int current = multiSceneStepFragment.getCurrentScene();
            int count = multiSceneStepFragment.getSceneCount();
            if (current < count - 1)
            {
                multiSceneStepFragment.goForward();
                return;
            }
        }

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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeholder);
        if (fragment instanceof MultiSceneStepFragment)
        {
            MultiSceneStepFragment multiSceneStepFragment = (MultiSceneStepFragment) fragment;
            int current = multiSceneStepFragment.getCurrentScene();
            if (current > 0)
            {
                multiSceneStepFragment.goBack();
                return;
            }
        }

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
        Fragment fragment = getFragmentForStep(step);

        currentStep = step;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.placeholder,
                        fragment)
                .commit();
    }

    protected Fragment getFragmentForStep(Step step)
    {
        if(step == null)
        {
            return NotImplementedStepFragment.newInstance(new Step("NullStep"));
        }

        //TODO Implement Consent sharing & review fragments
        if (step instanceof ConsentVisualStep)
        {
            return ConsentVisualStepFragment.newInstance((ConsentVisualStep) step);
        }
        else if (step instanceof ConsentSharingStep)
        {
            return ConsentSharingStepFragment.newInstance((ConsentSharingStep) step);
        }
        else if (step instanceof ConsentReviewStep)
        {
            return ConsentReviewStepFragment.newInstance((ConsentReviewStep) step);
        }
        else if (step instanceof QuestionStep)
        {
            if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.SingleChoice)
            {
                LogExt.d(getClass(), "Single Choice Step");
                return SingleChoiceQuestionStepFragment.<Integer>newInstance((QuestionStep) step);
            }
            else if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.MultipleChoice)
            {
                LogExt.d(getClass(), "Multi Choice Step");
                return MultiChoiceQuestionStepFragment.<Integer>newInstance((QuestionStep) step);
            }
            else if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.Text)
            {
                LogExt.d(getClass(), "Text Step");
                return TextQuestionStepFragment.newInstance((QuestionStep) step);
            }
            else if (((QuestionStep) step).getQuestionType() == AnswerFormat.QuestionType.Integer)
            {
                LogExt.d(getClass(),
                        "Integer Step");
                return IntegerQuestionStepFragment.newInstance((QuestionStep) step);
            }
            else
            {
                return NotImplementedStepFragment.newInstance(step);
            }
        }
        else
        {
            if (step.getIdentifier()
                    .equals(SignUpTask.SignUpInclusionCriteriaStepIdentifier))
            {
                return SignUpInclusionCriteriaStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpIneligibleStepIdentifier))
            {
                return SignUpIneligibleStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpEligibleStepIdentifier))
            {
                return SignUpEligibleStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpPermissionsPrimingStepIdentifier))
            {
                return SignUpPermissionsPrimingStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpGeneralInfoStepIdentifier))
            {
                return SignUpGeneralInfoStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpMedicalInfoStepIdentifier))
            {
                return SignUpAdditionalInfoStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpPasscodeStepIdentifier))
            {
                return SignUpPasscodeStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignUpPermissionsStepIdentifier))
            {
                return SignUpPermissionsStepFragment.newInstance(step);
            }
            else if (step.getIdentifier()
                    .equals(SignUpTask.SignInStepIdentifier))
            {
                return SignInStepFragment.newInstance(step);
            }
            else
            {
                LogExt.d(getClass(), "No implementation for this step " + step.getIdentifier());
                return NotImplementedStepFragment.newInstance(step);
            }
        }
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
