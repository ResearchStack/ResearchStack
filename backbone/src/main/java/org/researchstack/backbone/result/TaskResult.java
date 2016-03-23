package org.researchstack.backbone.result;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskResult extends Result
{
    private Map<String, StepResult> results;

    private UUID uuidTask;

    private Uri outputDirectory;

    public TaskResult(String identifier)
    {
        super(identifier);
        this.results = new HashMap<>();
    }

    public Map<String, StepResult> getResults()
    {
        return results;
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
