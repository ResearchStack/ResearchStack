package co.touchlab.researchstack.backbone.ui.step.layout;
import android.view.View;

import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.ui.callbacks.StepCallbacks;

public interface StepLayout
{
    void initialize(Step step, StepResult result);

    View getLayout();

    /**
     * Method allowing a scene to consume a back event.
     *
     * @return
     */
    boolean isBackEventConsumed();

    void setCallbacks(StepCallbacks callbacks);

}
