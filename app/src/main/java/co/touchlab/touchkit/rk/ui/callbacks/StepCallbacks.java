package co.touchlab.touchkit.rk.ui.callbacks;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public interface StepCallbacks
{
    void onNextPressed(Step step);
    void onStepResultChanged(Step step, StepResult result);
    void onSkipStep(Step step);
//        void onError(String title, String message);

    StepResult getResultStep(String stepId);

    void onCancelStep();
}
