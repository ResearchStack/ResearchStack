package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

@Deprecated
public class NotImplementedStepFragment extends StepFragment
{

    public NotImplementedStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        NotImplementedStepFragment fragment = new NotImplementedStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {

        TextView editText = new TextView(getActivity());
        editText.setText(((Step) getArguments().getSerializable(KEY_QUESTION_STEP)).getIdentifier());
        return editText;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<String>>(stepIdentifier);
    }
}
