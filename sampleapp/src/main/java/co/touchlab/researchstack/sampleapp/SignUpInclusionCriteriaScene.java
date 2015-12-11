package co.touchlab.researchstack.sampleapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

import co.touchlab.researchstack.glue.ui.scene.AbstractSignUpInclusionCriteriaScene;

/**
 * Created by bradleymcdermott on 11/17/15.
 */
public class SignUpInclusionCriteriaScene extends AbstractSignUpInclusionCriteriaScene
{
    private RadioGroup humanRadioGroup;

    public SignUpInclusionCriteriaScene(Context context)
    {
        super(context);
    }

    public SignUpInclusionCriteriaScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpInclusionCriteriaScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
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
