package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.ConsentReviewSubstepListStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/16/17.
 */

public class ConsentReviewSubstepListStep extends SubstepListStep {

    /* Default constructor needed for serilization/deserialization of object */
    ConsentReviewSubstepListStep() {
        super();
        init();
    }

    public ConsentReviewSubstepListStep(String identifier, List<Step> stepList) {
        super(identifier, stepList);
        init();
    }

    protected void init() {
        stepLayoutClass = ConsentReviewSubstepListStepLayout.class;
    }
}
