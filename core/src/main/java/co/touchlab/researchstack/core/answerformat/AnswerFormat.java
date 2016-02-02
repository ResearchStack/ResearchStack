package co.touchlab.researchstack.core.answerformat;

import java.io.Serializable;

import co.touchlab.researchstack.core.ui.step.body.DateQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.FormBody;
import co.touchlab.researchstack.core.ui.step.body.IntegerQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.MultiChoiceQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.NotImplementedStepBody;
import co.touchlab.researchstack.core.ui.step.body.SingleChoiceQuestionBody;
import co.touchlab.researchstack.core.ui.step.body.TextQuestionBody;

// TODO are we just using this because ResearchKit did? look into just using QuestionBody class
public abstract class AnswerFormat implements Serializable
{
    public AnswerFormat()
    {
    }

    public QuestionType getQuestionType()
    {
        return Type.None;
    }

    public interface QuestionType
    {
        Class<?> getSceneClass();
    }

    public enum Type implements QuestionType
    {
        None(NotImplementedStepBody.class),
        Form(FormBody.class),
        Scale(NotImplementedStepBody.class),
        SingleChoice(SingleChoiceQuestionBody.class),
        MultipleChoice(MultiChoiceQuestionBody.class),
        Decimal(NotImplementedStepBody.class),
        Integer(IntegerQuestionBody.class),
        Boolean(NotImplementedStepBody.class),
        Text(TextQuestionBody.class),
        TimeOfDay(NotImplementedStepBody.class),
        DateAndTime(NotImplementedStepBody.class),
        Date(DateQuestionBody.class),
        TimeInterval(NotImplementedStepBody.class);

        private Class<?> stepBodyClass;

        Type(Class<?> stepBodyClass)
        {
            this.stepBodyClass = stepBodyClass;
        }

        @Override
        public Class<?> getSceneClass()
        {
            return stepBodyClass;
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
