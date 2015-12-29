package co.touchlab.researchstack.core.answerformat;

/**
 * Created by bradleymcdermott on 10/13/15.
 */
public class FormAnswerFormat extends AnswerFormat
{
    public FormAnswerFormat()
    {
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.Form;
    }
}
