package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.TouchAnywhereStepLayout;

/**
 * Created by David Evans, 2019.
 *
 * The `TouchAnywhereStep` class represents a step that enables the user to begin the task by touching anywhere on the screen.
 *
 */

public class TouchAnywhereStep extends ActiveStep {

    TouchAnywhereStep() {
        super();
    }

    public TouchAnywhereStep(String identifier) {
        super(identifier);

        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldContinueOnFinish(true);

    }
}

