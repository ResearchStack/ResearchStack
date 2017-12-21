package org.researchstack.backbone.answerformat;


/**
 * The TextAnswerFormat class represents the answer format for questions that collect a text
 * response from the user.
 */
public class TextAnswerFormat extends AnswerFormat {
    public static final int UNLIMITED_LENGTH = 0;
    private int maximumLength;

    private boolean isMultipleLines;
    private boolean isTwoFields;

    /**
     * Creates a TextAnswerFormat with no maximum length
     */
    public TextAnswerFormat() {
        this(UNLIMITED_LENGTH);
    }

    /**
     * Creates a TextAnswerFormat with a specified maximum length
     *
     * @param maximumLength the maximum text length allowed
     */
    public TextAnswerFormat(int maximumLength) {
        this.maximumLength = maximumLength;
    }

    /**
     * Returns the maximum length for the answer, <code>UNLIMITED_LENGTH</code> (0) if no maximum
     *
     * @return the maximum length, <code>UNLIMITED_LENGTH</code> (0) if no maximum
     */
    public int getMaximumLength() {
        return maximumLength;
    }

    @Override
    public QuestionType getQuestionType() {
        if (isTwoFields) {
            return Type.ConsentName;
        } else {
            return Type.Text;
        }
    }

    /**
     * Sets whether the EditText should allow multiple lines.
     *
     * @param isMultipleLines boolean indicating if multiple lines are allowed
     */
    public void setIsMultipleLines(boolean isMultipleLines) {
        this.isMultipleLines = isMultipleLines;
    }

    /**
     * Returns whether multiple lines are allowed.
     *
     * @return boolean indicating if multiple lines are allowed
     */
    public boolean isMultipleLines() {
        return isMultipleLines;
    }

    /**
     * Sets whether there should be two EditTexts in the answer (combined as a result).
     *
     * @param isTwoFields boolean indicating if the format should use two EditTexts
     */
    public void setIsTwoTextFields(boolean isTwoFields) {
        this.isTwoFields = isTwoFields;
    }

    /**
     * Returns whether the format should use two EditTexts.
     *
     * @return boolean indicating if the format should use two EditTexts
     */
    public boolean isTwoTextFields() {
        return isTwoFields;
    }

    /**
     * Returns a boolean indicating whether the passed in text is valid based on this answer format
     *
     * @param text the user's text answer to be validated
     * @return a boolean indicating if the answer is valid
     */
    public boolean isAnswerValid(String text) {
        return text != null && text.length() > 0 &&
                (maximumLength == UNLIMITED_LENGTH || text.length() <= maximumLength);
    }
}
