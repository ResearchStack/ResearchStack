package org.sagebionetworks.researchstack.backbone.utils;

import android.util.Log;

import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.result.TaskResult;
import org.sagebionetworks.researchstack.backbone.step.FormStep;
import org.sagebionetworks.researchstack.backbone.step.NavigationExpectedAnswerQuestionStep;
import org.sagebionetworks.researchstack.backbone.step.QuestionStep;
import org.sagebionetworks.researchstack.backbone.step.Step;
import org.sagebionetworks.researchstack.backbone.step.SubtaskStep;
import org.sagebionetworks.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/15/17.
 *
 * TODO: unit tests
 */

public class StepHelper {

    private static final String LOG_TAG = StepHelper.class.getCanonicalName();

    /**
     * /**
     * This helper method is used by navigation steps to share the algorithm of
     * the nextStepIdentifier method implementation
     * @param skipToStepIdentifier the identifier to skip to if conditions pass
     * @param skipIfPassed used to determine skip identifier if quiz is all passed or all failed
     * @param formSteps sub steps to be compared to with expected answers
     * @param result result to search for actual answers to compare to expected answers
     * @param additionalTaskResults additional results
     * @return identifier of next step if conditions are met, null if next step should be determined else where
     */
    public static String navigationFormStepSkipIdentifier(
            String skipToStepIdentifier,
            boolean skipIfPassed,
            List<QuestionStep> formSteps,
            TaskResult result,
            List<TaskResult> additionalTaskResults)
    {
        if (skipToStepIdentifier == null) {
            return null;
        }
        boolean allPassed = true;
        for (QuestionStep step : formSteps) {
            // Only perform search on navigation question steps that have expected answers
            if (step instanceof NavigationExpectedAnswerQuestionStep) {
                NavigationExpectedAnswerQuestionStep navStep = (NavigationExpectedAnswerQuestionStep)step;
                boolean navStepPassed = containsMatchingAnswer(
                        navStep.getExpectedAnswer(), navStep.getIdentifier(),
                        result, additionalTaskResults);
                if (!navStepPassed) {
                    allPassed = false;
                }
            }
        }
        if (allPassed && skipIfPassed ||
                !allPassed && !skipIfPassed)
        {
            return skipToStepIdentifier;
        }
        return null;
    }

    /**
     * @param formStep the form step containing quesiton steps
     * @param result the result of the task so far
     * @return true if form step was skipped optionally (all results are null), false otherwise
     */
    public static boolean wasFormStepSkipped(FormStep formStep, TaskResult result) {

        List<String> stepIdentifiersToCheck = new ArrayList<>();
        stepIdentifiersToCheck.add(formStep.getIdentifier());
        if (formStep.getFormSteps() != null) {
            for (QuestionStep step: formStep.getFormSteps()) {
                stepIdentifiersToCheck.add(step.getIdentifier());
            }
        }

        for (String identifier: stepIdentifiersToCheck) {
            StepResult stepResult = StepResultHelper.findStepResult(result, identifier);
            if (stepResult != null && stepResult.getResult() != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param expectedAnswer expected answer of step
     * @param stepIdentifier the step's identifier
     * @param result task results
     * @param additionalTaskResults additional results
     * @return true if answer in result is our expected answer, false for all other cases
     */
    private static boolean containsMatchingAnswer(
            Object expectedAnswer,
            String stepIdentifier,
            TaskResult result,
            List<TaskResult> additionalTaskResults)
    {
        if (expectedAnswer == null) {
            return false;
        }


        for (String stepId : result.getResults().keySet()) {
            StepResult stepResult = StepResultHelper.findStepResult(result.getStepResult(stepId), stepIdentifier);
            // We find an ID match
            if (stepResult != null) {
                if (!stepResult.getResults().isEmpty()) {
                    if (stepResult.getResults().size() > 1) {
                        Log.d(LOG_TAG, "This is currently only supported for " +
                                "StepResults with one result, looking at first result instead");
                    }
                    Object answer = stepResult.getResults().values().toArray()[0];
                    return expectedAnswer.equals(answer);
                }
            }
        }
        return false;
    }

    /**
     * @param stepList to be searched for step identifier
     * @param stepId the search parameter
     * @return the step in the list matching stepId, null if none found with stepId
     */
    public static Step getStepWithIdentifier(List<? extends Step> stepList, String stepId) {
        if (stepList == null || stepId == null) {
            return null;
        }
        for (Step step : stepList) {
            if (stepId.equals(step.getIdentifier())) {
                return step;
            }
            // A step can contain a task itself, so check for this as well
            if (step instanceof SubtaskStep) {
                SubtaskStep subtaskStep = (SubtaskStep)step;
                if (subtaskStep.getSubtask() != null &&
                        subtaskStep.getSubtask() instanceof OrderedTask) {
                    OrderedTask task = (OrderedTask)subtaskStep.getSubtask();
                    Step foundSubtaskStep = getStepWithIdentifier(task.getSteps(), stepId);
                    if (foundSubtaskStep != null) {
                        return foundSubtaskStep;
                    }
                }
            }
        }
        return null;
    }
}
