package co.touchlab.researchstack.common.result;
public class TextQuestionResult extends QuestionResult<String>
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
