package co.touchlab.touchkit.rk.common.task;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class OrderedTask extends Task implements Parcelable
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

    public OrderedTask(Parcel in)
    {
        super(in.readString());

        steps = in.readArrayList(Step.class.getClassLoader());
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(getIdentifier());
        dest.writeList(steps);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public OrderedTask createFromParcel(Parcel in)
        {
            return new OrderedTask(in);
        }

        public OrderedTask[] newArray(int size)
        {
            return new OrderedTask[size];
        }
    };

}
