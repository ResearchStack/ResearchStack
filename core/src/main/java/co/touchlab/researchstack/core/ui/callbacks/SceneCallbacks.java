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

    /**
     * TODO Remove
     * The result object should be passed in when the scene is initially created. After that, the result
     * object can only controlled within said class. The result object should then pass that up to
     * the host via {@link #setStepResultForHost}
     */
    @Deprecated
    StepResult getStepResultFromHost(String stepId);
}
