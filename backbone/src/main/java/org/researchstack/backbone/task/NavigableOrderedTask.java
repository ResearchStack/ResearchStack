package org.researchstack.backbone.task;

import android.util.Log;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.result.TaskResultSource;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 12/29/16.
 */

public class NavigableOrderedTask extends OrderedTask implements TaskResultSource {

    static final String LOG_TAG = NavigableOrderedTask.class.getCanonicalName();

    public NavigableOrderedTask(String identifier, List<Step> steps) {
        super(identifier, steps);
        orderedStepIdentifiers = new ArrayList<>();
    }

    public NavigableOrderedTask(String identifier, Step... steps) {
        super(identifier, steps);
        orderedStepIdentifiers = new ArrayList<>();
    }

    List<TaskResult> additionalTaskResults;
    ConditionalRule  conditionalRule;

    private List<String> orderedStepIdentifiers;

    private SubtaskStep subtaskStep(String identifier) {
        // Look for a period in the range of the string
        if (identifier == null) {
            //Log.d(LOG_TAG, "Identifier is null, cannot find subtask step");
            return null;
        }

        // Parse out the subtask identifier and look in super for a step with that identifier
        int indexOfPeriod = identifier.indexOf(".");
        if (indexOfPeriod < 0) {
            //Log.d(LOG_TAG, "Identifier has no substep deliminator, aka a period");
            return null;
        }

        String subtaskStepIdentifier = identifier.substring(0, indexOfPeriod);
        Step subtaskStep = super.getStepWithIdentifier(subtaskStepIdentifier);
        if (subtaskStep instanceof SubtaskStep) {
            return (SubtaskStep)subtaskStep;
        }
        return null; // Wasnt an instance of SubtaskStep
    }

    private Step superStepAfterStep(Step step, TaskResult result) {
        // Check the conditional rule to see if it returns a next step for the given previous
        // step and return that with an early exit if applicable.
        if (conditionalRule != null) {
            Step nextStep = conditionalRule.nextStep(step, null, result);
            if (nextStep != null) {
                return nextStep;
            }
        }

        Step returnStep;
        Step previousStep = step;
        boolean shouldSkip;

        do {
            do {

                if (previousStep instanceof NavigationRule) {
                    NavigationRule navigableStep = (NavigationRule)previousStep;
                    String nextStepIdentifier = navigableStep.nextStepIdentifier(result, additionalTaskResults);
                    // If this is a step that conforms to the SBANavigableStep protocol and
                    // the next step identifier is non-nil then get the next step by looking within
                    // the steps associated with this task
                    returnStep = super.getStepWithIdentifier(nextStepIdentifier);
                } else {
                    // If we've dropped through without setting the return step to something non-nil
                    // then look to super for the next step
                    returnStep = super.getStepAfterStep(previousStep, result);
                }

                // Check if this is a skip-able step
                if (returnStep instanceof NavigationSkipRule &&
                   ((NavigationSkipRule)returnStep).shouldSkipStep(result, additionalTaskResults))
                {
                    shouldSkip = true;
                    previousStep = returnStep;
                } else {
                    shouldSkip = false;
                }

            } while (shouldSkip);

            // If the superclass returns a step of type subtask step, then get the first step from the subtask
            // Since it is possible that the subtask will return an empty task (all steps are invalid) then
            // need to also check that the return is non-nil
            while (returnStep instanceof SubtaskStep) {
                SubtaskStep subtaskStep = (SubtaskStep)returnStep;
                Step subtaskReturnStep = subtaskStep.getStepAfterStep(null, result);
                if (subtaskReturnStep != null) {
                    returnStep = subtaskReturnStep;
                } else {
                    returnStep = super.getStepAfterStep(subtaskStep, result);
                }
            }

            // Check to see if this is a conditional step that *should* be skipped
            if (conditionalRule != null) {
                shouldSkip = conditionalRule.shouldSkip(returnStep, result);
            } else {
                shouldSkip = false;
            }

            if (!shouldSkip && returnStep instanceof NavigationSkipRule) {
                shouldSkip = ((NavigationSkipRule)returnStep).shouldSkipStep(result, additionalTaskResults);
            }

            if (shouldSkip) {
                previousStep = returnStep;
            }

        } while (shouldSkip);

        // If there is a conditionalRule, then check to see if the step should be mutated or replaced
        if (conditionalRule != null) {
            returnStep = conditionalRule.nextStep(null, returnStep, result);
        }

        return returnStep;
    }

    // MARK: ORKOrderedTask overrides
    /**
     * Returns the next step immediately after the passed in step in the list of steps, or null
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> after the passed in step, or null if at the end
     */
    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step returnStep;

        String stepIdentifier = step != null ? step.getIdentifier() : null;

        // Look to see if this has a valid subtask step associated with this step
        SubtaskStep subtaskStep = subtaskStep(stepIdentifier);
        if (subtaskStep != null) {
            returnStep = subtaskStep.getStepAfterStep(step, result);
            if (returnStep == null) {
                // If the subtask returns nil then it is at the last step
                // Check super for more steps
                returnStep = superStepAfterStep(subtaskStep, result);
            }
        } else {
            // If this isn't a subtask step then look to super nav for the next step
            returnStep = superStepAfterStep(step, result);
        }

        // Look for step in the ordered steps and remove all items in the list after this one
        String previousIdentifier = stepIdentifier;
        int idx = -1;
        if (previousIdentifier != null) {
            idx = orderedStepIdentifiers.indexOf(previousIdentifier);
            if (idx >= 0 && idx < (orderedStepIdentifiers.size() - 1)) {
                orderedStepIdentifiers = orderedStepIdentifiers.subList(idx + 1, orderedStepIdentifiers.size());
            }
        }

        String identifier = null;
        if (returnStep != null) {
            identifier = returnStep.getIdentifier();
        }

        if (identifier != null) {
            int indexOfId = orderedStepIdentifiers.indexOf(identifier);
            if (indexOfId >= 0) {
                orderedStepIdentifiers = orderedStepIdentifiers.subList(idx, orderedStepIdentifiers.size());
            }
            orderedStepIdentifiers.add(identifier);
        }

        return returnStep;
    }

    /**
     * Returns the next step immediately before the passed in step in the list of steps, or null
     *
     * @param step   The reference step.
     * @param result A snapshot of the current set of results.
     * @return the next step in <code>steps</code> before the passed in step, or null if at the
     * start
     */
    @Override
    public Step getStepBeforeStep(Step step, TaskResult result) {
        if (step.getIdentifier() == null) {
            //Log.e(LOG_TAG, "Found step with null identifier");
            return null;
        }

        int idx = orderedStepIdentifiers.indexOf(step.getIdentifier());
        if (idx < 0) {
            //Log.d(LOG_TAG, "Couldnt find step in orderedStepIdentifiers");
            return null;
        }

        String previousIdentifier = orderedStepIdentifiers.get(idx-1);
        return getStepWithIdentifier(previousIdentifier);
    }

    @Override
    public Step getStepWithIdentifier(String identifier) {
        // Look for the step in the superclass
        Step step = super.getStepWithIdentifier(identifier);
        if (step != null) {
            return step;
        }
        // If not found check to see if it is a substep
        SubtaskStep subtaskStep = subtaskStep(identifier);
        if (subtaskStep == null) {
            Log.d(LOG_TAG, "No step with identifier found " + identifier);
            return null;
        }
        return subtaskStep.getStepWithIdentifier(identifier);
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
        // iOS has this return no progress ever because you truly can't predict it
        // But, it will be helpful still as an estimate of progress
        // so let's just estimate the progress by calling ordered task's progress
        return super.getProgressOfCurrentStep(step, result);
    }

    /**
     * Validates that there are no duplicate identifiers in the list of steps
     * @throws org.researchstack.backbone.task.Task.InvalidTaskException
     */
    @Override
    public void validateParameters() {
        super.validateParameters();
        for (Step step : steps) {
            // Check if the step is a subtask step and validate parameters
            if (step instanceof SubtaskStep) {
                ((SubtaskStep)step).getSubtask().validateParameters();
            }
        }
    }

// TODO: may need this when we add audio support
//    override open var providesBackgroundAudioPrompts: Bool {
//        let superRet = super.providesBackgroundAudioPrompts
//        if (superRet) {
//            return true
//        }
//        for step in self.steps {
//            // Check if the step is a subtask step and validate parameters
//            if let subtaskStep = step as? SBASubtaskStep,
//                    let subRet = subtaskStep.subtask.providesBackgroundAudioPrompts , subRet {
//                return true
//            }
//        }
//        return false
//    }


    // MARK: TaskResultSource overrides
    TaskResult initialResult;

    public Map<String, StepResult> getStoredTaskResults() {
        if (initialResult == null) {
            initialResult = new TaskResult(getIdentifier());
            initialResult.setResults(new HashMap<>());
        }
        return initialResult.getResults();
    }

    public void appendInitialResults(StepResult result) {
        Map<String, StepResult> results = getStoredTaskResults();
        results.put(result.getIdentifier(), result);
        initialResult.setResults(results);
    }

    public void appendInitialResults(Map<String, StepResult> contentsOf) {
        Map<String, StepResult> results = getStoredTaskResults();
        results.putAll(contentsOf);
        initialResult.setResults(results);
    }

    public StepResult getStepResult(String stepIdentifier) {
        // If there is an initial result then return that
        if (initialResult != null) {
            StepResult result = initialResult.getStepResult(stepIdentifier);
            if (result != null) {
                return result;
            }
        }
        // Otherwise, look at the substeps
        SubtaskStep subtaskStep = subtaskStep(stepIdentifier);
        if (subtaskStep == null) {
            return null;
        }
        return subtaskStep.getStepResult(stepIdentifier);
    }

    // TODO: do we need all this?
    // MARK: NSCopy
//
//    override open func copy(with zone: NSZone? = nil) -> Any {
//        let copy = super.copy(with: zone)
//        guard let task = copy as? SBANavigableOrderedTask else { return copy }
//        task.additionalTaskResults = self.additionalTaskResults
//        task.orderedStepIdentifiers = self.orderedStepIdentifiers
//        task.conditionalRule = self.conditionalRule
//        task.initialResult = self.initialResult
//        return task
//    }
//
    // MARK: NSCoding
//
//    required public init(coder aDecoder: NSCoder) {
//        self.additionalTaskResults = aDecoder.decodeObject(forKey: #keyPath(additionalTaskResults)) as? [ORKTaskResult]
//        self.orderedStepIdentifiers = aDecoder.decodeObject(forKey: #keyPath(orderedStepIdentifiers)) as! [String]
//        self.conditionalRule = aDecoder.decodeObject(forKey: #keyPath(conditionalRule)) as? SBAConditionalRule
//        self.initialResult = aDecoder.decodeObject(forKey: #keyPath(initialResult)) as? ORKTaskResult
//        super.init(coder: aDecoder);
//    }
//
//    override open func encode(with aCoder: NSCoder) {
//        super.encode(with: aCoder)
//        aCoder.encode(self.additionalTaskResults, forKey: #keyPath(additionalTaskResults))
//        aCoder.encode(self.conditionalRule, forKey: #keyPath(conditionalRule))
//        aCoder.encode(self.orderedStepIdentifiers, forKey: #keyPath(orderedStepIdentifiers))
//        aCoder.encode(self.initialResult, forKey: #keyPath(initialResult))
//    }
//
//    // MARK: Equality
//
//    override open func isEqual(_ object: Any?) -> Bool {
//        guard let object = object as? SBANavigableOrderedTask else { return false }
//        return super.isEqual(object) &&
//                SBAObjectEquality(self.additionalTaskResults, object.additionalTaskResults) &&
//                SBAObjectEquality(self.orderedStepIdentifiers, object.orderedStepIdentifiers) &&
//                SBAObjectEquality(self.conditionalRule as? NSObject, object.conditionalRule as? NSObject) &&
//                SBAObjectEquality(self.initialResult, self.initialResult)
//    }
//
//    override open var hash: Int {
//        return super.hash ^
//                SBAObjectHash(self.additionalTaskResults) ^
//                SBAObjectHash(self.orderedStepIdentifiers) ^
//                SBAObjectHash(self.conditionalRule) ^
//                SBAObjectHash(self.initialResult)
//    }

    /**
     * Define the navigation rule as a protocol to allow for protocol-oriented extention (multiple inheritance).
     * Currently defined usage is to allow the SBANavigableOrderedTask to check if a step has a navigation rule.
     */
    public interface NavigationRule {
        String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults);
    }

    /**
     * A navigation skip rule applies to this step to allow that step to be skipped.
     */
    public interface NavigationSkipRule {
        boolean shouldSkipStep(TaskResult result, List<TaskResult> additionalTaskResults);
    }

    /**
     * A conditional rule is appended to the navigable task to check a secondary source for whether or not the
     * step should be displayed.
     */
    public interface ConditionalRule {
        boolean shouldSkip(Step step, TaskResult result);
        Step nextStep(Step previousStep, Step nextStep, TaskResult result);
    }
}


