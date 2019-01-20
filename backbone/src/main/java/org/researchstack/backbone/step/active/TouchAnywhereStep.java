package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.TouchAnywhereStepLayout;
//import org.researchstack.backbone.ui.step.layout.CountdownStepLayout;

/**
 * Created by David Evans, 2019.
 *
 * The `TouchAnywhereStep` class represents a step that displays a label and a
 * countdown for a time equal to its duration.
 *
 * To use the countdown step, set the `duration` property, incorporate it into a
 * task, and present the task with ViewTaskActivity.
 *
 * The countdown step is used in most of ResearchStacks's predefined active tasks.
 */

public class TouchAnywhereStep extends ActiveStep {

    public static final int DEFAULT_STEP_DURATION = 5;

    /* Default constructor needed for serilization/deserialization of object */
    CountdownStep() {
        super();
    }

    public TouchAnywhereStep(String identifier) {
        super(identifier);
        setStepDuration(DEFAULT_STEP_DURATION);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldContinueOnFinish(true);
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return CountdownStepLayout.class;
    }
}
