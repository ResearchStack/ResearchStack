package org.researchstack.foundation.components.common.ui.layout;

import android.view.View;

import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

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
}
