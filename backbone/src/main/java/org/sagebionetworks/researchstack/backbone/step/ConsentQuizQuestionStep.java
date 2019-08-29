package org.sagebionetworks.researchstack.backbone.step;

import org.sagebionetworks.researchstack.backbone.R;
import org.sagebionetworks.researchstack.backbone.model.ConsentQuizModel;
import org.sagebionetworks.researchstack.backbone.ui.layout.ConsentQuizQuestionStepLayout;

@Deprecated // Use NavigationFormStep or NavigationSubtaskStep instead
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
