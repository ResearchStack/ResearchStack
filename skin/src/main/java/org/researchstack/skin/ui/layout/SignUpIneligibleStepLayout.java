package org.researchstack.skin.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.skin.R;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.step.layout.TextViewLinkHandler;
import org.researchstack.backbone.ui.views.SubmitBar;

public class SignUpIneligibleStepLayout extends LinearLayout implements StepLayout
{
    private StepCallbacks callbacks;
    private Step          step;

    public SignUpIneligibleStepLayout(Context context)
    {
        this(context, null);
    }

    public SignUpIneligibleStepLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SignUpIneligibleStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        initializeStep();
    }

    private void initializeStep()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_ineligible, this, true);
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }


    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
