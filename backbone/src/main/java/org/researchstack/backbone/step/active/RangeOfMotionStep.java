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
        setShouldShowDefaultTimer(false);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return RangeOfMotionStepLayout.class;
    }

}
