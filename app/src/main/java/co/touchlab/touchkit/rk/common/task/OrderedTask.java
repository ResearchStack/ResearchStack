package co.touchlab.touchkit.rk.common.task;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class OrderedTask extends Task implements Serializable
{

    private List<Step> steps;

    public OrderedTask(String identifier, List<Step> steps)
    {
        super(identifier);
        this.steps = steps;
    }

    public OrderedTask(String identifier, Step ... steps)
    {
        super(identifier);
        this.steps = Arrays.asList(steps);
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        return null;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        return null;
    }

    @Override
    public Step getStepWithIdentifier(String identifier)
    {
        return null;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result)
    {
        return null;
    }

    @Override
    public void validateParameters()
    {
    }

    public List<Step> getSteps()
    {
        return steps;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof OrderedTask)
        {
            OrderedTask orderedTask = (OrderedTask) o;
            return getIdentifier().equals(orderedTask.getIdentifier()) && steps.equals(
                    orderedTask.getSteps());
        }
        return false;
    }
}
