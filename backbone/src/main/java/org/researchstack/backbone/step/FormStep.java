package org.researchstack.backbone.step;
import org.researchstack.backbone.answerformat.FormAnswerFormat;

import java.util.Arrays;
import java.util.List;


public class FormStep extends QuestionStep
{
    private List<QuestionStep> formSteps;

    public FormStep(String identifier, String title, String text)
    {
        super(identifier, title, new FormAnswerFormat());
        setText(text);
    }

    public List<QuestionStep> getFormSteps()
    {
        return formSteps;
    }

    public void setFormSteps(List<QuestionStep> formSteps)
    {
        this.formSteps = formSteps;
    }

    public void setFormSteps(QuestionStep... formSteps)
    {
        setFormSteps(Arrays.asList(formSteps));
    }
}
