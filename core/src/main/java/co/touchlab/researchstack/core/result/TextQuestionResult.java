package co.touchlab.researchstack.core.result;
public class TextQuestionResult extends FormResult<String>
{

    private String textAnswer;

    public TextQuestionResult(String identifier)
    {
        super(identifier);
    }

    public String getTextAnswer()
    {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer)
    {
        this.textAnswer = textAnswer;
    }
}
