package org.researchstack.skin.step;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.UnknownAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.skin.R;
import org.researchstack.skin.model.ConsentQuizModel;
import org.researchstack.skin.ui.layout.ConsentQuizQuestionStepLayout;

import java.util.List;

public class ConsentQuizQuestionStep extends QuestionStep
{
    private ConsentQuizModel.QuizQuestion       question;

    public ConsentQuizQuestionStep(ConsentQuizModel.QuizQuestion question)
    {
        super(question.getIdentifier());
        this.question = question;

        if(question.getType().equals("instruction"))
        {
            setAnswerFormat(new UnknownAnswerFormat());
        }
        else if(question.getType().equals("boolean"))
        {
            setAnswerFormat(new BooleanAnswerFormat());
        }
        else if(question.getType().equals("singleChoiceText"))
        {
            List<Choice<Integer>> choices = Choice.from(question.getTextChoices());
            setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                    (Choice[]) choices.toArray()));
        }

    }

    @Override
    public Class getStepLayoutClass()
    {
        return ConsentQuizQuestionStepLayout.class;
    }

    @Override
    public int getStepTitle()
    {
        return R.string.rsb_quiz;
    }

    @Override
    public String getTitle()
    {
        return question.getPrompt();
    }

    public ConsentQuizModel.QuizQuestion getQuestion()
    {
        return question;
    }
}
