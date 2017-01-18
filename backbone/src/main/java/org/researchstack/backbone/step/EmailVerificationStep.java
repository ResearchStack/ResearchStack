package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.EmailVerificationStepLayout;
import org.researchstack.backbone.ui.step.layout.PasscodeCreationStepLayout;

/**
 * Created by TheMDP on 1/4/17.
 */

public class EmailVerificationStep extends InstructionStep {
    /* Default constructor needed for serilization/deserialization of object */
    EmailVerificationStep() {
        super();
    }

    public EmailVerificationStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return EmailVerificationStepLayout.class;
    }
}
