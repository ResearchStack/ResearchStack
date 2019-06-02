package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.RangeOfMotionStepLayout;

/**
 * Created by David Evans, 2019.
 */

public class RangeOfMotionStep extends ActiveStep {


    /* Default constructor needed for serilization/deserialization of object */
    RangeOfMotionStep() {
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
        setShouldShowDefaultTimer(false);
        // Should the below settings be here or in RangeOfMotionTaskFactory?
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
