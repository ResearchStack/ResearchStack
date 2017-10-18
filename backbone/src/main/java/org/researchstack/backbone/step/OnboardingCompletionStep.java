package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.OnboardingCompletionStepLayout;

/**
 * Created by TheMDP on 1/18/17.
 */

public class OnboardingCompletionStep extends InstructionStep {

    /* Default constructor needed for serilization/deserialization of object */
    OnboardingCompletionStep() {
        super();
    }

    public OnboardingCompletionStep(String identifier, String title, String detailText)
    {
        super(identifier, title, detailText);
        setOptional(false);
    }

    public Class getStepLayoutClass() {
        return OnboardingCompletionStepLayout.class;
    }
}
