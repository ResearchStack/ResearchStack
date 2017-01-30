package org.researchstack.backbone.answerformat;


import android.text.InputType;

/**
 * The TextAnswerFormat class represents the answer format for questions that collect a text
 * response from the user.
 */
public class TextAnswerFormat extends AnswerFormat {
    public static final int UNLIMITED_LENGTH = 0;
    private int maximumLength;
    private int minimumLength = 0;

    private boolean isMultipleLines = false;
    private int     inputType       = InputType.TYPE_CLASS_TEXT;
    private String  validationRegex = null;

    /**
     * Creates a TextAnswerFormat with no maximum length
     * Also, default constructor needed for serilization/deserialization of object
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

    /**
     * @param maximumLength the maximum length for the answer, 0 if no maximum set
     */
    public void setMaximumLength(int maximumLength)
    {
        this.maximumLength = maximumLength;
    }

    /**
     * Returns the minimum length for the answer, 0 if no minumum set
     *
     * @return the minumum
     */
    public int getMinumumLength()
    {
        return minimumLength;
    }

    /**
     * @param minimumLength minimum length for the answer, 0 if no minumum set
     */
    public void setMinumumLength(int minimumLength)
    {
        this.minimumLength = minimumLength;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Text;
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
     * @param validationRegex used to validate the text answer
     */
    public void setValidationRegex(String validationRegex)
    {
        this.validationRegex = validationRegex;
    }

    /**
     * Returns whether validation regex for text answer
     *
     * @return String which can be null
     */
    public String validationRegex()
    {
        return validationRegex;
    }

    /**
     * @return int indicating the input type of the text answer format
     */
    public int getInputType()
    {
        return inputType;
    }

    /**
     * @param inputType indicating the input type used for this format
     */
    public void setInputType(int inputType)
    {
        this.inputType = inputType;
    }

    /**
     * Returns a boolean indicating whether the passed in text is valid based on this answer format
     *
     * @param text the user's text answer to be validated
     * @return a boolean indicating if the answer is valid
     */
    public boolean isAnswerValid(String text) {
        boolean valid = text != null && text.length() >= minimumLength &&
                (maximumLength == UNLIMITED_LENGTH || text.length() <= maximumLength);

        if (valid == false) {
            return valid;
        }

        if (validationRegex != null) {
            valid = text.matches(validationRegex);
        }

        return valid;
    }
}
