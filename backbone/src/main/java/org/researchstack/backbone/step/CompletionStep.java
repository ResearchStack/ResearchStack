package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.CompletionStepLayout;

/**
 * Created by TheMDP on 12/31/16.
 */

public class CompletionStep extends InstructionStep {

    /* Default constructor needed for serilization/deserialization of object */
    CompletionStep() {
        super();
    }

    public CompletionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return CompletionStepLayout.class;
    }
}
