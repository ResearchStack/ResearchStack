package co.touchlab.researchstack.core.ui.step.layout;
import android.view.View;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

public interface StepLayout
{
    void initialize(Step step, StepResult result);

    View getLayout();

    /**
     * Method allowing a scene to consume a back event.
     * @return
     */
    boolean isBackEventConsumed();

    void setCallbacks(SceneCallbacks callbacks);

}
