package org.sagebionetworks.researchstack.backbone.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.step.Step;
import org.sagebionetworks.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.sagebionetworks.researchstack.backbone.ui.step.layout.StepLayout;
import org.sagebionetworks.researchstack.backbone.R;

@Deprecated // use OnboardingManager.getInstance().launchOnboarding(OnboardingTaskType.REGISTRATION, this);
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
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_layout_ineligible, this, true);

        TextView text = (TextView) findViewById(R.id.ineligible_text);
        TextView detailText = (TextView) findViewById(R.id.ineligible_detail);

        text.setText(step.getTitle());
        detailText.setText(step.getText());
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
