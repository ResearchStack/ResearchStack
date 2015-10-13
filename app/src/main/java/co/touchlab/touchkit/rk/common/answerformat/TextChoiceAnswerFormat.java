package co.touchlab.touchkit.rk.common.answerformat;

import co.touchlab.touchkit.rk.common.helpers.TextChoice;

public class TextChoiceAnswerFormat extends AnswerFormat
{

    private AnswerFormat.ChoiceAnswerStyle answerStyle;
    private TextChoice[]                   textChoices;

    public TextChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle answerStyle, TextChoice[] textChoices)
    {
        this.answerStyle = answerStyle;
        this.textChoices = textChoices;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return QuestionType.SingleChoice;
    }

    public TextChoice[] getTextChoices() {
        return textChoices;
    }
}
