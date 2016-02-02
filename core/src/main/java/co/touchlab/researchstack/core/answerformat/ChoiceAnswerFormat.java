package co.touchlab.researchstack.core.answerformat;


import co.touchlab.researchstack.core.model.Choice;

public class ChoiceAnswerFormat extends AnswerFormat
{

    private AnswerFormat.ChoiceAnswerStyle answerStyle;
    private Choice[]                       choices;

    public ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle answerStyle, Choice... choices)
    {
        this.answerStyle = answerStyle;
        this.choices = choices;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return answerStyle == ChoiceAnswerStyle.MultipleChoice
                ? Type.MultipleChoice
                : Type.SingleChoice;
    }

    public Choice[] getChoices()
    {
        return choices;
    }

    public String[] getTextChoiceNames()
    {
        String[] names = new String[choices.length];
        for(int i = 0; i < choices.length; i++)
        {
            names[i] = choices[i].getText();
        }
        return names;
    }
}
