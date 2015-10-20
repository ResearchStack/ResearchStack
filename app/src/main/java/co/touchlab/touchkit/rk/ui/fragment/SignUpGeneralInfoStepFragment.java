package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpGeneralInfoStepFragment extends StepFragment
{

    public SignUpGeneralInfoStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpGeneralInfoStepFragment fragment = new SignUpGeneralInfoStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_general_info,
                null);

        AppCompatEditText name = (AppCompatEditText) root.findViewById(R.id.name);
        AppCompatEditText email = (AppCompatEditText) root.findViewById(R.id.email);
        ImageView profileImage = (ImageView) root.findViewById(R.id.profile_image);

        return root;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
