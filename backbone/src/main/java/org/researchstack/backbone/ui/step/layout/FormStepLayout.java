package org.researchstack.backbone.ui.step.layout;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;

public interface FormStepLayout extends StepLayout {
    void revertAllChildren(StepResult result);
    Boolean isFormStep();
}