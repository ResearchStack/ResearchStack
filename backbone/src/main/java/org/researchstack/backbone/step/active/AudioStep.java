package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.AudioStepLayout;

/**
 * Created by TheMDP on 2/27/17.
 */

public class AudioStep extends ActiveStep {

    /* Default constructor needed for serialization/deserialization of object */
    AudioStep() {
        super();
    }

    public AudioStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public AudioStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    protected void commonInit() {
        setShouldShowDefaultTimer(false);
        setShouldStartTimerAutomatically(true);
    }

    @Override
    public Class getStepLayoutClass() {
        return AudioStepLayout.class;
    }
}
