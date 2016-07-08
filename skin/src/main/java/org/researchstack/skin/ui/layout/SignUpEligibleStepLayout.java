package org.researchstack.skin.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.skin.R;

public class SignUpEligibleStepLayout extends RelativeLayout implements StepLayout
{

    public static final int CONSENT_REQUEST = 1001;

    private ActivityCallback permissionCallback;
    private Step             step;
    private StepResult       result;
    private StepCallbacks    callbacks;

    public SignUpEligibleStepLayout(Context context)
    {
        super(context);
    }

    public SignUpEligibleStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpEligibleStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result;

        if(getContext() instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) getContext();
        }

        initializeStep();
    }

    private void initializeStep()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.rss_layout_eligible, this, true);

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveAction((v) -> startConsentActivity());
        submitBar.getNegativeActionView().setVisibility(GONE);
    }

    private void startConsentActivity()
    {
        permissionCallback.startConsentTask();
    }

    private void exitSignUpActivity()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_END, step, null);
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

    @Override
    public void receiveIntentExtraOnResult(int requestCode, Intent intent) {

    }
}
