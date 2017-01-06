package org.researchstack.backbone.step;

import org.researchstack.backbone.model.survey.InstructionSurveyItem;
import org.researchstack.backbone.ui.step.layout.CompletionStepLayout;

/**
 * Created by TheMDP on 12/31/16.
 */

public class CompletionStep extends InstructionStep {
    public CompletionStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

//    public CompletionStep(InstructionSurveyItem item) {
//        super(item);
//    }

    @Override
    public Class getStepLayoutClass()
    {
        return CompletionStepLayout.class;
    }
}
