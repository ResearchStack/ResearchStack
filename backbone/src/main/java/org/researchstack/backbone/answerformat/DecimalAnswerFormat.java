package org.researchstack.backbone.answerformat;

/**
 * Created by bradleymcdermott on 12/28/15.
 */
public class DecimalAnswerFormat extends AnswerFormat
{
    private float maxValue;
    private float minValue;

    public DecimalAnswerFormat(float maxValue, float minValue)
    {
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return Type.Decimal;
    }

    public float getMaxValue()
    {
        return maxValue;
    }

    public float getMinValue()
    {
        return minValue;
    }
}
