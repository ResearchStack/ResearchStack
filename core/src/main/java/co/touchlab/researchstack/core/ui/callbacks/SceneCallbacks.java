package co.touchlab.researchstack.core.ui.callbacks;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public interface SceneCallbacks
{
    void onStepTitleChanged(String title);
    void onNextStep(Step step, StepResult result);
    void onPreviousStep(Step step, StepResult result);

    @Deprecated void onCancelStep();
}
