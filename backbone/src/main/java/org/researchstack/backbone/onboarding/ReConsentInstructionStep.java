package org.researchstack.backbone.onboarding;

import org.researchstack.backbone.step.InstructionStep;

/**
 * Created by TheMDP on 3/25/17.
 */

public class ReConsentInstructionStep extends InstructionStep {
    /* Default constructor needed for serialization/deserialization of object */
    public ReConsentInstructionStep() {
        super();
    }

    public ReConsentInstructionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }
}
