package org.researchstack.backbone.model.survey;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;

import java.util.List;

/**
 * Created by TheMDP on 12/31/16.
 */

public interface NavigationStep {
    String getNextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults);
    QuestionStep matchingSurveyStep(StepResult result);

    // Step identifier to go to if the quiz passed
    String getSkipToStepIdentifier();
    void setSkipToStepIdentifier(String identifier);

    // Should the rule skip if results match expected
    boolean getSkipIfPassed();
    void setSkipIfPassed(boolean skipIfPassed);
}
