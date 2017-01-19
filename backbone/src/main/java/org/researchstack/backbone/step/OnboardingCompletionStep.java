package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.OnboardingCompletionStepLayout;

/**
 * Created by TheMDP on 12/31/16.
 */

public class OnboardingCompletionStep extends InstructionStep {

    /* Default constructor needed for serilization/deserialization of object */
    OnboardingCompletionStep() {
        super();
    }

    public OnboardingCompletionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

//    public OnboardingCompletionStep(InstructionSurveyItem item) {
//        super(item);
//    }

    @Override
    public Class getStepLayoutClass()
    {
        return OnboardingCompletionStepLayout.class;
    }
}
