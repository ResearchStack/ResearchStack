package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.RangeOfMotionStepLayout;

/**
 * Created by David Evans, 2019.
 */

public class RangeOfMotionStep extends ActiveStep {

    public static final int DEFAULT_RANGE_OF_MOTION_STEP_DURATION = 60; // in seconds

    /* Default constructor needed for serilization/deserialization of object */
    public RangeOfMotionStep() {
        super();
    }

    public RangeOfMotionStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public RangeOfMotionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setOptional(false);
        setStepDuration(DEFAULT_RANGE_OF_MOTION_STEP_DURATION);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldUseNextAsSkipButton(false);
        setShouldSpeakRemainingTimeAtHalfway(false);
        setShouldVibrateOnStart(true);
        setShouldPlaySoundOnStart(true);
        setShouldVibrateOnFinish(true);
        setShouldPlaySoundOnFinish(true);
        setShouldContinueOnFinish(true);
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return RangeOfMotionStepLayout.class;
    }

}
