package org.researchstack.backbone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
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
import org.researchstack.backbone.task.OrderedTask;
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

    protected StepSwitcher root;
    protected Toolbar toolbar;

    protected StepLayout currentStepLayout;
    public StepLayout getCurrentStepLayout() {
        return currentStepLayout;
    }

    protected Step currentStep;
    protected Task task;
    public Task getTask() {
        return task;
    }
    protected TaskResult taskResult;

    protected int currentStepAction;

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
        super.setContentView(getContentViewId());

        toolbar = (Toolbar) findViewById(getToolbarResourceId());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        root = (StepSwitcher) findViewById(getViewSwitcherRootId());

        if(savedInstanceState == null)
        {
            task = (Task) getIntent() .getSerializableExtra(EXTRA_TASK);

            // Grab the existing task result if it is available, otherwise make a new one
            if (getIntent().hasExtra(EXTRA_TASK_RESULT)) {
                taskResult = (TaskResult) getIntent().getSerializableExtra(EXTRA_TASK_RESULT);
            } else {
                taskResult = new TaskResult(task.getIdentifier());
            }

            if (getIntent().hasExtra(EXTRA_STEP)) {
                currentStep = (Step)getIntent().getSerializableExtra(EXTRA_STEP);
            }

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

    public @IdRes int getToolbarResourceId() {
        return R.id.toolbar;
    }

    /**
     * Returns the actual current step being shown.
     * @return an instance of @Step
     */
    public Step getCurrentStep()
    {
        return currentStep;
    }

    public @LayoutRes int getContentViewId() {
        return R.layout.rsb_activity_step_switcher;
    }

    public @IdRes int getViewSwitcherRootId() {
        return R.id.container;
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
            discardResultsAndFinish();
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

        if (stepLayout == null) {
            LogExt.e(ViewTaskActivity.class, "Trying to add a step layout with a null task result");
            return;
        }

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

        if (taskResult == null) {
            LogExt.e(ViewTaskActivity.class,
                    "Trying to add a step layout with a null task result");
            return null;
        }

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        // Return the Class & constructor
        StepLayout stepLayout = StepLayoutHelper.createLayoutFromStep(step, this);
        setupStepLayoutBeforeInitializeIsCalled(stepLayout);
        stepLayout.initialize(step, result);

        // Some step layouts need to know about the task result
        if (stepLayout instanceof ResultListener) {
            ((ResultListener)stepLayout).taskResult(this, taskResult);
        }

        return stepLayout;
    }

    protected void setupStepLayoutBeforeInitializeIsCalled(StepLayout stepLayout) {
        // can be implemented by sub-classes to set up the step layout before it's initialized
        stepLayout.setCallbacks(this);
        if (stepLayout instanceof OnActionListener) {
            ((OnActionListener)stepLayout).onAction(currentStepAction, this);
        }
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

        // Some step layouts need to know about when the activity pauses
        if (currentStepLayout != null && currentStepLayout instanceof ActivityPauseListener) {
            ((ActivityPauseListener)currentStepLayout).onActivityPause(this);
        }
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
        if (taskResult != null) {  // taskResult can be null in a bad state
            taskResult.getResults().clear();
        } else {
            LogExt.d(ViewTaskActivity.class,
                    "Task result is already null when discarding results");
        }
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
        if (taskResult == null) {
            LogExt.e(ViewTaskActivity.class, "In bad state, " +
                    "task result should never be null, skipping onSaveStepResult");
            return;
        }
        if (result != null) {
            taskResult.setStepResultForStepIdentifier(id, result);
        } else if (taskResult.getResults() != null) {
            // result is null, make sure that is reflected in the results
            taskResult.getResults().remove(id);
        }
    }

    protected void onExecuteStepAction(int action)
    {
        currentStepAction = action;
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
     * This may choose to simply exit the activity if the user is on the first step of the OrderedTask
     */
    public void showConfirmExitDialog()
    {
        boolean showConfigrmDialog = true;
        // Do not show the "Are you sure?" dialog if we are on the first step
        if (task instanceof OrderedTask) {
            OrderedTask orderedTask = (OrderedTask)task;
            if (currentStep != null && orderedTask.getSteps().indexOf(currentStep) == 0) {
                showConfigrmDialog = false;
            }
        }
        if (showConfigrmDialog) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.rsb_are_you_sure)
                    .setPositiveButton(R.string.rsb_discard_results, (dialog, i) -> discardResultsAndFinish())
                    .setNegativeButton(R.string.rsb_cancel, null).create().show();
        } else {
            discardResultsAndFinish();
        }
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

    public interface ResultListener {
        void taskResult(ViewTaskActivity activity, TaskResult taskResult);
    }

    public interface ActivityPauseListener {
        void onActivityPause(ViewTaskActivity activity);
    }

    /**
     * This interface allows StepLayouts to know what step action brought the user to them
     */
    public interface OnActionListener {
        void onAction(int action, ViewTaskActivity activity);
    }
}
