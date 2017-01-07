package org.researchstack.backbone.step;

import android.util.Log;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.result.TaskResultSource;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.utils.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 12/29/16.
 */

public class SubtaskStep extends Step {

    static final String LOG_TAG = SubtaskStep.class.getCanonicalName();

    Task subtask;
    public Task getSubtask() {
        return subtask;
    }

    public SubtaskStep(String identifier) {
        super(identifier);
    }

    public SubtaskStep(String identifier, String title) {
        super(identifier, title);
    }

    public SubtaskStep(String identifier, List<Step> steps) {
        this(identifier);
        subtask = new OrderedTask(identifier, steps);
    }

    public SubtaskStep(Task task) {
        this(task.getIdentifier());
        subtask = task;
    }

    private String substepIdentifier(String identifier) {
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
        Step replacementStep = step.clone(replacementIdentifier);
        return replacementStep;
    }

    private TaskResult filteredTaskResult(TaskResult inputResult) {
        // create a mutated copy of the results that includes only the subtask results
        TaskResult subtaskResult = inputResult.copy();
        Map<String, StepResult> stepResults = subtaskResult.getResults();
        subtaskResult.getResults().clear();
        for (String identifier : stepResults.keySet()) {
            subtaskResult.setStepResultForStepIdentifier(identifier, stepResults.get(identifier));
        }
        return subtaskResult;
    }

    private Map<String, StepResult> filteredStepResults(Map<String, StepResult> inputResults) {
        Map<String, StepResult> subtaskResults = new HashMap<>();
        for (String identifier : inputResults.keySet()) {
            if (identifier.startsWith(subtask.getIdentifier())) {
                // TODO: iOS does a deep copy, I'm not sure if we need to
                StepResult inStepResult = inputResults.get(identifier);
                String newIdentifier = identifier.substring(subtask.getIdentifier().length());
                StepResult stepResult = inStepResult.clone(newIdentifier);

                // Search results of the step for non-subtask identifiers as well
                if (stepResult.getResults() != null) {
                    Map<String, Object> subtaskStepResults = new HashMap<>();
                    for (String stepResultIdentifier : inputResults.keySet()) {
                        Object stepResultObject = stepResult.getResults().get(stepResultIdentifier);
                        int indexOfId = stepResultIdentifier.indexOf(subtask.getIdentifier());
                        if (indexOfId < 0) {
                            subtaskStepResults.put(stepResultIdentifier, stepResultObject);
                        } else {
                            String stepResultNewIdentifier = stepResultIdentifier.substring(
                                    indexOfId + subtask.getIdentifier().length());
                            subtaskStepResults.put(stepResultNewIdentifier, stepResultObject);
                        }
                    }
                    stepResult.setResults(subtaskStepResults);
                }

                subtaskResults.put(newIdentifier, stepResult);
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

        Step substep = step.clone(substepIdentifier);
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

    public StepResult getStepResult(String stepIdentifier) {
        String substepIdentifier = substepIdentifier(stepIdentifier);
        if (substepIdentifier == null) {
            return null;
        }
        if (subtask instanceof TaskResultSource) {
            return ((TaskResultSource)subtask).getStepResult(substepIdentifier);
        }
        return null;
    }

    // TODO: do we need this?
//    override open var requestedPermissions: ORKPermissionMask {
//        if let permissions = self.subtask.requestedPermissions {
//            return permissions
//        }
//        return []
//    }




    // TODO: do we need this?
//    // MARK: NSCopy
//
//    @objc(copyWithSubtask:)
//    open func copy(with subtask: ORKTask & NSCopying & NSSecureCoding) -> SBASubtaskStep {
//        let copy = self.copy() as! SBASubtaskStep
//        copy._subtask = subtask
//        return copy
//    }
//
//    override open func copy(with zone: NSZone? = nil) -> Any {
//        let copy = super.copy(with: zone) as! SBASubtaskStep
//        copy._subtask = _subtask.copy(with: zone) as! ORKTask & NSCopying & NSSecureCoding
//        copy.taskIdentifier = taskIdentifier
//        copy.schemaIdentifier = schemaIdentifier
//        return copy
//    }
//
//    // MARK: NSCoding
//
//    required public init(coder aDecoder: NSCoder) {
//        _subtask = aDecoder.decodeObject(forKey: #keyPath(subtask)) as! ORKTask & NSCopying & NSSecureCoding
//        taskIdentifier = aDecoder.decodeObject(forKey: #keyPath(taskIdentifier)) as? String
//        schemaIdentifier = aDecoder.decodeObject(forKey: #keyPath(schemaIdentifier)) as? String
//        super.init(coder: aDecoder);
//    }
//
//    override open func encode(with aCoder: NSCoder) {
//        super.encode(with: aCoder)
//        aCoder.encode(_subtask, forKey: #keyPath(subtask))
//        aCoder.encode(taskIdentifier, forKey: #keyPath(taskIdentifier))
//        aCoder.encode(schemaIdentifier, forKey: #keyPath(schemaIdentifier))
//    }
//
//    // MARK: Equality
//
//    override open func isEqual(_ object: Any?) -> Bool {
//        guard let object = object as? SBASubtaskStep else { return false }
//        return super.isEqual(object) &&
//                _subtask.isEqual(object._subtask) &&
//                (self.taskIdentifier == object.taskIdentifier) &&
//                (self.schemaIdentifier == object.schemaIdentifier)
//    }
//
//    override open var hash: Int {
//        return super.hash ^
//                SBAObjectHash(self.taskIdentifier) ^
//                SBAObjectHash(self.schemaIdentifier) ^
//                _subtask.hash
//    }
}
