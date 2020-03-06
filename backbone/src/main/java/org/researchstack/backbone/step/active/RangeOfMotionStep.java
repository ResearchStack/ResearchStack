package com.spineapp;

import org.researchstack.backbone.step.active.ActiveStep;

/**
 * Created by David Evans, 2019.
 */

public class RangeOfMotionStep extends ActiveStep {

    public static final int DEFAULT_RANGE_OF_MOTION_STEP_DURATION = 60; // in seconds

    /* Default constructor needed for serilization/deserialization of object */
    //RangeOfMotionStep() { //TODO: this method creates an error when outside of the RS package
    //        super();
    //    }

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
        setShouldVibrateOnStart(true);
        setShouldPlaySoundOnStart(true);
        setShouldContinueOnFinish(true);
        setShouldStartTimerAutomatically(true);
        setShouldVibrateOnFinish(true);
        setShouldPlaySoundOnFinish(true);
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return RangeOfMotionStepLayout.class;
    }
}
