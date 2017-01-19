package org.researchstack.backbone.ui.callbacks;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewTaskActivity;

/**
 * Interface used for callbacks related to changes in the Step.
 * Used by {@link ViewTaskActivity} to communicate when a new step has been visualised
 */
public interface StepViewCallback {

    /**
     * This function is called when the {@link org.researchstack.backbone.ui.step.layout.StepLayout}
     * of a @{@link Step} has been visualised and all animations have finished.
     * @param activity the instance of the {@link ViewTaskActivity}
     * @param step the @{@link Step} currently being shown
     */
    public void onStepShown(ViewTaskActivity activity, Step step);
}
