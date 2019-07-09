package org.researchstack.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.NotNull;
import org.researchstack.backbone.R;
import org.researchstack.backbone.interop.ResultFactory;
import org.researchstack.backbone.interop.StepAdapterFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.foundation.components.common.task.OrderedTask;
import org.researchstack.foundation.components.presentation.ITaskProvider;
import org.researchstack.foundation.components.presentation.TaskPresentationFragment;
import org.researchstack.foundation.components.presentation.TaskPresentationViewModelFactory;
import org.researchstack.foundation.components.presentation.compatibility.BackwardsCompatibleStepFragmentProvider;
import org.researchstack.foundation.components.presentation.compatibility.BackwardsCompatibleTaskPresentationFragment;
import org.researchstack.foundation.components.presentation.interfaces.IStepFragmentProvider;
import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator;
import org.researchstack.foundation.core.interfaces.IStep;
import org.researchstack.foundation.core.models.step.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.threeten.bp.DateTimeUtils.toDate;

public class ViewTaskActivity2 extends PinCodeActivity2 implements TaskPresentationFragment.OnPerformTaskExitListener {
    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
    public static final String TASK_FRAGMENT_TAG = "TaskFragmentTag";

    public static final int CONTENT_VIEW_ID = R.id.rsb_content_container;

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ViewTaskActivity2.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    private Fragment taskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setResult(RESULT_CANCELED);

        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        root = (StepSwitcher) findViewById(R.id.container);
        Step currentStep = null;
        Task task;
        TaskResult taskResult = null;
        if (savedInstanceState == null) {
            task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
        } else {
            task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }

        if (savedInstanceState == null) {
            taskFragment = create(task, taskResult, currentStep);
        }

        task.validateParameters();

        //TODO: joliu fix
//        task.onViewChange(Task.ViewChangeType.ActivityCreate, this, currentStep);
    }

    @VisibleForTesting
    BackwardsCompatibleTaskPresentationFragment create(@NonNull Task task, @Nullable TaskResult taskResult, @Nullable Step step) {
        return BackwardsCompatibleTaskPresentationFragment.createInstance(task.getIdentifier(), getTaskPresentationViewModelFactory(task), getIStepFragmentProvider());
    }

    @VisibleForTesting
    ResultFactory getResultFactory() {
        return new ResultFactory() {

            @Override
            public <E> StepResult<E> create(@NotNull org.researchstack.foundation.core.models.result.StepResult<E> result) {
                StepResult<E> stepResult = new StepResult<>(new org.researchstack.backbone.step.Step(result.getIdentifier()));

                stepResult.setResults(result.getResults());

                return stepResult;
            }

            @Override
            public <E> org.researchstack.foundation.core.models.result.StepResult<E> create(@NotNull StepResult<E> result) {
                org.researchstack.foundation.core.models.result.StepResult<E> stepResult =
                        new org.researchstack.foundation.core.models.result.StepResult<>(result.getIdentifier());
                stepResult.setResults(result.getResults());

                return stepResult;
            }
        };
    }

    @VisibleForTesting
    IStepFragmentProvider getIStepFragmentProvider() {
        return new BackwardsCompatibleStepFragmentProvider(this, getStepAdapterFactory(), getResultFactory());
    }

    Map<String, org.researchstack.backbone.step.Step> backboneSteps = new HashMap<>();

    @VisibleForTesting
    StepAdapterFactory getStepAdapterFactory() {
        return new StepAdapterFactory() {

            @Override
            public org.researchstack.backbone.step.Step create(IStep step) {
                return backboneSteps.get(step.getIdentifier());
            }

            @Override
            public IStep create(org.researchstack.backbone.step.Step step) {
                backboneSteps.put(step.getIdentifier(), step);
                return new org.researchstack.foundation.core.models.step.Step(step.getIdentifier(), step.getTitle());
            }
        };
    }

    @VisibleForTesting
    ITaskProvider getITaskProvider(@NonNull Task task) {
        org.researchstack.backbone.task.OrderedTask orderedTask = (org.researchstack.backbone.task.OrderedTask) task;

        List<org.researchstack.foundation.core.models.step.Step> uiSteps = new ArrayList<>();
        for (org.researchstack.backbone.step.Step backboneStep : orderedTask.getSteps()) {
            Step uiStep = (Step) getStepAdapterFactory().create(backboneStep);
            uiSteps.add(uiStep);
        }
        return (taskIdentifier) -> new OrderedTask(task.getIdentifier(), uiSteps);
    }

    @VisibleForTesting
    ITaskNavigator<org.researchstack.foundation.core.models.step.Step, org.researchstack.foundation.core.models.result.TaskResult> getITaskNavigator(@NonNull Task task) {
        return (OrderedTask) getITaskProvider(task).task(task.getIdentifier());
    }

    @VisibleForTesting
    TaskPresentationViewModelFactory<Step>
    getTaskPresentationViewModelFactory(@NonNull Task task) {
        return new TaskPresentationViewModelFactory<>(getITaskNavigator(task), getITaskProvider(task));
    }

//    protected void showNextStep() {
//        Step nextStep = task.getStepAfterStep(currentStep, taskResult);
//        if (nextStep == null) {
//            saveAndFinish();
//        } else {
//            showStep(nextStep);
//        }
//    }
//
//    protected void showPreviousStep() {
//        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
//        if (previousStep == null) {
//            finish();
//        } else {
//            showStep(previousStep);
//        }
//    }

//    private void showStep(Step step) {
//        int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult)
//                .getCurrent();
//        int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();
//
//        StepLayout stepLayout = getLayoutForStep(step);
//        stepLayout.getLayout().setTag(R.id.rsb_step_layout_id, step.getIdentifier());
//        root.show(stepLayout,
//                newStepPosition >= currentStepPosition
//                        ? StepSwitcher.SHIFT_LEFT
//                        : StepSwitcher.SHIFT_RIGHT);
//        currentStep = step;
//    }

//    protected StepLayout getLayoutForStep(Step step) {
//        // Change the title on the activity
//        String title = task.getTitleForStep(this, step);
//        setActionBarTitle(title);
//
//        // Get result from the TaskResult, can be null
//        StepResult result = taskResult.getStepResult(step.getIdentifier());
//
//        // Return the Class & constructor
//        StepLayout stepLayout = createLayoutFromStep(step);
//        stepLayout.initialize(step, result);
//        stepLayout.setCallbacks(this);
//
//        return stepLayout;
//    }

//    @NonNull
//    private StepLayout createLayoutFromStep(Step step) {
//        try {
//            Class cls = step.getStepLayoutClass();
//            Constructor constructor = cls.getConstructor(Context.class);
//            return (StepLayout) constructor.newInstance(this);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void saveAndFinish() {
//        taskResult.setEndDate(new Date());
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
//        setResult(RESULT_OK, resultIntent);
//        finish();
//    }
//
//    @Override
//    protected void onPause() {
////        hideKeyboard();
//        super.onPause();
////TODO: joliu fix
////        task.onViewChange(Task.ViewChangeType.ActivityPause, this, currentStep);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //TODO: joliu fix
////        task.onViewChange(Task.ViewChangeType.ActivityResume, this, currentStep);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        //TODO: joliu fix
////        task.onViewChange(Task.ViewChangeType.ActivityStop, this, currentStep);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            notifyStepOfBackPress();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onBackPressed() {
//        notifyStepOfBackPress();
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable(EXTRA_TASK, task);
//        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
//        outState.putSerializable(EXTRA_STEP, currentStep);
//    }

//    private void notifyStepOfBackPress() {
//        StepLayout currentStepLayout = (StepLayout) findViewById(R.id.rsb_current_step);
//        currentStepLayout.isBackEventConsumed();
//    }

    @Override
    public void onDataReady() {
        super.onDataReady();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(CONTENT_VIEW_ID, taskFragment).commit();
    }

    @Override
    public void onDataFailed() {
        super.onDataFailed();
        Toast.makeText(this, R.string.rsb_error_data_failed, Toast.LENGTH_LONG).show();
        finish();
    }

//    @Override
//    public void onSaveStep(int action, Step step, StepResult result) {
////        onSaveStepResult(step.getIdentifier(), result);
//
////        onExecuteStepAction(action);
//    }

//    protected void onSaveStepResult(String id, StepResult result) {
//        taskResult.setStepResultForStepIdentifier(id, result);
//    }
//
//    protected void onExecuteStepAction(int action) {
//        if (action == StepCallbacks.ACTION_NEXT) {
//            showNextStep();
//        } else if (action == StepCallbacks.ACTION_PREV) {
//            showPreviousStep();
//        } else if (action == StepCallbacks.ACTION_END) {
//            showConfirmExitDialog();
//        } else if (action == StepCallbacks.ACTION_NONE) {
//            // Used when onSaveInstanceState is called of a view. No action is taken.
//        } else {
//            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
//                    "See StepCallbacks for allowable arguments");
//        }
//    }

//    private void hideKeyboard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (imm.isActive() && imm.isAcceptingText()) {
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        }
//    }

//    private void showConfirmExitDialog() {
//        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(
//                "Are you sure you want to exit?")
//                .setMessage(R.string.lorem_medium)
//                .setPositiveButton("End Task", (dialog, which) -> finish())
//                .setNegativeButton("Cancel", null)
//                .create();
//        alertDialog.show();
//    }

//    @Override
//    public void onCancelStep() {
//        setResult(Activity.RESULT_CANCELED);
//        finish();
//    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    TaskResult convert(@NotNull org.researchstack.foundation.core.models.result.TaskResult taskResult) {
        TaskResult tr = new TaskResult(taskResult.getIdentifier());
        tr.setStartDate(toDate(taskResult.getStartTimestamp()));
        tr.setEndDate(toDate(taskResult.getEndTimestamp()));
        for(Map.Entry<String, org.researchstack.foundation.core.models.result.StepResult<?>> e : taskResult.getResults().entrySet()) {
            tr.setStepResultForStepIdentifier(e.getKey(),getResultFactory().create(e.getValue()));
        }

        return tr;
    }

    @Override
    public void onTaskExit(@NotNull Status status, @NotNull org.researchstack.foundation.core.models.result.TaskResult taskResult) {
        if (status == Status.CANCELLED) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (status == Status.FINISHED) {
            TaskResult tr = convert(taskResult);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_TASK_RESULT, tr);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
