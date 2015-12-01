package co.touchlab.researchstack.core.answerformat;

/**
 * Created by bradleymcdermott on 10/13/15.
 */
@Deprecated
public class UnknownAnswerFormat extends AnswerFormat
{
    public UnknownAnswerFormat()
    {
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.None;
    }
}
