package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.EmailVerificationStepLayout;

/**
 * Created by TheMDP on 1/4/17.
 */

public class EmailVerificationSubStep extends InstructionStep {
    /* Default constructor needed for serilization/deserialization of object */
    EmailVerificationSubStep() {
        super();
    }

    public EmailVerificationSubStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return EmailVerificationStepLayout.SubStepLayout.class;
    }
}
