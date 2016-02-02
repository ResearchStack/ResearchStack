package co.touchlab.researchstack.core.ui.callbacks;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public interface StepCallbacks
{
    int ACTION_PREV = - 1;
    int ACTION_NONE = 0;
    int ACTION_NEXT = 1;
    int ACTION_END  = 2;

    void onSaveStep(int action, Step step, StepResult result);

    @Deprecated
    void onCancelStep();
}
