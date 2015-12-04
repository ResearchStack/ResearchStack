package co.touchlab.researchstack.core.ui.scene;
import android.view.View;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

public interface Scene<T>
{

    View getView();

    /**
     * Method allowing a scene to consume a back event.
     * @return
     */
    boolean isBackEventConsumed();

    void onNextClicked();


    void setStepResult(StepResult<T> result);

    StepResult getStepResult();


    void setCallbacks(SceneCallbacks callbacks);

    SceneCallbacks getCallbacks();

}
