package org.sagebionetworks.researchstack.backbone.answerformat;

import org.sagebionetworks.researchstack.backbone.ui.step.body.DateQuestionBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.DecimalQuestionBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.DurationQuestionBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.FormBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.ImageChoiceBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.IntegerQuestionBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.MultiChoiceQuestionBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.NotImplementedStepBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.StepBody;
import org.sagebionetworks.researchstack.backbone.ui.step.body.TextQuestionBody;

import java.io.Serializable;

/**
 * The AnswerFormat class is the abstract base class for classes that describe the format in which a
 * survey question should be answered. The ResearchStack framework uses {@link
 * org.sagebionetworks.researchstack.backbone.step.QuestionStep} to represent questions to ask the user. Each
 * question must have an associated answer format.
 * <p>
 * To use an answer format, instantiate the appropriate answer format subclass and attach it to a
 * question step or form item. Incorporate the resulting step into a task, and present the task with
 * a {@link org.sagebionetworks.researchstack.backbone.ui.ViewTaskActivity}.
 */
public abstract class AnswerFormat implements Serializable {
    /* Default constructor needed for serialization/deserialization of object */
    public AnswerFormat() {
    }

    /**
     * Returns the QuestionType for this answer format. Implement this in your subclass.
     *
     * @return the question type
     */
    public QuestionType getQuestionType() {
        return Type.None;
    }

    /**
     * The type of question. (read-only)
     * <p>
     * The type provides a default {@link org.sagebionetworks.researchstack.backbone.ui.step.body.StepBody} for that
     * type of question. A custom StepLayout implementation may provide it's own StepBody rather
     * than using the default provided by this AnswerFormat.
     */
    public enum Type implements QuestionType {
        None(NotImplementedStepBody.class),
        Scale(NotImplementedStepBody.class),
        SingleChoice(SingleChoiceQuestionBody.class),
        MultipleChoice(MultiChoiceQuestionBody.class),
        Decimal(DecimalQuestionBody.class),
        Integer(IntegerQuestionBody.class),
        Boolean(SingleChoiceQuestionBody.class),
        Eligibility(NotImplementedStepBody.class),
        Text(TextQuestionBody.class),
        TimeOfDay(DateQuestionBody.class),
        DateAndTime(DateQuestionBody.class),
        Date(DateQuestionBody.class),
        TimeInterval(NotImplementedStepBody.class),
        Duration(DurationQuestionBody.class),
        Location(NotImplementedStepBody.class),
        Form(FormBody.class),
        ImageChoice(ImageChoiceBody.class);

        private Class<? extends StepBody> stepBodyClass;

        Type(Class<? extends StepBody> stepBodyClass) {
            this.stepBodyClass = stepBodyClass;
        }

        @Override
        public Class<? extends StepBody> getStepBodyClass() {
            return stepBodyClass;
        }

    }

    /**
     * The style of the question (that is, single or multiple choice).
     */
    public enum ChoiceAnswerStyle {
        SingleChoice,
        MultipleChoice
    }

    /**
     * An enumeration of the format styles available for scale answers.
     */
    public enum NumberFormattingStyle {
        Default,
        Percent
    }

    /**
     * The style of date picker to use in an {@link DateAnswerFormat} object.
     */
    public enum DateAnswerStyle {
        DateAndTime,
        Date,
        TimeOfDay
    }

    /**
     * Interface that {@link Type} implements. Since you cannot add a value to an existing enum, you
     * may implement this interface instead to provide your own QuestionType that provides a {@link
     * org.sagebionetworks.researchstack.backbone.ui.step.body.StepBody} class.
     */
    public interface QuestionType {
        Class<? extends StepBody> getStepBodyClass();
    }
}
