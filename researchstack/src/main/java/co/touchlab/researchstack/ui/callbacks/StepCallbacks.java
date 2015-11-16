package co.touchlab.researchstack.ui.callbacks;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;

public interface StepCallbacks
{
    void onNextPressed(Step step);
    void onStepResultChanged(Step step, StepResult result);
    void onSkipStep(Step step);
//        void onError(String title, String message);

    StepResult getResultStep(String stepId);

    void onCancelStep();
}
