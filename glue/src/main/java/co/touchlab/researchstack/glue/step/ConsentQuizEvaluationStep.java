package co.touchlab.researchstack.glue.step;

import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.glue.ui.scene.ConsentQuizEvaluationScene;

public class ConsentQuizEvaluationStep extends Step
{
    private ConsentQuizModel.EvaluationProperties questionProperties;

    private int attempt;
    private int incorrect;

    public ConsentQuizEvaluationStep(String identifier, ConsentQuizModel.EvaluationProperties questionProperties)
    {
        super(identifier);
        this.questionProperties = questionProperties;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentQuizEvaluationScene.class;
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.quiz_evaluation;
    }

    public void setAttempt(int attempt)
    {
        this.attempt = attempt;
    }

    public int getAttempt()
    {
        return attempt;
    }

    public void setIncorrectCount(int incorrect)
    {
        this.incorrect = incorrect;
    }

    public int getIncorrect()
    {
        return incorrect;
    }

    public ConsentQuizModel.EvaluationProperties getQuestionProperties()
    {
        return questionProperties;
    }
}
