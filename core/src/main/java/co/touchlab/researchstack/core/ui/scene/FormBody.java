package co.touchlab.researchstack.core.ui.scene;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private LinearLayout body;
    private FormStep formStep;
    private StepResult<StepResult> stepResult;
    private List<StepBody> formStepBodies;

    private StepResult<StepResult> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        this.formStep = (FormStep) step;

        if (result == null)
        {
            result = createStepResult(StepResult.DEFAULT_KEY);
        }

        stepResult = result;

        body = (LinearLayout) inflater.inflate(R.layout.scene_form_body,
                parent,
                false);

        List<QuestionStep> items = formStep.getFormSteps();
        formStepBodies = new ArrayList<>(items.size());
        for (int i = 0, size = items.size(); i < size; i++)
        {
            QuestionStep item = items.get(i);
            StepResult formStepResult = stepResult.getResultForIdentifier(item.getIdentifier());

            StepBody stepBody = createStepBody(item);
            View bodyView = stepBody.initializeCompact(inflater,
                    body,
                    item,
                    formStepResult,
                    item.getIdentifier());

            formStepBodies.add(stepBody);
            body.addView(bodyView);
        }

        return body;
    }

    @Override
    public View initializeCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        throw new RuntimeException("No compact view for this type of step");
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
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StepResult getStepResult()
    {
        for (StepBody formStepBody : formStepBodies)
        {
            StepResult result = formStepBody.getStepResult();
            if (result != null)
            {
                this.stepResult.setResultForIdentifier(result.getIdentifier(),
                        result);
            }
        }

        return stepResult;
    }

    @Override
    public boolean isAnswerValid()
    {
        for (StepBody formStepBody : formStepBodies)
        {
            // TODO show better invalid feedback
            if (!formStepBody.isAnswerValid())
            {
                return false;
            }
        }
        return true;
    }
}
