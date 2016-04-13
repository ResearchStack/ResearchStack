package org.researchstack.backbone.answerformat;

/**
 * This class defines the attributes for a decimal answer format that participants enter using a
 * numeric keyboard.
 * <p>
 * If you specify maximum or minimum values and the user enters a value outside the specified range,
 * the DecimalQuestionBody does not allow navigation until the participant provides a value that is
 * within the valid range.
 */
public class DecimalAnswerFormat extends AnswerFormat
{
    private float minValue;
    private float maxValue;

    /**
     * Creates an answer format with the specified min and max values
     *
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value, or 0f for unlimited
     */
    public DecimalAnswerFormat(float minValue, float maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return Type.Decimal;
    }

    /**
     * Returns the min value
     *
     * @return returns the min value
     */
    public float getMinValue()
    {
        return minValue;
    }

    /**
     * Returns the max value, or 0f for no maximum
     *
     * @return returns the max value, or 0f for no maximum
     */
    public float getMaxValue()
    {
        return maxValue;
    }
}
