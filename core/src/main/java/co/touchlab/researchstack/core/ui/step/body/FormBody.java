package co.touchlab.researchstack.core.ui.step.body;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;

public class FormBody implements StepBody
{
    private LinearLayout   body;
    private FormStep       formStep;
    private List<StepBody> formStepBodies;
    private String         identifier;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        this.formStep = (FormStep) step;

        body = (LinearLayout) inflater.inflate(R.layout.scene_form_body, parent, false);

        List<QuestionStep> items = formStep.getFormSteps();
        formStepBodies = new ArrayList<>(items.size());
        for(QuestionStep item : items)
        {
            StepBody stepBody = createStepBody(item);
            View bodyView = stepBody.initViewCompact(inflater, body, item);
            // TODO this is a little weird, but normal question steps use default, forms use ids
            stepBody.setIdentifier(item.getIdentifier());

            formStepBodies.add(stepBody);
            body.addView(bodyView);
        }

        return body;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        throw new RuntimeException("No compact view for this type of step");
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<StepResult> stepResult = new StepResult<>(identifier);

        for(StepBody formStepBody : formStepBodies)
        {
            StepResult result = formStepBody.getStepResult();
            if(result != null)
            {
                stepResult.setResultForIdentifier(formStepBody.getIdentifier(), result);
            }
        }

        return stepResult;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        for(StepBody formStepBody : formStepBodies)
        {
            StepResult formStepResult = (StepResult) result.getResultForIdentifier(
                    formStepBody.getIdentifier());
            if(formStepResult != null)
            {
                formStepBody.prefillResult(formStepResult);
            }
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        for(StepBody formStepBody : formStepBodies)
        {
            // TODO show better invalid feedback
            if(! formStepBody.isAnswerValid())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    @NonNull
    private StepBody createStepBody(Step step)
    {
        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor();
            return (StepBody) constructor.newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
