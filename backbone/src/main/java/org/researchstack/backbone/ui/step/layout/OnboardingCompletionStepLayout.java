package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by TheMDP on 1/18/17.
 */

public class OnboardingCompletionStepLayout extends InstructionStepLayout {
    public OnboardingCompletionStepLayout(Context context) {
        super(context);
    }

    public OnboardingCompletionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnboardingCompletionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public OnboardingCompletionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onComplete() {
        // TODO: make a hook to control where the user goes after onboarding is complete
        super.onComplete();
    }
}
