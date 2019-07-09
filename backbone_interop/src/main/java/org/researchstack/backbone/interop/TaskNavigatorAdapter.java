package org.researchstack.backbone.interop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.researchstack.backbone.task.Task;
import org.researchstack.foundation.components.presentation.interfaces.ITaskNavigator;
import org.researchstack.foundation.core.interfaces.IResult;
import org.researchstack.foundation.core.interfaces.IStep;
import org.researchstack.foundation.core.interfaces.ITask;
import org.researchstack.foundation.core.models.task.Task.TaskProgress;

public class TaskNavigatorAdapter implements ITaskNavigator, ITask {

    private final Task task;

    public TaskNavigatorAdapter(Task task) {
        this.task = task;
    }

    @NotNull
    @Override
    public TaskProgress getProgressOfCurrentStep(@NotNull final IStep step, @NotNull final IResult result) {
        return null;
    }

    @Nullable
    @Override
    public IStep getStepAfterStep(@Nullable final IStep step, @NotNull final IResult result) {
        return null;
    }

    @Nullable
    @Override
    public IStep getStepBeforeStep(@Nullable final IStep step, @NotNull final IResult result) {
        return null;
    }

    @Nullable
    @Override
    public IStep getStepWithIdentifier(@NotNull final String identifier) {
        return null;
    }

    @Override
    public void validateParameters() {

    }

    @NotNull
    @Override
    public String getIdentifier() {
        return null;
    }
}
