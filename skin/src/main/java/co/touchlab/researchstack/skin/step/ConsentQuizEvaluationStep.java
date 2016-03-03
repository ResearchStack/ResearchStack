package co.touchlab.researchstack.skin.step;

import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.model.ConsentQuizModel;
import co.touchlab.researchstack.skin.ui.layout.ConsentQuizEvaluationStepLayout;

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
    public int getStepTitle()
    {
        return R.string.rsb_quiz_evaluation;
    }

    @Override
    public Class getStepLayoutClass()
    {
        return ConsentQuizEvaluationStepLayout.class;
    }

    public int getAttempt()
    {
        return attempt;
    }

    public void setAttempt(int attempt)
    {
        this.attempt = attempt;
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

    public boolean isOverMaxAttempts()
    {
        return attempt >= 1;
    }

    public boolean isQuizPassed()
    {
        return questionProperties != null && incorrect < questionProperties.maxIncorrect;
    }
}
