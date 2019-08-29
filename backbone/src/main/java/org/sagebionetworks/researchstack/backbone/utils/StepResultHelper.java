package org.sagebionetworks.researchstack.backbone.utils;

import org.sagebionetworks.researchstack.backbone.result.Result;
import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.result.TaskResult;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 1/16/17.
 */

public class StepResultHelper {

    /**
     * @param taskResult the TaskResult to search within
     * @param stepResultKey the identifier of the step to find
     * @return a StepResult object within taskResult that has map key stepResultKey, null otherwise
     */
    public static StepResult findStepResult(TaskResult taskResult, String stepResultKey) {
        if (taskResult == null || taskResult.getResults() == null || stepResultKey == null) {
            return null;
        }
        return findStepResult(taskResult.getResults().values(), stepResultKey);
    }

    /**
     * @param stepResultList the stepResultList to search within
     * @param stepResultKey the identifier of the step to find
     * @return a StepResult object within the list of stepResultList that has map key stepResultKey, null otherwise
     */
    public static StepResult findStepResult(Collection<StepResult> stepResultList, String stepResultKey) {
        if (stepResultList == null || stepResultKey == null) {
            return null;
        }
        for (StepResult stepResult : stepResultList) {
            StepResult foundResult = findStepResult(stepResult, stepResultKey);
            if (foundResult != null) {
                return foundResult;
            }
        }
        return null;
    }

    /**
     * @param stepResultList    The list of StepResults to search within
     * @param stepResultKey     The identifier of the step to find
     * @return the index within stepResultList that has map key stepResultKey, -1 otherwise
     */
    public static int indexOfStepResultKey(List<StepResult> stepResultList, String stepResultKey) {
        if (stepResultList == null || stepResultList.isEmpty() || stepResultKey == null) {
            return -1;
        }
        for (int i = 0; i < stepResultList.size(); i++) {
            StepResult stepResult = stepResultList.get(i);
            StepResult foundResult = findStepResult(stepResult, stepResultKey);
            if (foundResult != null) {
                return i;
            }
        }
        return -1;
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
     * Only works with the DEFAULT Result identifier keys
     * @param taskResult the TaskResult to search within
     * @param stepIdentifier for result
     * @return String object if exists, empty string otherwise
     */
    public static String findStringResult(TaskResult taskResult, String stepIdentifier) {
        if (taskResult == null || taskResult.getResults() == null || stepIdentifier == null) {
            return null;
        }
        for (StepResult stepResult : taskResult.getResults().values()) {
            String stringResult = findStringResult(stepIdentifier, stepResult);
            if (stringResult != null) {
                return stringResult;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
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
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param stepResult the step result to try and find the boolean result in
     * @return String object if exists, empty string otherwise
     */
    public static Boolean findBooleanResult(String stepIdentifier, StepResult stepResult) {
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
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param taskResult the task result to try and find the boolean result in
     * @return String object if exists, empty string otherwise
     */
    public static Boolean findBooleanResult(String stepIdentifier, TaskResult taskResult) {
        if (taskResult == null || taskResult.getResults() == null || stepIdentifier == null) {
            return null;
        }
        for (StepResult stepResult : taskResult.getResults().values()) {
            Boolean stringResult = findBooleanResult(stepIdentifier, stepResult);
            if (stringResult != null) {
                return stringResult;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param stepResult the step result to try and find the Number result in
     * @return Number object if exists, empty string otherwise
     */
    public static Number findNumberResult(String stepIdentifier, StepResult stepResult) {
        StepResult idStepResult = findStepResult(stepResult, stepIdentifier);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Number) {
                return (Number) resultValue;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param taskResult the task result to try and find the Number result in
     * @return Number object if exists, empty string otherwise
     */
    public static Number findNumberResult(String stepIdentifier, TaskResult taskResult) {
        if (taskResult == null || taskResult.getResults() == null || stepIdentifier == null) {
            return null;
        }
        for (StepResult stepResult : taskResult.getResults().values()) {
            Number result = findNumberResult(stepIdentifier, stepResult);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param stepResult the step result to try and find the integer result in
     * @return Integer object if exists, empty string otherwise
     */
    public static Integer findIntegerResult(String stepIdentifier, StepResult stepResult) {
        StepResult idStepResult = findStepResult(stepResult, stepIdentifier);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Integer) {
                return (Integer) resultValue;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param taskResult the task result to try and find the integer result in
     * @return Integer object if exists, empty string otherwise
     */
    public static Integer findIntegerResult(String stepIdentifier, TaskResult taskResult) {
        if (taskResult == null || taskResult.getResults() == null || stepIdentifier == null) {
            return null;
        }
        for (StepResult stepResult : taskResult.getResults().values()) {
            Integer result = findIntegerResult(stepIdentifier, stepResult);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
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

    /**
     * Only works with the DEFAULT Result identifier keys
     * @param stepIdentifier for result
     * @param taskResult the task result to try and find the date result in
     * @return Date object if exists, empty string otherwise
     */
    public static Date findDateResult(String stepIdentifier, TaskResult taskResult) {
        if (taskResult == null || taskResult.getResults() == null || stepIdentifier == null) {
            return null;
        }
        for (StepResult stepResult : taskResult.getResults().values()) {
            Date result = findDateResult(stepIdentifier, stepResult);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Only works with the DEFAULT Result identifier keys
     * Will find the first Result with a specific class type from within a StepResult
     * @param stepResult the step result to search within
     * @param comparator a class comparator that will be provided by the caller
     *                   this is how you check if the type is the one provided
     * @param <T>        a generic type of class that can be used to avoid casting of return type
     * @return null if result object of type exists somewhere, the object otherwise
     */
    @SuppressWarnings("unchecked")  // needed for unchecked generic type casting
    public static <T extends Result> T findResultOfClass(StepResult stepResult, ResultClassComparator<T> comparator) {
        if (stepResult == null) {
            return null;
        }
        Map results = stepResult.getResults();
        for (Object stepId : results.keySet()) {
            Object value = results.get(stepId);
            if (comparator.isTypeOfClass(value)) {
                return (T)value;
            } else if (value instanceof StepResult) {
                StepResult substepResult = (StepResult)value;
                T recursiveResult = findResultOfClass(substepResult, comparator);
                if (recursiveResult != null) {
                    return recursiveResult;
                }
            }
        }
        return null;
    }

    /**
     * @param subtaskId the id of the parent subtask that contains the nested step result
     * @param stepResultId the step result id
     * @return the fully qualified identifier for use within StepResultHelper functions
     */
    public static String subtaskIdentifier(String subtaskId, String stepResultId) {
        return subtaskId + "." + stepResultId;
    }

    public static abstract class ResultClassComparator<T extends Result> {
        public abstract boolean isTypeOfClass(Object object);
    }
}
