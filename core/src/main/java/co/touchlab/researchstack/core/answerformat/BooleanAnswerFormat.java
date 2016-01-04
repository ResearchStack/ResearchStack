package co.touchlab.researchstack.core.answerformat;


import co.touchlab.researchstack.core.model.Choice;

public class BooleanAnswerFormat extends ChoiceAnswerFormat
{

    //TODO Fetch strings from Resources
    public BooleanAnswerFormat()
    {
        super(ChoiceAnswerStyle.SingleChoice, new Choice<>("Yes", 1), new Choice<>("No", 0));
    }
}
