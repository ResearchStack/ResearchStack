package co.touchlab.touchkit.rk.common.result;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskResult extends Result
{
    public Map<String, StepResult> results;

    private UUID uuidTask; //TODO Implement
    private Uri  outputDirectory; //TODO Implement

    public TaskResult(String identifier, UUID uuidTask, Uri outputDirectory)
    {
        super(identifier);
        this.uuidTask = uuidTask;
        this.outputDirectory = outputDirectory;
        this.results = new HashMap<>();
    }

    public TaskResult(Parcel in)
    {
        super(in);
        Bundle bundle = in.readBundle(StepResult.class.getClassLoader());
        results = new HashMap<>();
        for (String key : bundle.keySet())
        {
            results.put(key,
                    (StepResult) bundle.getParcelable(key));
        }
    }

    public UUID getUuidTask()
    {
        return uuidTask;
    }

    public Uri getOutputDirectory()
    {
        return outputDirectory;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof TaskResult)
        {
            TaskResult result = (TaskResult) o;
            return getIdentifier().equals(result.getIdentifier()) &&
                    uuidTask.equals(result.getUuidTask()) &&
                    outputDirectory.equals(result.getOutputDirectory());

        }
        return false;
    }

    public StepResult getStepResultForStepIdentifier(String identifier)
    {
        // TODO fix this casting
        return results.get(identifier);
    }

    public void setStepResultForStepIdentifier(String identifier, StepResult stepResult)
    {
        results.put(identifier,
                stepResult);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest,
                flags);
        Bundle bundle = new Bundle();
        for(Map.Entry<String, StepResult> entry : results.entrySet())
        {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
        dest.writeBundle(bundle);
    }

    public static final Creator<TaskResult> CREATOR = new Creator<TaskResult>()
    {
        @Override
        public TaskResult createFromParcel(Parcel in)
        {
            return new TaskResult(in);
        }

        @Override
        public TaskResult[] newArray(int size)
        {
            return new TaskResult[size];
        }
    };
}
