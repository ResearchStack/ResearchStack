package org.researchstack.backbone.answerformat;

import org.researchstack.backbone.ui.step.body.DateQuestionBody;
import org.researchstack.backbone.ui.step.body.FormBody;
import org.researchstack.backbone.ui.step.body.IntegerQuestionBody;
import org.researchstack.backbone.ui.step.body.MultiChoiceQuestionBody;
import org.researchstack.backbone.ui.step.body.NotImplementedStepBody;
import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;

import java.io.Serializable;

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
        Class<?> getStepBodyClass();
    }

    // order matters for bridge uploads
    public enum Type implements QuestionType
    {
        None(NotImplementedStepBody.class),
        Scale(NotImplementedStepBody.class),
        SingleChoice(SingleChoiceQuestionBody.class),
        MultipleChoice(MultiChoiceQuestionBody.class),
        Decimal(NotImplementedStepBody.class),
        Integer(IntegerQuestionBody.class),
        Boolean(SingleChoiceQuestionBody.class),
        Eligibility(NotImplementedStepBody.class),
        Text(TextQuestionBody.class),
        TimeOfDay(NotImplementedStepBody.class),
        DateAndTime(NotImplementedStepBody.class),
        Date(DateQuestionBody.class),
        TimeInterval(NotImplementedStepBody.class),
        Location(NotImplementedStepBody.class),
        Form(FormBody.class);

        private Class<?> stepBodyClass;

        Type(Class<?> stepBodyClass)
        {
            this.stepBodyClass = stepBodyClass;
        }

        @Override
        public Class<?> getStepBodyClass()
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
