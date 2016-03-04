package org.researchstack.backbone.answerformat;

/**
 * Created by bradleymcdermott on 10/13/15.
 */
public class IntegerAnswerFormat extends AnswerFormat
{
    private int maxValue;
    private int minValue;

    // TODO Params should be reserved.
    public IntegerAnswerFormat(int minValue, int maxValue)
    {
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return Type.Integer;
    }

    public int getMaxValue()
    {
        return maxValue;
    }

    public int getMinValue()
    {
        return minValue;
    }
}
