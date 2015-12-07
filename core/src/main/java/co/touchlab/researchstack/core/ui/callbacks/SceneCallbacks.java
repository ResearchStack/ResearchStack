package co.touchlab.researchstack.core.ui.callbacks;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public interface SceneCallbacks
{
    void onStepTitleChanged(String title);
    void onNextPressed(Step step);
    void onSkipStep(Step step);
    void onCancelStep();
    void onStepResultChanged(Step step, StepResult result);
    StepResult getResultStep(String stepId);
}
