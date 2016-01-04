package co.touchlab.researchstack.sampleapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import co.touchlab.researchstack.glue.ui.scene.AbstractSignUpInclusionCriteriaStepLayout;

/**
 * Created by bradleymcdermott on 11/17/15.
 */
public class SignUpInclusionCriteriaStepLayout extends AbstractSignUpInclusionCriteriaStepLayout
{
    private RadioGroup humanRadioGroup;

    public SignUpInclusionCriteriaStepLayout(Context context)
    {
        super(context);
    }

    public SignUpInclusionCriteriaStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpInclusionCriteriaStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBodyCreated(View body)
    {
        humanRadioGroup = (RadioGroup) body.findViewById(R.id.human_radio_group);
    }

    @Override
    public boolean isAnswerValid()
    {
        // make sure the user has answered all the eligibility questions
        return humanRadioGroup.getCheckedRadioButtonId() != - 1;
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
}
