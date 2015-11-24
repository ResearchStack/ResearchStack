package co.touchlab.researchstack.ui.callbacks;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;

public interface StepCallbacks
{
    void onChangeStepTitle(String title);
    void onNextPressed(Step step);
    void onSkipStep(Step step);
    void onCancelStep();
    void onStepResultChanged(Step step, StepResult result);
    StepResult getResultStep(String stepId);
}
