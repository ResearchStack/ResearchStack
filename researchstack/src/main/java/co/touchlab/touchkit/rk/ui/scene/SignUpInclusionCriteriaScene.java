package co.touchlab.touchkit.rk.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxCompoundButton;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpInclusionCriteriaScene extends Scene
{

    public SignUpInclusionCriteriaScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        // TODO this should be and abstract fragment that the developer implements
        return inflater.inflate(R.layout.item_placeholder_inclusion_criteria, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        String identifier = getStep().getIdentifier();

        StepResult resultStep = getCallbacks().getResultStep(identifier);

        AppCompatCheckBox checkBox = (AppCompatCheckBox) body.findViewById(R.id.eligible_checkbox);
        if (resultStep != null)
        {
            checkBox.setChecked(((QuestionResult<Boolean>) resultStep.getResultForIdentifier(identifier)).getAnswer());
        }

        RxCompoundButton.checkedChanges(checkBox)
                .subscribe((isChecked) -> {
                    QuestionResult<Boolean> questionResult = new QuestionResult<>(
                            getStep().getIdentifier());
                    questionResult.setAnswer(isChecked);
                    setStepResult(questionResult);
                });
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
