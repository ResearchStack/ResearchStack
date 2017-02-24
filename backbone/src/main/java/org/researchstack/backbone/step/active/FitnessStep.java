package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.FitnessStepLayout;

/**
 * Created by TheMDP on 2/16/17.
 */

public class FitnessStep extends ActiveStep {

    /* Default constructor needed for serilization/deserialization of object */
    FitnessStep() {
        super();
    }

    public FitnessStep(String identifier) {
        super(identifier);
        setShouldShowDefaultTimer(false);
    }

    public FitnessStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        setShouldShowDefaultTimer(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return FitnessStepLayout.class;
    }
}
