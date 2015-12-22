package co.touchlab.researchstack.core.answerformat;

import java.io.Serializable;

import co.touchlab.researchstack.core.dev.DevUtils;
import co.touchlab.researchstack.core.ui.scene.DateQuestionScene;
import co.touchlab.researchstack.core.ui.scene.IntegerQuestionScene;
import co.touchlab.researchstack.core.ui.scene.MultiChoiceQuestionScene;
import co.touchlab.researchstack.core.ui.scene.NotImplementedScene;
import co.touchlab.researchstack.core.ui.scene.SingleChoiceQuestionScene;
import co.touchlab.researchstack.core.ui.scene.TextQuestionScene;


public abstract class AnswerFormat implements Serializable
{
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
        TimeInterval;

        public Class<?> getSceneClass()
        {
            switch (this)
            {
                case SingleChoice:
                    //TODO type <Integer>
                    return SingleChoiceQuestionScene.class;
                case MultipleChoice:
                    //TODO type <Integer>
                    return MultiChoiceQuestionScene.class;
                case Text:
                    return TextQuestionScene.class;
                case Integer:
                    return IntegerQuestionScene.class;
                case Scale:
                case Decimal:
                case Boolean:
                case TimeOfDay:
                case DateAndTime:
                    return NotImplementedScene.class;
                case Date:
                    return DateQuestionScene.class;
                case TimeInterval:
                default:
                    return NotImplementedScene.class;
            }
        }

    }

    public enum ChoiceAnswerStyle
    {
        SingleChoice,
        MultipleChoice
    }

    public enum NumberFormattingStyle
    {
        Default,
        Percent
    }

    public enum DateAnswerStyle
    {
        DateAndTime,
        Date
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
}
