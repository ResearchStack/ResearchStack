package org.researchstack.foundation.components.common.ui.layout;

import android.view.View;

import androidx.annotation.CheckResult;

import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

public interface StepLayout<ResultType> {
    void initialize(Step step, StepResult<ResultType> result);

    View getLayout();

    /**
     * Method allowing a step layout to consume a back event.
     *
     * @return
     */
    @CheckResult
    boolean isBackEventConsumed();

    void setCallbacks(StepCallbacks callbacks);
}
