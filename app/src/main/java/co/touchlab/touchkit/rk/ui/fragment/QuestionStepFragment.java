package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;

public abstract class QuestionStepFragment extends StepFragment
{
    public static final String KEY_QUESTION_STEP = "KEY_STEP";
    public static final String KEY_STEP_RESULT = "KEY_STEP_RESULT";
    protected QuestionStep step;
    protected StepResult stepResult;

    public QuestionStepFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        step = (QuestionStep) getArguments().getSerializable(KEY_QUESTION_STEP);
        stepResult = (StepResult) getArguments().getSerializable(KEY_STEP_RESULT);

        if (stepResult == null)
        {
            stepResult = createNewStepResult(step.getIdentifier());
        }
    }

    public abstract View getBodyView(LayoutInflater inflater);

    @Override
    public Step getStep()
    {
        return step;
    }

    @Override
    protected StepResult getStepResult()
    {
        return stepResult;
    }

    public abstract StepResult createNewStepResult(String stepIdentifier);
}
