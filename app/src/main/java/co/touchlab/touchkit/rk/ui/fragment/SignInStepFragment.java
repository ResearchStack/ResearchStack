package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignInStepFragment extends StepFragment
{

    public SignInStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignInStepFragment fragment = new SignInStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_sign_in,
                null);

        AppCompatEditText email = (AppCompatEditText) root.findViewById(R.id.email);
        AppCompatEditText password = (AppCompatEditText) root.findViewById(R.id.password);
        TextView forgotPassword = (TextView) root.findViewById(R.id.forgot_password);

        return root;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
