package org.researchstack.backbone.ui.step.layout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;

public interface StepLayout {
    /**
     * @param step Step to be related to this StepLayout
     * @param result the StepResult for this step, if one already exists
     */
    void initialize(Step step, StepResult result);

    View getLayout();

    /**
     * Method allowing a step layout to consume a back event.
     *
     * @return a boolean indicating whether the back event is consumed
     */
    boolean isBackEventConsumed();

    void setCallbacks(StepCallbacks callbacks);
}
