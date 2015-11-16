package co.touchlab.researchstack.common.answerformat;

/**
 * Created by bradleymcdermott on 10/13/15.
 */
public class IntegerAnswerFormat extends AnswerFormat
{
    private int maxValue;
    private int minValue;

    public IntegerAnswerFormat(int maxValue, int minValue)
    {
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.Integer;
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
