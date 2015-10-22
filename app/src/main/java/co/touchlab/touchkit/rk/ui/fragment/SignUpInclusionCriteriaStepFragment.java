package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;

import com.jakewharton.rxbinding.widget.RxCompoundButton;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public class SignUpInclusionCriteriaStepFragment extends StepFragment
{

    public SignUpInclusionCriteriaStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpInclusionCriteriaStepFragment fragment = new SignUpInclusionCriteriaStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        // TODO this should be and abstract fragment that the developer implements
        View root = inflater.inflate(R.layout.item_placeholder_inclusion_criteria,
                null);

        String identifier = getStep().getIdentifier();
        StepResult resultStep = ((ViewTaskActivity) getActivity()).getResultStep(identifier);

        AppCompatCheckBox checkBox = (AppCompatCheckBox) root.findViewById(R.id.eligible_checkbox);
        if (resultStep != null)
        {
            checkBox.setChecked(((QuestionResult<Boolean>) resultStep.getResultForIdentifier(identifier)).getAnswer());
        }

        RxCompoundButton.checkedChanges(checkBox)
                .subscribe((isChecked) -> {
                    QuestionResult<Boolean> questionResult = new QuestionResult<>(step.getIdentifier());
                    questionResult.setAnswer(isChecked);
                    setStepResult(questionResult);
                });

        return root;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
