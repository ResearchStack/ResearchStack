package co.touchlab.researchstack.core.ui.scene;
import android.view.View;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

public interface Scene
{
    void initialize(Step step, StepResult result);

    View getView();

    /**
     * Method allowing a scene to consume a back event.
     * @return
     */
    boolean isBackEventConsumed();

    void setCallbacks(SceneCallbacks callbacks);

}
