package co.touchlab.touchkit.rk.common.answerformat;

import co.touchlab.touchkit.rk.common.helpers.TextChoice;

public class BooleanAnswerFormat extends TextChoiceAnswerFormat
{

    public BooleanAnswerFormat() {
        super(ChoiceAnswerStyle.SingleChoice, new TextChoice[] {
                new TextChoice("Yes", true, null), new TextChoice("No", false, null)

        });
    }
}
