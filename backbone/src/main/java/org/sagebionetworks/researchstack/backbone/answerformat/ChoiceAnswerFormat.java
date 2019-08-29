package org.sagebionetworks.researchstack.backbone.answerformat;


import org.sagebionetworks.researchstack.backbone.model.Choice;

/**
 * The ChoiceAnswerFormat class represents an answer format that lets participants choose from a
 * fixed set of text choices in a multiple or single choice question.
 */
public class ChoiceAnswerFormat extends AnswerFormat {
    private AnswerFormat.ChoiceAnswerStyle answerStyle;
    public AnswerFormat.ChoiceAnswerStyle getAnswerStyle() {
        return answerStyle;
    }
    public void setAnswerStyle(AnswerFormat.ChoiceAnswerStyle style) {
        answerStyle = style;
    }
    private Choice[] choices;
    public void setChoices(Choice[] choices) {
        this.choices = choices;
    }

    /* Default constructor needed for serilization/deserialization of object */
    public ChoiceAnswerFormat()
    {
        super();
    }

    /**
     * Creates an answer format with the specified answerStyle(single or multichoice) and collection
     * of choices.
     *
     * @param answerStyle either MultipleChoice or SingleChoice
     * @param choices     an array of {@link Choice} objects, all of the same type
     */
    public ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle answerStyle, Choice... choices) {
        this.answerStyle = answerStyle;
        this.choices = choices.clone();
    }

    /**
     * Returns a multiple choice or single choice question type, which will decide which {@link
     * org.sagebionetworks.researchstack.backbone.ui.step.body.StepBody} to use to display this question.
     *
     * @return the question type for this answer format
     */
    @Override
    public QuestionType getQuestionType() {
        return answerStyle == ChoiceAnswerStyle.MultipleChoice
                ? Type.MultipleChoice
                : Type.SingleChoice;
    }

    /**
     * Returns a copy of the choice array
     *
     * @return a copy of the choices for this question
     */
    public Choice[] getChoices() {
        return choices.clone();
    }
}
