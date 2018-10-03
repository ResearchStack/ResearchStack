package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.EmailVerificationStepLayout;

import java.util.Arrays;

/**
 * Created by TheMDP on 1/21/17.
 *
 * The EmailVerificationStep contains the substep of email verification and also
 * the substep of registration, so that the user can change their email from this step
 */

public class EmailVerificationStep extends SubstepListStep {
    /* Default constructor needed for serilization/deserialization of object */
    EmailVerificationStep() {
        super();
    }

    public EmailVerificationStep(
            String identifier,
            EmailVerificationSubStep verifySubstep,
            RegistrationStep registrationStep)
    {
        super(identifier, Arrays.asList(verifySubstep, registrationStep));
    }

    @Override
    public Class getStepLayoutClass() {
        return EmailVerificationStepLayout.class;
    }
}
