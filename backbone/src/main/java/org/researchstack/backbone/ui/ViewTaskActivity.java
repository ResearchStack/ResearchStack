package org.researchstack.backbone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.StepSwitcher;
import org.researchstack.backbone.utils.LogExt;

import java.lang.reflect.Constructor;
import java.util.Date;

public class ViewTaskActivity extends PinCodeActivity implements StepCallbacks
{
    public static final String EXTRA_TASK        = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP        = "ViewTaskActivity.ExtraStep";

    private StepSwitcher root;

    private Step       currentStep;
    private StepLayout currentLayout;
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
        super.setContentView(R.layout.rsb_activity_step_switcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        task.validateParameters();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogExt.i(ViewTaskActivity.class, "Result received");

        // case where result isn't ok?

        if (resultCode == RESULT_OK) {

            if (data != null) {
                getCurrentLayout().receiveIntentExtraOnResult(requestCode, data);
            } else {
                // case for some actions (such as camera) where default behavior is to return null
                // even if action was successful- lets layout know request was successful
                getCurrentLayout().receiveIntentExtraOnResult(requestCode, new Intent());
            }
            LogExt.i(ViewTaskActivity.class, "Result sent to layout " + getCurrentLayout().getClass());
        }
    }

    protected Step getCurrentStep()
    {
        return currentStep;
    }

    protected StepLayout getCurrentLayout() {
        return currentLayout;
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

        StepLayout stepLayout = getLayoutForStep(step);
        currentLayout = stepLayout;
        stepLayout.getLayout().setTag(R.id.rsb_step_layout_id, step.getIdentifier());
        root.show(stepLayout,
                newStepPosition >= currentStepPosition
                        ? StepSwitcher.SHIFT_LEFT
                        : StepSwitcher.SHIFT_RIGHT);
        currentStep = step;
    }

    protected StepLayout getLayoutForStep(Step step)
    {
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        setActionBarTitle(title);

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        // Return the Class & constructor
        StepLayout stepLayout = createLayoutFromStep(step);
        stepLayout.initialize(step, result);
        stepLayout.setCallbacks(this);

        return stepLayout;
    }

    @NonNull
    private StepLayout createLayoutFromStep(Step step)
    {
        try
        {
            Class cls = step.getStepLayoutClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (StepLayout) constructor.newInstance(this);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveAndFinish()
    {
        taskResult.setEndDate(new Date());
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onPause()
    {
        hideKeyboard();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            notifyStepOfBackPress();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        notifyStepOfBackPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TASK, task);
        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
        outState.putSerializable(EXTRA_STEP, currentStep);
    }

    private void notifyStepOfBackPress()
    {
        StepLayout currentStepLayout = (StepLayout) findViewById(R.id.rsb_current_step);
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
        Toast.makeText(this, R.string.rsb_error_data_failed, Toast.LENGTH_LONG).show();
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
                    "See StepCallbacks for allowable arguments");
        }
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm.isActive() && imm.isAcceptingText())
        {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void showConfirmExitDialog()
    {
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
