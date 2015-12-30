package co.touchlab.researchstack.core.step;
import java.util.List;

import co.touchlab.researchstack.core.answerformat.FormAnswerFormat;
import co.touchlab.researchstack.core.ui.step.body.FormBody;


public class FormStep extends QuestionStep
{
    private List<QuestionStep> formSteps;

    public FormStep(String identifier, String title, String text)
    {
        super(identifier,
                title,
                new FormAnswerFormat());
        setText(text);
    }

    public void setFormSteps(List<QuestionStep> formSteps)
    {
        this.formSteps = formSteps;
    }

    public List<QuestionStep> getFormSteps()
    {
        return formSteps;
    }

    @Override
    public Class getSceneClass()
    {
        return FormBody.class;
    }
}
