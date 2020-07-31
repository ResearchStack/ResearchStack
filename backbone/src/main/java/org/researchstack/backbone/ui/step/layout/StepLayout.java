package org.researchstack.backbone.ui.step.layout;

import android.view.View;

import org.jetbrains.annotations.NotNull;
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

    /**
     * Replaces the Step's existing result, with a new one. For FormSteps, each children (that has a response in the new result) will have its result replaced too.
     * @param newResult The new result set
     */
    void setStepResultTo(@NotNull StepResult newResult);
}