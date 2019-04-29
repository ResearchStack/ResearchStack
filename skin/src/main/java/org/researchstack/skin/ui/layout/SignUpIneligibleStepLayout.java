package org.researchstack.skin.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks;
import org.researchstack.foundation.components.common.ui.layout.StepLayout;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;
import org.researchstack.skin.R;

public class SignUpIneligibleStepLayout extends LinearLayout implements StepLayout {
    private StepCallbacks callbacks;
    private Step step;

    public SignUpIneligibleStepLayout(Context context) {
        this(context, null);
    }

    public SignUpIneligibleStepLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignUpIneligibleStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        this.step = step;
        initializeStep();
    }

    private void initializeStep() {
        LayoutInflater.from(getContext()).inflate(R.layout.rss_layout_ineligible, this, true);
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }


    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }
}
