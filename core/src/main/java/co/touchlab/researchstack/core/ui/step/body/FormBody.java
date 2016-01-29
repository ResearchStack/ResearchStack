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
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private FormStep               step;
    private StepResult<StepResult> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private List<StepBody> formStepChildren;

    public FormBody(Step step, StepResult result)
    {
        this.step = (FormStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        // Inflate our container for each compact child StepBody
        LinearLayout body = (LinearLayout) inflater.inflate(R.layout.scene_form_body,
                parent,
                false);

        List<QuestionStep> steps = step.getFormSteps();
        formStepChildren = new ArrayList<>(steps.size());

        // Iterate through all steps and generate each compact view. Store each StepBody child in a
        // list to iterate over (e.g. within getStepResult())
        for(QuestionStep step : steps)
        {
            StepBody stepBody = createStepBody(step);
            View bodyView = stepBody.getBodyView(VIEW_TYPE_COMPACT, inflater, body);
            body.addView(bodyView);

            formStepChildren.add(stepBody);
        }

        return body;
    }

    @Override
    public StepResult getStepResult()
    {
        for(StepBody child : formStepChildren)
        {
            StepResult childResult = child.getStepResult();
            if(childResult != null)
            {
                this.result.setResultForIdentifier(childResult.getIdentifier(), childResult);
            }
        }

        return this.result;
    }

    @Override
    public boolean isAnswerValid()
    {
        for(StepBody formStepBody : formStepChildren)
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
        StepResult childResult = result.getResultForIdentifier(step.getIdentifier());

        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(step, childResult);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
