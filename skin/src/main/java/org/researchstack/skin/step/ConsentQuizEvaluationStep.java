package org.researchstack.skin.step;

import org.researchstack.backbone.step.Step;
import org.researchstack.skin.R;
import org.researchstack.skin.model.ConsentQuizModel;
import org.researchstack.skin.ui.layout.ConsentQuizEvaluationStepLayout;

public class ConsentQuizEvaluationStep extends Step {
    private ConsentQuizModel quizModel;

    private int attempt;
    private int incorrect;

    public ConsentQuizEvaluationStep(String identifier, ConsentQuizModel quizModel) {
        super(identifier);
        this.quizModel = quizModel;
    }

    @Override
    public int getStepTitle() {
        return R.string.rsb_quiz_evaluation;
    }

    @Override
    public Class getStepLayoutClass() {
        return ConsentQuizEvaluationStepLayout.class;
    }

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public void setIncorrectCount(int incorrect) {
        this.incorrect = incorrect;
    }

    public int getIncorrect() {
        return incorrect;
    }

    public ConsentQuizModel getQuizModel() {
        return quizModel;
    }

    public boolean isOverMaxAttempts() {
        return attempt >= 1;
    }

    public boolean isQuizPassed() {
        return incorrect <= quizModel.getAllowedFailures();
    }
}
