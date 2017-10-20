package org.researchstack.backbone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import org.researchstack.backbone.utils.StepLayoutHelper;

import java.util.Date;

public class ViewTaskActivity extends PinCodeActivity implements StepCallbacks
{
    public static final String EXTRA_TASK        = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP        = "ViewTaskActivity.ExtraStep";

    private StepSwitcher root;
    protected Toolbar toolbar;

    protected StepLayout currentStepLayout;
    protected Step currentStep;
    protected Task task;
    public Task getTask() {
        return task;
    }
    protected TaskResult taskResult;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        root = (StepSwitcher) findViewById(R.id.container);

        if(savedInstanceState == null)
        {
            task = (Task) getIntent() .getSerializableExtra(EXTRA_TASK);
            taskResult = new TaskResult(task.getIdentifier());
            taskResult.setStartDate(new Date());
        }
        else
        {
            task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }

        LogExt.d(ViewTaskActivity.class, "Received task: "+task.getIdentifier());

        task.validateParameters();

        task.onViewChange(Task.ViewChangeType.ActivityCreate, this, currentStep);
    }

    /**
     * Returns the actual current step being shown.
     * @return an instance of @Step
     */
    public Step getCurrentStep()
    {
        return currentStep;
    }

    protected void showNextStep()
    {
        hideKeyboard();
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

    protected void showStep(Step step)
    {
        showStep(step, false);
    }

    protected void showStep(Step step, boolean alwaysReplaceView)
    {
        int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult)
                .getCurrent();
        int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();

        StepLayout stepLayout = getLayoutForStep(step);
        stepLayout.getLayout().setTag(R.id.rsb_step_layout_id, step.getIdentifier());
        root.show(stepLayout,
                newStepPosition >= currentStepPosition
                        ? StepSwitcher.SHIFT_LEFT
                        : StepSwitcher.SHIFT_RIGHT, alwaysReplaceView);
        currentStep = step;
        currentStepLayout = stepLayout;
    }

    protected void refreshCurrentStep()
    {
        showStep(currentStep, true);
    }

    protected StepLayout getLayoutForStep(Step step)
    {
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        setActionBarTitle(title);

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        // Return the Class & constructor
        StepLayout stepLayout = StepLayoutHelper.createLayoutFromStep(step, this);
        stepLayout.initialize(step, result);
        stepLayout.setCallbacks(this);

        return stepLayout;
    }

    protected void saveAndFinish()
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

        task.onViewChange(Task.ViewChangeType.ActivityPause, this, currentStep);
    }

    @Override
    protected void onResume(){
        super.onResume();
        task.onViewChange(Task.ViewChangeType.ActivityResume, this, currentStep);
    }

    @Override
    protected void onStop(){
        super.onStop();
        task.onViewChange(Task.ViewChangeType.ActivityStop, this, currentStep);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Create Menu which has an "X" or cancel icon
        getMenuInflater().inflate(R.menu.rsb_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.rsb_clear_menu_item) {
            showConfirmExitDialog();
            return true;
        } else if(item.getItemId() == android.R.id.home) {
            notifyStepOfBackPress();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clear out all the data that has been saved by this Activity
     * And push user back to the Overview screen, or whatever screen was below this Activity
     */
    protected void discardResultsAndFinish() {
        taskResult.getResults().clear();
        taskResult = null;
        setResult(Activity.RESULT_CANCELED);
        finish();
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

    protected void notifyStepOfBackPress()
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
        if (result != null) {
            taskResult.setStepResultForStepIdentifier(id, result);
        }
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
        else if(action == StepCallbacks.ACTION_REFRESH)
        {
            refreshCurrentStep();
        }
        else
        {
            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments");
        }
    }

    protected void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm.isActive() && imm.isAcceptingText())
        {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * Make sure user is 100% wanting to cancel, since their data will be discarded
     */
    private void showConfirmExitDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.rsb_are_you_sure)
                .setPositiveButton(R.string.rsb_discard_results, (dialog, i) -> discardResultsAndFinish())
                .setNegativeButton(R.string.rsb_cancel, null).create().show();
    }

    @Override
    public void onCancelStep()
    {
        discardResultsAndFinish();
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
