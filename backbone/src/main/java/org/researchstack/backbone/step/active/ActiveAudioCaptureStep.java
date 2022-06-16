package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.ActiveAudioCaptureStepLayout;

public class ActiveAudioCaptureStep extends ActiveStep {

    private String instructionsText;

    public ActiveAudioCaptureStep(String identifier, String title, String detailText, int duration) {
        super(identifier, title, detailText);
        if (duration <= 0)
            throw new IllegalArgumentException("Step duration must be greater than 0");
        setStepDuration(duration);
    }

    public Class getStepLayoutClass() {
        return ActiveAudioCaptureStepLayout.class;
    }

    public void setInstructionsText(String instructionsText) {
        this.instructionsText = instructionsText;
    }

    public String getInstructionsText() {
        return this.instructionsText;
    }

}
