package org.researchstack.backbone.interop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.NotNull;
import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
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

/**
 * Replicates the behavior of ViewTaskActivity while running :backbone tasks on :foundation.
 */
public class ViewBackboneInteropTaskActivity extends PinCodeActivity implements TaskPresentationFragment.OnTaskExitListener {
    public static final int CONTENT_VIEW_ID = R.id.rsb_content_container;

    /**
     * @param context application context
     * @param task    the backbone task to run
     * @return intent to launch the backbone task
     */
    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ViewBackboneInteropTaskActivity.class);
        intent.putExtra(ViewTaskActivity.EXTRA_TASK, task);
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

        Step currentStep = null;
        Task task;
        TaskResult taskResult = null;
        if (savedInstanceState == null) {
            task = (Task) getIntent().getSerializableExtra(ViewTaskActivity.EXTRA_TASK);
        } else {
            task = (Task) savedInstanceState.getSerializable(ViewTaskActivity.EXTRA_TASK);
            taskResult = (TaskResult) savedInstanceState.getSerializable(ViewTaskActivity.EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(ViewTaskActivity.EXTRA_STEP);
        }

        if (savedInstanceState == null) {
            taskFragment = create(task, taskResult, currentStep);
        }

        task.validateParameters();
    }


    @Override
    public void onDataReady() {
        super.onDataReady();

        // wait for data ready and then add TaskPrsentationFragment to the view hierarchy
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(CONTENT_VIEW_ID, taskFragment).commit();
    }

    @Override
    public void onDataFailed() {
        super.onDataFailed();
        Toast.makeText(this, R.string.rsb_error_data_failed, Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void onTaskExit(@NotNull Status status, @NotNull org.researchstack.foundation.core.models.result.TaskResult taskResult) {
        // Send back task finish signal same way as ViewTaskActivity does in backbone
        if (status == Status.CANCELLED) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (status == Status.FINISHED) {
            TaskResult tr = convert(taskResult);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(ViewTaskActivity.EXTRA_TASK_RESULT, tr);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    // region first stab at dependencies

    //
    // work on progress, fleshing out mappers/factories
    //

    @VisibleForTesting
    TaskResult convert(@NotNull org.researchstack.foundation.core.models.result.TaskResult taskResult) {
        TaskResult tr = new TaskResult(taskResult.getIdentifier());
        tr.setStartDate(toDate(taskResult.getStartTimestamp()));
        tr.setEndDate(toDate(taskResult.getEndTimestamp()));
        for (Map.Entry<String, org.researchstack.foundation.core.models.result.StepResult<?>> e : taskResult.getResults().entrySet()) {
            tr.setStepResultForStepIdentifier(e.getKey(), getResultFactory().create(e.getValue()));
        }

        return tr;
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

    // endregion
}
