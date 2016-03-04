package org.researchstack.skin.step;

import org.researchstack.backbone.step.Step;
import org.researchstack.skin.R;
import org.researchstack.skin.model.ConsentQuizModel;
import org.researchstack.skin.ui.layout.ConsentQuizQuestionStepLayout;

public class ConsentQuizQuestionStep extends Step
{
    private ConsentQuizModel.QuestionProperties properties;
    private ConsentQuizModel.QuizQuestion       question;

    public ConsentQuizQuestionStep(String identifier, ConsentQuizModel.QuestionProperties properties, ConsentQuizModel.QuizQuestion question)
    {
        super(identifier);
        this.properties = properties;
        this.question = question;
    }

    @Override
    public Class getStepLayoutClass()
    {
        return ConsentQuizQuestionStepLayout.class;
    }

    @Override
    public int getStepTitle()
    {
        return R.string.rsb_quiz;
    }

    public ConsentQuizModel.QuestionProperties getProperties()
    {
        return properties;
    }

    public ConsentQuizModel.QuizQuestion getQuestion()
    {
        return question;
    }
}
