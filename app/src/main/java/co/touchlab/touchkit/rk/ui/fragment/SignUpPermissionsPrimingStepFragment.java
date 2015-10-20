package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpPermissionsPrimingStepFragment extends StepFragment
{

    public SignUpPermissionsPrimingStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpPermissionsPrimingStepFragment fragment = new SignUpPermissionsPrimingStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_permission_priming,
                null);

        TextView body = (TextView) root.findViewById(R.id.priming_body);
//        body.setText();

        return root;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
