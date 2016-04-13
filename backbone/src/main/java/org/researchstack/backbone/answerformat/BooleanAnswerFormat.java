package org.researchstack.backbone.answerformat;


import org.researchstack.backbone.model.Choice;

public class BooleanAnswerFormat extends ChoiceAnswerFormat
{

    public BooleanAnswerFormat(String trueString, String falseString)
    {
        super(ChoiceAnswerStyle.SingleChoice,
                new Choice<>(trueString, true),
                new Choice<>(falseString, false));
    }

    @Override
    public QuestionType getQuestionType()
    {
        return Type.Boolean;
    }
}
