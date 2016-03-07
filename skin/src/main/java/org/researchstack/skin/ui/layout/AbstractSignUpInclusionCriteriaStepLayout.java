package org.researchstack.skin.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.ui.step.layout.StepLayoutImpl;

public abstract class AbstractSignUpInclusionCriteriaStepLayout extends StepLayoutImpl<Boolean>
{

    public AbstractSignUpInclusionCriteriaStepLayout(Context context)
    {
        super(context);
    }

    public AbstractSignUpInclusionCriteriaStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractSignUpInclusionCriteriaStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(getLayoutId(), parent, false);
    }

    public abstract void onBodyCreated(View body);

    @Override
    public StepResult<Boolean> getStepResult()
    {
        StepResult<Boolean> result = super.getStepResult();
        result.setResult(isEligible());
        return result;
    }

    public abstract boolean isAnswerValid();

    public abstract int getLayoutId();

    public abstract boolean isEligible();
}
