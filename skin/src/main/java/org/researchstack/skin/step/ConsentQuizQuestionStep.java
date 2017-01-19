package org.researchstack.skin.step;

import org.researchstack.backbone.step.Step;
import org.researchstack.skin.R;
import org.researchstack.skin.model.ConsentQuizModel;
import org.researchstack.skin.ui.layout.ConsentQuizQuestionStepLayout;

public class ConsentQuizQuestionStep extends Step {
    private ConsentQuizModel.QuizQuestion question;

    public ConsentQuizQuestionStep(ConsentQuizModel.QuizQuestion question) {
        super(question.getIdentifier());
        this.question = question;
    }

    @Override
    public Class getStepLayoutClass() {
        return ConsentQuizQuestionStepLayout.class;
    }

    @Override
    public int getStepTitle() {
        return R.string.rsb_quiz;
    }

    @Override
    public String getTitle() {
        return question.getPrompt();
    }

    public ConsentQuizModel.QuizQuestion getQuestion() {
        return question;
    }
}
