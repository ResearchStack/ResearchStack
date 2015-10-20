package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpIneligibleStepFragment extends StepFragment
{

    public SignUpIneligibleStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpIneligibleStepFragment fragment = new SignUpIneligibleStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_ineligible,
                null);

        hideNextButtons();

        return root;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
