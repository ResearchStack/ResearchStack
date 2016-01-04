package co.touchlab.researchstack.core.result;

import android.net.Uri;

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

    public StepResult getStepResult(String identifier)
    {
        return results.get(identifier);
    }

    public void setStepResultForStepIdentifier(String identifier, StepResult stepResult)
    {
        results.put(identifier, stepResult);
    }
}
