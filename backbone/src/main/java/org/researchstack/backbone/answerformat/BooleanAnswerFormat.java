package org.researchstack.backbone.answerformat;


import org.researchstack.backbone.model.Choice;

public class BooleanAnswerFormat extends ChoiceAnswerFormat
{

    //TODO Fetch strings from Resources
    public BooleanAnswerFormat()
    {
        super(ChoiceAnswerStyle.SingleChoice, new Choice<>("Yes", 1), new Choice<>("No", 0));
    }
}
