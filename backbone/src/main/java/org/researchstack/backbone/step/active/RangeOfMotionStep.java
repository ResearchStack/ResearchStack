package org.researchstack.backbone.step.active;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.ui.ActiveTaskActivity;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.step.layout.RangeOfMotionStepLayout;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by David Evans. Copyright (c) 2019.
 */

public class RangeOfMotionStep extends ActiveStep {

    private int limbOption;

    /* Default constructor needed for serilization/deserialization of object */
    RangeOfMotionStep() {
        super();
    }

    /* Default constructor needed for serilization/deserialization of object */
    public RangeOfMotionStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public RangeOfMotionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    @Override
    public Class getStepLayoutClass() {
        return RangeOfMotionStepLayout.class;
    }

    private void commonInit() {
        private boolean shouldVibrateOnStart = true;
        private boolean shouldPlaySoundOnStart = true;
        private boolean shouldVibrateOnFinish = true;
        private boolean shouldPlaySoundOnFinish = true;
        private boolean shouldContinueOnFinish = true;
        private boolean shouldStartTimerAutomatically = true;
    }

    public int getLimbOption() {
        return limbOption;
    }

    public void setLimbOption(int limbOption) {
        this.limbOption = limbOption;
    }
}
