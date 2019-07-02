package org.researchstack.backbone.task;

import android.util.Log;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.utils.StepResultHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 12/29/16.
 *
 * NavigableOrderedTask is meant for controlling a list of steps
 * that can be SubtaskSteps, or any other normal step that may or may not
 * implement and of the interfaces defined in this class...
 * Which are NavigationRule, ConditionalRule, NavigationSkipRule
 */

public class NavigableOrderedTask extends OrderedTask {

    static final String LOG_TAG = NavigableOrderedTask.class.getCanonicalName();

    /* Default constructor needed for serilization/deserialization of object */
    public NavigableOrderedTask() {
        super();
    }

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
                    if (nextStepIdentifier == null) {
                        returnStep = super.getStepAfterStep(previousStep, result);
                    } else {
                        returnStep = super.getStepWithIdentifier(nextStepIdentifier);
                    }
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
                orderedStepIdentifiers = new ArrayList<>(orderedStepIdentifiers.subList(0, idx + 1));
            }
        }

        String identifier = null;
        if (returnStep != null) {
            identifier = returnStep.getIdentifier();
        }

        if (identifier != null) {
            int indexOfId = orderedStepIdentifiers.indexOf(identifier);
            if (indexOfId >= 0) {
                orderedStepIdentifiers = new ArrayList<>(orderedStepIdentifiers.subList(0, indexOfId));
            } else {
                orderedStepIdentifiers.add(identifier);
            }
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

        int prevIdx = idx - 1;
        if (prevIdx >= 0) {
            String previousIdentifier = orderedStepIdentifiers.get(prevIdx);
            return getStepWithIdentifier(previousIdentifier);
        }

        return null;
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
     * @throws org.researchstack.backbone.task.Task.InvalidTaskException if parameters are invalid
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

    /**
     * Define the navigation rule as an interface to allow for protocol-oriented extention (multiple inheritance).
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

    /**
     * This class is used to enable a simple navigation pattern for Steps
     * The standard usage is to apply a TaskResult to it and look for the nextIdentifier
     */
    public static class ObjectEqualsNavigationRule implements NavigationRule, Serializable {
        private Object navigationResult;
        private String navigationIdentifier;
        private String resultIdentifier;

        /** Default constructor needed for serializable interface */
        ObjectEqualsNavigationRule() {
            super();
        }

        /**
         * @param navigationResult the expected result to enable navigation
         * @param navigationIdentifier the navigation step identifier to go to if result is matched
         * @param resultIdentifier the identifier of the result to find
         */
        public ObjectEqualsNavigationRule(
                Object navigationResult,
                String navigationIdentifier,
                String resultIdentifier)
        {
            this.navigationResult = navigationResult;
            this.navigationIdentifier = navigationIdentifier;
            this.resultIdentifier = resultIdentifier;
        }

        public Object getNavigationResult() {
            return navigationResult;
        }

        public String getNavigationIdentifier() {
            return navigationIdentifier;
        }

        public String getResultIdentifier() {
            return resultIdentifier;
        }

        @Override
        public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
            if (navigationResult != null && navigationIdentifier != null && resultIdentifier != null) {
                StepResult stepResult = StepResultHelper.findStepResult(result, resultIdentifier);
                if (stepResult != null &&
                    stepResult.getResult() != null &&
                    stepResult.getResult().equals(navigationResult))
                {
                    return navigationIdentifier;
                }
            }
            return null;
        }
    }
}


