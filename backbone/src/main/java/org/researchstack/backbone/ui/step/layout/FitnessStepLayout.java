package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.FitnessStep;

/**
 * Created by TheMDP on 2/16/17.
 *
 * This exists in iOS and the class is used to monitor certain special Recorders,
 * in iOS these special recorders are HeartRate and Pedometer and they feed into HealthKit,
 * But since there is not HealthKit equivalent on Android, I'm not sure this class should
 * really exist
 *
 * TODO: potentially remove this class
 */

public class FitnessStepLayout extends ActiveStepLayout {

    private FitnessStep fitnessStep;

    public FitnessStepLayout(Context context) {
        super(context);
    }

    public FitnessStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitnessStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FitnessStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void validateStep(Step step) {
        if (!(step instanceof FitnessStep)) {
            throw new IllegalStateException("FitnessStepLayout must have an FitnessStep");
        }
        fitnessStep = (FitnessStep) step;
        super.validateStep(step);
    }
}
