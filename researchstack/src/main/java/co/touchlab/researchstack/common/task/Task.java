package co.touchlab.researchstack.common.task;

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
