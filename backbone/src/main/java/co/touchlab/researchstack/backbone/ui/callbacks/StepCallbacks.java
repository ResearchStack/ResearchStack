package co.touchlab.researchstack.backbone.ui.callbacks;
import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.step.Step;

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
