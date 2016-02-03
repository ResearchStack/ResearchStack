package co.touchlab.researchstack.backbone.result;

import java.util.HashMap;
import java.util.Map;

public class TaskResult extends Result
{
    private Map<String, StepResult> results;

    // TODO Implement private UUID uuidTask;

    // TODO Implement private Uri outputDirectory;

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
