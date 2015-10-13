package co.touchlab.touchkit.rk.common.answerformat;

/**
 * Created by bradleymcdermott on 10/13/15.
 */
public class TextAnswerFormat extends AnswerFormat
{
    private int maximumLength;
    private boolean multipleLines = true;

    public TextAnswerFormat()
    {
        this(0);
    }

    public TextAnswerFormat(int maximumLength)
    {
        this.maximumLength = maximumLength;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.Text;
    }
}
