package org.researchstack.backbone.ui.step.layout;

import android.view.View;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;

public interface StepLayout {
    void initialize(Step step, StepResult result);

    View getLayout();

    /**
     * Method allowing a step layout to consume a back event.
     *
     * @return
     */
    boolean isBackEventConsumed();

    void setCallbacks(StepCallbacks callbacks);

    void setCancelEditMode(boolean isCancelEdit);

    void setRemoveFromBackStack(boolean removeFromBackStack);

    void isEditView(boolean isEditView);

    /**
     * @return StepResult for a step even if it's not yet saved
     */
    StepResult getStepResult();
}