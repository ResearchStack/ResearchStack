package co.touchlab.researchstack.glue.step;

import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.ui.scene.SingleChoiceQuestionBody;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;

public class ConsentQuizQuestionStep extends QuestionStep
{
    private ConsentQuizModel.QuestionProperties properties;
    private ConsentQuizModel.QuizQuestion question;

    public ConsentQuizQuestionStep(String identifier, ConsentQuizModel.QuestionProperties properties,  ConsentQuizModel.QuizQuestion question)
    {
        super(identifier);
        this.properties = properties;
        this.question = question;
    }

    @Override
    public Class getSceneClass()
    {
        return SingleChoiceQuestionBody.class;
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.rsc_quiz;
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
