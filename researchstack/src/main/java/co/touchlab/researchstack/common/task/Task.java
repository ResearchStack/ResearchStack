package co.touchlab.researchstack.common.task;

import android.content.Context;

import java.io.Serializable;

import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.Step;

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
        return step.getSceneTitle() != 0 ? context.getString(step.getSceneTitle()) : "";
    }

    public abstract TaskProgress getProgressOfCurrentStep(Step step, TaskResult result);

    /**
     * Throw exception if params are not valid
     */
    public abstract void validateParameters();

    public static class TaskProgress {

        public int current;

        public int total;

    }

    public abstract int getNumberOfSteps();
}
