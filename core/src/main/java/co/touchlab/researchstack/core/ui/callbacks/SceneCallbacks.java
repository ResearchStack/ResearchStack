package co.touchlab.researchstack.core.ui.callbacks;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public interface SceneCallbacks
{
    int ACTION_PREV = -1;
    int ACTION_NONE = 0;
    int ACTION_NEXT = 1;

    void onStepTitleChanged(String title);

    void onSaveStep(int action, Step step, StepResult result);

    @Deprecated void onCancelStep();
}
