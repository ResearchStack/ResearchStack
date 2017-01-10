package org.researchstack.backbone.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.model.survey.NavigationStep;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;

import java.util.List;

/**
 * Created by TheMDP on 12/31/16.
 */

public class NavigationQuestionStep extends QuestionStep implements NavigationStep {

    String skipToStepIdentifier;
    boolean skipIfPassed;

    public NavigationQuestionStep(String identifier) {
        super(identifier);
    }

    public NavigationQuestionStep(String identifier, String title) {
        super(identifier, title);
    }

    public NavigationQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    @Override
    public String getSkipToStepIdentifier() {
        return skipToStepIdentifier;
    }

    @Override
    public void setSkipToStepIdentifier(String identifier) {
        skipToStepIdentifier = identifier;
    }

    @Override
    public boolean getSkipIfPassed() {
        return skipIfPassed;
    }

    @Override
    public void setSkipIfPassed(boolean skipIfPassed) {
        this.skipIfPassed = skipIfPassed;
    }
}
