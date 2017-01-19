package org.researchstack.backbone.step;

import android.util.Log;

import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.step.layout.ViewPagerSubstepListStepLayout;
import org.researchstack.backbone.utils.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 12/29/16.
 */

public class SubtaskStep extends Step {

    private static final String LOG_TAG = SubtaskStep.class.getCanonicalName();

    Task subtask;
    public Task getSubtask() {
        return subtask;
    }

    /* Default constructor needed for serilization/deserialization of object */
    SubtaskStep() {
        super();
    }

    public SubtaskStep(String identifier) {
        super(identifier);
    }

    public SubtaskStep(String identifier, String title) {
        super(identifier, title);
    }

    public SubtaskStep(String identifier, List<Step> steps) {
        this(identifier);
        subtask = new NavigableOrderedTask(identifier, steps);
    }

    public SubtaskStep(Task task) {
        this(task.getIdentifier());
        subtask = task;
    }

    /**
     * @param identifier full identifier, i.e. "consent.step1"
     * @return identifier with subtask identifier stripped out, i.e. "step1"
     */
    protected String substepIdentifier(String identifier) {
        if (subtask == null) {
            Log.e(LOG_TAG, "Subtask is null subtask step");
            return null;
        }

        if (identifier == null || subtask.getIdentifier() == null) {
            Log.e(LOG_TAG, "Identifier or subtask identifier is null in subtask step");
            return null;
        }

        // Add a period to the end of the substep
        String baseIdPrefix = subtask.getIdentifier() + ".";
        int startIndex = identifier.indexOf(baseIdPrefix);

        if (startIndex < 0) {
            return null;
        }

        String substepId = identifier.substring(startIndex + baseIdPrefix.length());
        return substepId;
    }

    private Step replacementStep(Step step) {
        if (step == null) {
            //Log.e(LOG_TAG, "Step is null in subtask step method");
            return null;
        }
        String replacementIdentifier = subtask.getIdentifier() + "." + step.getIdentifier();
        Step replacementStep = step.deepCopy(replacementIdentifier);
        return replacementStep;
    }

    protected TaskResult filteredTaskResult(TaskResult inputResult) {
        // create a mutated copy of the results that includes only the subtask results
        TaskResult subtaskResult = (TaskResult) ObjectUtils.clone(inputResult);
        Map<String, StepResult> stepResults = subtaskResult.getResults();
        if (stepResults != null && !stepResults.keySet().isEmpty()) {
            Map<String, StepResult> subtaskResults = filteredStepResults(stepResults);
            subtaskResult.setResults(subtaskResults);
        }
        return subtaskResult;
    }

    private Map<String, StepResult> filteredStepResults(Map<String, StepResult> inputResults) {
        Map<String, StepResult> subtaskResults = new LinkedHashMap<>();
        String prefix = subtask.getIdentifier() + ".";
        for (String identifier : inputResults.keySet()) {
            if (identifier.startsWith(prefix)) {
                
                Map<String, Object> newResultMap = new LinkedHashMap<>();
                String newIdentifier = identifier.substring(prefix.length());
                if (inputResults.get(identifier) != null) {
                    StepResult stepResult = (StepResult) inputResults.get(identifier).deepCopy(newIdentifier);

                    // Search results of the step for non-subtask identifiers as well
                    if (stepResult.getResults() != null) {
                        for (Object stepResultIdentifierObj : stepResult.getResults().keySet()) {
                            if (stepResultIdentifierObj instanceof String) {
                                String stepResultIdentifier = (String) stepResultIdentifierObj;
                                Object stepResultObject = stepResult.getResults().get(stepResultIdentifierObj);
                                if (stepResultObject instanceof Result) {
                                    Result newResult = ((Result) stepResultObject).deepCopy(stepResultIdentifier);
                                    newResultMap.put(stepResultIdentifier, newResult);
                                } else {
                                    newResultMap.put(stepResultIdentifier, stepResultObject);
                                }
                            }
                        }
                        stepResult.setResults(newResultMap);
                    }

                    subtaskResults.put(newIdentifier, stepResult);
                }
            }
        }
        return subtaskResults;
    }

    public Step getStepWithIdentifier(String identifier) {
        String substepIdentifier = substepIdentifier(identifier);
        if (substepIdentifier == null) {
            return null;
        }
        Step step = subtask.getStepWithIdentifier(substepIdentifier);
        if (step == null) {
            return null;
        }
        return replacementStep(step);
    }

    public Step getStepAfterStep(Step step, TaskResult result) {
        if (step == null) {
            return replacementStep(subtask.getStepAfterStep(null, result));
        }
        String substepIdentifier = substepIdentifier(step.getIdentifier());
        if (substepIdentifier == null) {
            return null;
        }

        Step substep = step.deepCopy(substepIdentifier);
        TaskResult replacementTaskResult = filteredTaskResult(result);
        Step nextStep = subtask.getStepAfterStep(substep, replacementTaskResult);

        // If the task result was mutated, need to add any changes back into the result set
        StepResult thisStepResult = replacementTaskResult.getStepResult(substepIdentifier);
        if (thisStepResult != null && result != null) {
            StepResult parentStepResult = result.getStepResult(step.getIdentifier());
            parentStepResult.setResults(thisStepResult.getResults());
        }

        // And finally return the replacement step
        return replacementStep(nextStep);
    }
}
