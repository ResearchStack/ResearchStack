package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.step.layout.WalkingTaskStepLayout;

/**
 * Created by TheMDP on 2/15/17.
 */

public class WalkingTaskStep extends ActiveStep {

    private int numberOfStepsPerLeg;

    /* Default constructor needed for serilization/deserialization of object */
    WalkingTaskStep() {
        super();
    }

    /* Default constructor needed for serilization/deserialization of object */
    public WalkingTaskStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public WalkingTaskStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    @Override
    public Class getStepLayoutClass() {
        return WalkingTaskStepLayout.class;
    }

    private void commonInit() {
        setShouldShowDefaultTimer(false);
    }

    public int getNumberOfStepsPerLeg() {
        return numberOfStepsPerLeg;
    }

    public void setNumberOfStepsPerLeg(int numberOfStepsPerLeg) {
        this.numberOfStepsPerLeg = numberOfStepsPerLeg;
    }
}
