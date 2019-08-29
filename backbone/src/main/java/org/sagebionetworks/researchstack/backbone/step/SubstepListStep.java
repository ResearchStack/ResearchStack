package org.sagebionetworks.researchstack.backbone.step;

import org.sagebionetworks.researchstack.backbone.ui.step.layout.ViewPagerSubstepListStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/16/17.
 */

public class SubstepListStep extends Step {
    List<Step> stepList;

    /* Default constructor needed for serilization/deserialization of object */
    protected SubstepListStep() {
        super();
    }

    public SubstepListStep(String identifier, List<Step> stepList) {
        super(identifier);
        this.stepList = stepList;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    @Override
    public Class getStepLayoutClass() {
        return ViewPagerSubstepListStepLayout.class;
    }
}
