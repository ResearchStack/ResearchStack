package co.touchlab.touchkit.rk.common.step;

import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.dev.DevUtils;

public class QuestionStep extends Step
{
    private AnswerFormat              answerFormat;

    private String placeholder;

    private QuestionStep(String identifier, String title)
    {
        super(identifier, title);
        setOptional(true);
        setUseSurveyMode(true);
    }

    public QuestionStep(String identifier, String title, AnswerFormat format)
    {
        super(identifier,
                title);
        this.answerFormat = format;
    }

    @Override
    public Class getStepFragment()
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }

    public AnswerFormat getAnswerFormat() {
        return answerFormat;
    }

    public String getPlaceholder()
    {
        return placeholder;
    }

    public void setPlaceholder(String placeholder)
    {
        this.placeholder = placeholder;
    }

    public AnswerFormat.QuestionType getQuestionType()
    {
        return answerFormat.getQuestionType();
    }
}
