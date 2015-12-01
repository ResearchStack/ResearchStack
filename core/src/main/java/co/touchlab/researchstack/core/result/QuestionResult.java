package co.touchlab.researchstack.core.result;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
public class QuestionResult<T> extends Result
{
    private T answer = null;

    public QuestionResult(String identifier)
    {
        super(identifier);
    }

    public void setAnswer(T answer)
    {
        this.answer = answer;
    }

    public T getAnswer()
    {
        return answer;
    }
}
