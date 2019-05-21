package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.TouchAnywhereStepLayout;

/**
 * Created by David Evans, Laurence Hurst, 2019.
 *
 * The `TouchAnywhereStep` class represents a step that enables the user to
 * begin the task by touching anywhere on the screen.
 *
 */

public class TouchAnywhereStep extends ActiveStep {

    /* Default constructor needed for serilization/deserialization of object */
    TouchAnywhereStep() {
        super();
    }

    public TouchAnywhereStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setOptional(false);
        setShouldVibrateOnStart(true);
        setShouldPlaySoundOnStart(true);
        setShouldVibrateOnFinish(true);
        setShouldPlaySoundOnFinish(true);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldContinueOnFinish(true);
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return TouchAnywhereStepLayout.class;
    }
}

