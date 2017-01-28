package org.researchstack.backbone.utils;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.util.Date;
import java.util.Map;

/**
 * Created by TheMDP on 1/16/17.
 *
 * TODO: unit tests
 */

public class StepResultHelper {

    /**
     * @param taskResult the TaskResult to search within
     * @param stepResultKey the identifier of the step to find
     * @return a StepResult object within taskResult that has map key stepResultKey, null otherwise
     */
    public static StepResult findStepResult(TaskResult taskResult, String stepResultKey) {
        if (taskResult == null || stepResultKey == null) {
            return null;
        }
        for (StepResult stepResult : taskResult.getResults().values()) {
            StepResult foundResult = findStepResult(stepResult, stepResultKey);
            if (foundResult != null) {
                return foundResult;
            }
        }
        return null;
    }

    /**
     * @param result A StepResult object that may or may not have other nested StepResults
     * @param stepResultKey the map key to find
     * @return a StepResult object within result that has map key stepResultKey, null otherwise
     */
    public static StepResult findStepResult(StepResult result, String stepResultKey) {
        if (result == null || stepResultKey == null) {
            return null;
        }
        if (result.getIdentifier().equals(stepResultKey)) {
            return result;
        }
        Map results = result.getResults();
        for (Object stepId : results.keySet()) {
            Object stepResultObj = results.get(stepId);
            if (stepResultObj instanceof StepResult) {
                StepResult stepResult = (StepResult)stepResultObj;
                if (stepResultKey.equals(stepId)) {
                    return stepResult;
                } else {
                    StepResult recursiveStepResult = findStepResult(stepResult, stepResultKey);
                    if (recursiveStepResult != null) {
                        return recursiveStepResult;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param stepIdentifier for result
     * @param stepResult the step result to try and find the String result in
     * @return String object if exists, empty string otherwise
     */
    public static String findStringResult(String stepIdentifier, StepResult stepResult) {
        StepResult idStepResult = findStepResult(stepResult, stepIdentifier);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof String) {
                return (String) resultValue;
            }
        }
        return null;
    }

    /**
     * @param stepIdentifier for result
     * @param stepResult the step result to try and find the boolean result in
     * @param taskResult the task result to try and find the boolean result in
     * @return String object if exists, empty string otherwise
     */
    public static Boolean findBooleanResult(String stepIdentifier, StepResult stepResult, TaskResult taskResult) {
        StepResult idStepResult = findStepResult(stepResult, stepIdentifier);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Boolean) {
                return (Boolean) resultValue;
            }
        }
        return null;
    }

    /**
     * @param stepIdentifier for result
     * @param stepResult the step result to try and find the date result in
     * @return String object if exists, empty string otherwise
     */
    public static Date findDateResult(String stepIdentifier, StepResult stepResult) {
        StepResult idStepResult = findStepResult(stepResult, stepIdentifier);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Long) {
                return new Date((Long)resultValue);
            }
        }
        return null;
    }
}
