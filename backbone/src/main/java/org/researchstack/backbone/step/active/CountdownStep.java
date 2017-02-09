package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.CountdownStepLayout;

/**
 * Created by TheMDP on 2/4/17.
 *
 * The `CountdownStep` class represents a step that displays a label and a
 * countdown for a time equal to its duration.
 *
 * To use the countdown step, set the `duration` property, incorporate it into a
 * task, and present the task with ViewTaskActivity.
 *
 * The countdown step is used in most of ResearchStacks's predefined active tasks.
 */

public class CountdownStep extends ActiveStep {

    public static final int DEFAULT_STEP_DURATION = 5;

    /* Default constructor needed for serilization/deserialization of object */
    CountdownStep() {
        super();
    }

    public CountdownStep(String identifier) {
        super(identifier);
        setStepDuration(DEFAULT_STEP_DURATION);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldContinueOnFinish(true);
    }

    @Override
    public Class getStepLayoutClass() {
        return CountdownStepLayout.class;
    }
}
