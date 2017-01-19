package org.researchstack.backbone.result;

/**
 * Created by TheMDP on 12/30/16.
 */

public interface TaskResultSource {
    /**
     * Returns a step result for the specified step identifier, if one exists.
     * When it's about to present a step, the task view needs to look up a
     * suitable default answer. The answer can be used to prepopulate a survey with
     * the results obtained on a previous run of the same task, by passing a
     * `TaskResult` object (which itself implements this protocol).
     * <p>
     *
     * @param stepIdentifier The identifier for which to search.
     * @return The result for the specified step, or `null` for none.
     */
    StepResult getStepResult(String stepIdentifier);
}
