package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by TheMDP on 1/18/17.
 */

public class OnboardingCompleStepLayout extends InstructionStepLayout {

    public OnboardingCompleStepLayout(Context context) {
        super(context);
    }

    public OnboardingCompleStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnboardingCompleStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public OnboardingCompleStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onComplete() {

    }
}
