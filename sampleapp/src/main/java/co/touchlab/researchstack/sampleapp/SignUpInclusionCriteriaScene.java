package co.touchlab.researchstack.sampleapp;

import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.glue.ui.scene.AbstractSignUpInclusionCriteriaScene;

/**
 * Created by bradleymcdermott on 11/17/15.
 */
public class SignUpInclusionCriteriaScene extends AbstractSignUpInclusionCriteriaScene
{
    private RadioGroup humanRadioGroup;

    public SignUpInclusionCriteriaScene(Context context, Step step, StepResult result)
    {
        super(context, step, result);
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.item_inclusion_criteria;
    }

    @Override
    public boolean isEligible()
    {
        return humanRadioGroup.getCheckedRadioButtonId() == R.id.human_radio_yes;
    }

    @Override
    public boolean isAnswerValid()
    {
        // make sure the user has answered all the eligibility questions
        return humanRadioGroup.getCheckedRadioButtonId() != -1;
    }

    @Override
    public void onBodyCreated(View body)
    {
        humanRadioGroup = (RadioGroup) body.findViewById(R.id.human_radio_group);
    }
}
