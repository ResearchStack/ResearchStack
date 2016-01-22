package co.touchlab.researchstack.core.answerformat;

import java.io.Serializable;

import co.touchlab.researchstack.core.dev.DevUtils;
import co.touchlab.researchstack.core.ui.step.body.DateQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.FormBody;
import co.touchlab.researchstack.core.ui.step.body.IntegerQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.MultiChoiceQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.NotImplementedStepBody;
import co.touchlab.researchstack.core.ui.step.body.SingleChoiceQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.TextQuestionBody;


public abstract class AnswerFormat implements Serializable
{
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

    // TODO do we need this at all? enum types + concrete implementations
    public enum QuestionType
    {
        None,
        Form,
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
        TimeInterval;

        //TODO add constructor and pass in class as param
        public Class<?> getSceneClass()
        {
            switch(this)
            {
                case SingleChoice:
                    //TODO type <Integer>
                    return SingleChoiceQuestionBody.class;
                case MultipleChoice:
                    //TODO type <Integer>
                    return MultiChoiceQuestionBody.class;
                case Text:
                    return TextQuestionBody.class;
                case Integer:
                    return IntegerQuestionBody.class;
                case Date:
                    return DateQuestionBody.class;
                case Form:
                    return FormBody.class;
                case Scale:
                case Decimal:
                case Boolean:
                case TimeOfDay:
                case DateAndTime:
                case TimeInterval:
                default:
                    return NotImplementedStepBody.class;
            }
        }

    }

    // TODO why here (or at all)?
    public enum ChoiceAnswerStyle
    {
        SingleChoice,
        MultipleChoice
    }

    // TODO why here?
    public enum NumberFormattingStyle
    {
        Default,
        Percent
    }

    // TODO why here?
    public enum DateAnswerStyle
    {
        DateAndTime,
        Date
    }
}
