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
    }

    public ConsentReviewSubstepListStep(String identifier, List<Step> stepList) {
        super(identifier, stepList);
    }

    @Override
    public Class getStepLayoutClass() {
        return ConsentReviewSubstepListStepLayout.class;
    }
}
