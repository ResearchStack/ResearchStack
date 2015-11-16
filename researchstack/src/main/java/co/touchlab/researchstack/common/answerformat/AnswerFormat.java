package co.touchlab.researchstack.common.answerformat;

import java.io.Serializable;

import co.touchlab.researchstack.common.helpers.TextChoice;
import co.touchlab.researchstack.common.model.TaskModel;
import co.touchlab.researchstack.dev.DevUtils;
import co.touchlab.researchstack.ui.scene.DateQuestionScene;
import co.touchlab.researchstack.ui.scene.IntegerQuestionScene;
import co.touchlab.researchstack.ui.scene.MultiChoiceQuestionScene;
import co.touchlab.researchstack.ui.scene.NotImplementedScene;
import co.touchlab.researchstack.ui.scene.SingleChoiceQuestionScene;
import co.touchlab.researchstack.ui.scene.TextQuestionScene;

public abstract class AnswerFormat implements Serializable
{

    public static AnswerFormat getChoiceAnswerFormatWithStyle(ChoiceAnswerStyle choiceAnswerStyle, TextChoice[] textChoices)
    {
        return null;
    }

    public static AnswerFormat from(TaskModel.ConstraintsModel constraints)
    {
        // TODO seems like we're doing weird things just to fit the ResearchKit architecture
        AnswerFormat answerFormat;
        String type = constraints.type;
        if (type.equals("BooleanConstraints"))
        {
            answerFormat = new BooleanAnswerFormat();
        }
        else if (type.equals("MultiValueConstraints"))
        {
            ChoiceAnswerStyle answerStyle = constraints.allowMultiple ? ChoiceAnswerStyle.MultipleChoice : ChoiceAnswerStyle.SingleChoice;
            answerFormat = new TextChoiceAnswerFormat(answerStyle, TextChoice.from(constraints.enumeration));
        }
        else if (type.equals("IntegerConstraints"))
        {
            answerFormat = new IntegerAnswerFormat(constraints.maxValue, constraints.minValue);
        }
        else if (type.equals("TextConstraints"))
        {
            answerFormat = new TextAnswerFormat();
        }
        else if (type.equals("DateConstraints"))
        {
            answerFormat = new DateAnswerFormat(DateAnswerStyle.Date);
        }
        else
        {
            answerFormat = new UnknownAnswerFormat();
        }
        return answerFormat;
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
        TimeInterval;

        public Class<?> getSceneClass()
        {
            switch(this)
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

    public AnswerFormat getImpliedAnswerFormat() {
        return this;
    }

}
