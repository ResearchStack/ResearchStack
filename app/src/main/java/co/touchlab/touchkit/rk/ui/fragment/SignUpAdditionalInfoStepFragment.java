package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.User;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpAdditionalInfoStepFragment extends StepFragment
{
    private User user;

    public SignUpAdditionalInfoStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpAdditionalInfoStepFragment fragment = new SignUpAdditionalInfoStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_additional_info,
                null);

        user = AppDelegate.getInstance()
                .getCurrentUser();

        AppCompatTextView height = (AppCompatTextView) root.findViewById(R.id.height);
        height.setText(formatHeightString(user.getHeight()));
        RxView.clicks(height)
                .subscribe(view -> showDatePicker());

        AppCompatEditText weight = (AppCompatEditText) root.findViewById(R.id.weight);
        weight.setText(String.valueOf(user.getWeight()));
        RxTextView.textChanges(weight)
                .filter(input -> input.length() > 0)
                .map(charSequence -> Integer.valueOf(charSequence.toString()))
                .subscribe(user::setWeight);

        return root;
    }

    private String formatHeightString(int height)
    {
        int feet = height / 12;
        int inches = height % 12;
        return String.format("%d\' %d\"", feet, inches);
    }

    private void showDatePicker()
    {
        Toast.makeText(getActivity(),
                "TODO: launch image picker",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
