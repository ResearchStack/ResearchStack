package co.touchlab.researchstack.core.result;

/**
 * Created by bradleymcdermott on 10/9/15.
 */
public class FormResult<T> extends Result
{
    private T answer = null;

    public FormResult(String identifier)
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
