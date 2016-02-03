package co.touchlab.researchstack.skin.step;

import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.model.ConsentQuizModel;
import co.touchlab.researchstack.skin.ui.scene.ConsentQuizQuestionStepLayout;

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
    public Class getSceneClass()
    {
        return ConsentQuizQuestionStepLayout.class;
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
