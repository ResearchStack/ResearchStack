package org.researchstack.backbone.answerformat;

/**
 * AnswerFormat indicating that the QuestionStep is a form step. This lets the layout know that it
 * needs to use the individual QuestionSteps inside the form step to build the ui and validate
 * answers.
 */
public class FormAnswerFormat extends AnswerFormat {
    /* Default constructor needed for serilization/deserialization of object */
    public FormAnswerFormat() {
        super();
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Form;
    }
}
