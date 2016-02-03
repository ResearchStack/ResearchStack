package co.touchlab.researchstack.skin.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.researchstack.backbone.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.glue.R;

public class SignUpIneligibleStepLayout extends StepLayoutImpl
{

    public SignUpIneligibleStepLayout(Context context)
    {
        super(context);
    }

    public SignUpIneligibleStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpIneligibleStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_ineligible, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        hideNextButtons();
    }

}
