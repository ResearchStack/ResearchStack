package co.touchlab.researchstack.core.ui.callbacks;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public interface SceneCallbacks
{
    void onStepTitleChanged(String title);
    void onNextStep(Step step);
    void onSkipStep(Step step);
    void onCancelStep();

    void setStepResultForHost(Step step, StepResult result);
    StepResult getStepResultFromHost(String stepId);
}
