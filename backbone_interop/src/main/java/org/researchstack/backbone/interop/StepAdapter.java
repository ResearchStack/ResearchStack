package org.researchstack.backbone.interop;

import org.researchstack.foundation.core.interfaces.IStep;
import org.researchstack.foundation.core.models.step.Step;

public class StepAdapter extends Step {

    public StepAdapter(IStep step) {
        super(step.getIdentifier());
    }

}
