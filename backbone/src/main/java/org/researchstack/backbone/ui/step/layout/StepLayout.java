package org.researchstack.backbone.ui.step.layout;

import android.content.Intent;
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

    void receiveIntentExtraOnResult(int requestCode, Intent intent);

}
