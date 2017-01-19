package org.researchstack.backbone.answerformat;


import org.researchstack.backbone.model.Choice;

/**
 * A convenience subclass of {@link ChoiceAnswerFormat} that provides a simple true/false single
 * choice question.
 * <p>
 * You may pass in the strings to display for true and false for the user, but the values will
 * always be true/false.
 */
public class BooleanAnswerFormat extends ChoiceAnswerFormat {

    /**
     * Constructs a single choice question with true/false values, using the specified strings to
     * represent those choices to the user.
     *
     * @param trueString  a string representing <code>true</code> ("Yes", "True", "OK", etc)
     * @param falseString a string representing <code>false</code> ("No", "False", etc)
     */
    public BooleanAnswerFormat(String trueString, String falseString) {
        super(ChoiceAnswerStyle.SingleChoice,
                new Choice<>(trueString, true),
                new Choice<>(falseString, false));
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Boolean;
    }
}
