package co.touchlab.touchkit.rk.common.result;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
public class BooleanQuestionResult extends QuestionResult
{
    private Boolean answer = null;

    public BooleanQuestionResult(String identifier)
    {
        super(identifier);
    }

    public void setAnswer(boolean answer)
    {
        this.answer = answer;
    }

    public boolean getAnswer()
    {
        return answer;
    }
}
