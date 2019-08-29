package org.sagebionetworks.researchstack.backbone.answerformat;


import org.sagebionetworks.researchstack.backbone.model.Choice;

/**
 * A convenience subclass of {@link ChoiceAnswerFormat} that provides a simple true/false single
 * choice question.
 * <p>
 * You may pass in the strings to display for true and false for the user, but the values will
 * always be true/false.
 */
public class BooleanAnswerFormat extends ChoiceAnswerFormat {

    public void setTextValues(String trueString, String falseString) {
        setAnswerStyle(ChoiceAnswerStyle.SingleChoice);
        setChoices(new Choice[] {
                new Choice<>(trueString, true),
                new Choice<>(falseString, false)
        });
    }

    /* Default constructor needed for serilization/deserialization of object */
    public BooleanAnswerFormat()
    {
        super();
    }

    /**
     * Constructs a single choice question with true/false values, using the specified strings to
     * represent those choices to the user.
     *
     * @param trueString  a string representing <code>true</code> ("Yes", "True", "OK", etc)
     * @param falseString a string representing <code>false</code> ("No", "False", etc)
     */
    public BooleanAnswerFormat(String trueString, String falseString) {
        this();
        setTextValues(trueString, falseString);
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Boolean;
    }
}
