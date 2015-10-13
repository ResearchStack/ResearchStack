package co.touchlab.touchkit.rk.common.answerformat;

import java.io.Serializable;

import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.dev.DevUtils;

public abstract class AnswerFormat implements Serializable
{

    public static AnswerFormat getChoiceAnswerFormatWithStyle(ChoiceAnswerStyle choiceAnswerStyle, TextChoice[] textChoices)
    {
        return null;
    }

    public enum QuestionType
    {
        None,
        Scale,
        SingleChoice,
        MultipleChoice,
        Decimal,
        Integer,
        Boolean,
        Text,
        TimeOfDay,
        DateAndTime,
        Date,
        TimeInterval
    }

    public enum ChoiceAnswerStyle
    {
        SingleChoice,
        MultipleChoice;
    }

    public enum NumberFormattingStyle
    {
        Default,
        Percent;
    }

    public AnswerFormat()
    {
    }

    public QuestionType getQuestionType()
    {
        return QuestionType.None;
    }

    //TODO figure out if this makes sense
    public Class getQuestionResultClass()
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }

    public AnswerFormat getImpliedAnswerFormat() {
        return this;
    }

}
