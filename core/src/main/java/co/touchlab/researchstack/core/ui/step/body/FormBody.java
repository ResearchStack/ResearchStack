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

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep formStep)
    {
        this.formStep = (FormStep) formStep;

        body = (LinearLayout) inflater.inflate(R.layout.scene_form_body, parent, false);

        List<QuestionStep> steps = this.formStep.getFormSteps();
        formStepBodies = new ArrayList<>(steps.size());
        for(QuestionStep step : steps)
        {
            StepBody stepBody = createStepBody(step);
            View bodyView = stepBody.initViewCompact(inflater, body, step);

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
        StepResult<StepResult> stepResult = new StepResult<>(formStep.getIdentifier());

        for(StepBody formStepBody : formStepBodies)
        {

            StepResult result = formStepBody.getStepResult();
            if(result != null)
            {
                stepResult.setResultForIdentifier(result.getIdentifier(), result);
            }
        }

        return stepResult;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        for(StepBody formStepBody : formStepBodies)
        {
            // TODO Implement prefill, formStepBody.getStepResult() will always return null after
            // orientation change
            // StepResult formStepResult = (StepResult) result.getResultForIdentifier(formStepBody.getStepResult().getIdentifier());
            // if(formStepResult != null)
            // {
            //     formStepBody.prefillResult(formStepResult);
            // }
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
