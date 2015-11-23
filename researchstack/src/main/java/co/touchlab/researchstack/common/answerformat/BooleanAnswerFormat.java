package co.touchlab.researchstack.common.answerformat;

import co.touchlab.researchstack.common.helpers.TextChoice;

public class BooleanAnswerFormat extends TextChoiceAnswerFormat
{

    //TODO Fetch strings from Resources
    public BooleanAnswerFormat() {
        super(ChoiceAnswerStyle.SingleChoice, new TextChoice[] {
                // using 1 and 0 to fit with skip rules in smart surveys
                new TextChoice<>("Yes", 1, null), new TextChoice<>("No", 0, null)

        });
    }
}
