package co.touchlab.researchstack.backbone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.Date;

import co.touchlab.researchstack.backbone.R;
import co.touchlab.researchstack.backbone.helpers.LogExt;
import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.task.Task;
import co.touchlab.researchstack.backbone.ui.callbacks.StepCallbacks;
import co.touchlab.researchstack.backbone.ui.step.layout.StepLayout;
import co.touchlab.researchstack.backbone.ui.step.layout.SurveyStepLayout;
import co.touchlab.researchstack.backbone.ui.views.StepSwitcher;

public class ViewTaskActivity extends PinCodeActivity implements StepCallbacks
{
    public static final String EXTRA_TASK        = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP        = "ViewTaskActivity.ExtraStep";

    private StepSwitcher root;

    private Step       currentStep;
    private Task       task;
    private TaskResult taskResult;

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
        super.setResult(RESULT_CANCELED);
        super.setContentView(R.layout.activity_step_switcher);

        root = (StepSwitcher) findViewById(R.id.container);

        if(savedInstanceState == null)
        {
            task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
            taskResult = new TaskResult(task.getIdentifier());
            taskResult.setStartDate(new Date());
        }
        else
        {
            task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected Step getCurrentStep()
    {
        return currentStep;
    }

    protected void showNextStep()
    {
        Step nextStep = task.getStepAfterStep(currentStep, taskResult);
        if(nextStep == null)
        {
            saveAndFinish();
        }
        else
        {
            showStep(nextStep);
        }
    }

    protected void showPreviousStep()
    {
        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if(previousStep == null)
        {
            finish();
        }
        else
        {
            showStep(previousStep);
        }
    }

    private void showStep(Step step)
    {
        int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult)
                .getCurrent();
        int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();

        StepLayout stepLayout = getSceneForStep(step);
        stepLayout.getLayout().setTag(R.id.rsc_step_layout_id, step.getIdentifier());
        //TODO Get SubmitBar from layout, set positive button title to either "Get Started", "Next", or "Done"
        //TODO Remove ConsentTask.initVisualSteps() and ConsentVisualStep.nextButtonString
        root.show(stepLayout,
                newStepPosition >= currentStepPosition
                        ? StepSwitcher.SHIFT_LEFT
                        : StepSwitcher.SHIFT_RIGHT);
        currentStep = step;
    }

    protected StepLayout getSceneForStep(Step step)
    {
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        setActionBarTitle(title);

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        // Return the Class & constructor
        StepLayout stepLayout = createSceneFromStep(step);
        stepLayout.initialize(step, result);
        stepLayout.setCallbacks(this);

        return stepLayout;
    }

    @NonNull
    private StepLayout createSceneFromStep(Step step)
    {
        // TODO figure out how to best create scenes (maybe method on the Step)
        if(step instanceof QuestionStep)
        {
            LogExt.d(getClass(), "Making new SurveyStep");
            return new SurveyStepLayout(ViewTaskActivity.this);
        }

        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (StepLayout) constructor.newInstance(this);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void saveAndFinish()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            notifySceneOfBackPress();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        notifySceneOfBackPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TASK, task);
        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
        outState.putSerializable(EXTRA_STEP, currentStep);
    }

    private void notifySceneOfBackPress()
    {
        StepLayout currentStepLayout = (StepLayout) findViewById(R.id.rsc_current_step);
        currentStepLayout.isBackEventConsumed();
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();

        if(currentStep == null)
        {
            currentStep = task.getStepAfterStep(null, taskResult);
        }

        showStep(currentStep);
    }

    @Override
    public void onDataFailed()
    {
        super.onDataFailed();
        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result)
    {
        onSaveStepResult(step.getIdentifier(), result);

        onExecuteStepAction(action);
    }

    protected void onSaveStepResult(String id, StepResult result)
    {
        taskResult.setStepResultForStepIdentifier(id, result);
    }

    protected void onExecuteStepAction(int action)
    {
        if(action == StepCallbacks.ACTION_NEXT)
        {
            showNextStep();
        }
        else if(action == StepCallbacks.ACTION_PREV)
        {
            showPreviousStep();
        }
        else if(action == StepCallbacks.ACTION_END)
        {
            showConfirmExitDialog();
        }
        else if(action == StepCallbacks.ACTION_NONE)
        {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        }
        else
        {
            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See SceneCallbacks for allowable arguments");
        }
    }

    private void showConfirmExitDialog()
    {
        //TODO Implement custom bottom sheet
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(
                "Are you sure you want to exit?")
                .setMessage(R.string.lorem_medium)
                .setPositiveButton("End Task", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onCancelStep()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void setActionBarTitle(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(title);
        }
    }
}
