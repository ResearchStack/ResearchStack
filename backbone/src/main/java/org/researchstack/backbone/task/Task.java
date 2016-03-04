package org.researchstack.backbone.task;

import android.content.Context;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;

import java.io.Serializable;

public abstract class Task implements Serializable
{
    private String identifier;

    public Task()
    {
    }

    public Task(String identifier)
    {
        this.identifier = identifier;
    }


    public String getIdentifier()
    {
        return identifier;
    }

    public abstract Step getStepAfterStep(Step step, TaskResult result);

    public abstract Step getStepBeforeStep(Step step, TaskResult result);

    public abstract Step getStepWithIdentifier(String identifier);

    public String getTitleForStep(Context context, Step step)
    {
        return step.getStepTitle() != 0 ? context.getString(step.getStepTitle()) : "";
    }

    public abstract TaskProgress getProgressOfCurrentStep(Step step, TaskResult result);

    /**
     * Throw exception if params are not valid
     */
    public abstract void validateParameters();

    public abstract int getNumberOfSteps();

    public static class TaskProgress
    {

        private int current;

        private int total;

        public TaskProgress(int current, int total)
        {
            this.current = current;
            this.total = total;
        }

        public int getCurrent()
        {
            return current;
        }

        public int getTotal()
        {
            return total;
        }
    }
}
