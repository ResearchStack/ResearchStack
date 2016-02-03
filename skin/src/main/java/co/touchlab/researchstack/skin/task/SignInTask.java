package co.touchlab.researchstack.skin.task;

import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.step.Step;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignInTask extends OnboardingTask
{

    public static final int    MINIMUM_STEPS = 0;
    public static final String ID_EMAIL      = "ID_EMAIL";
    public static final String ID_PASSWORD   = "ID_PASSWORD";

    public SignInTask()
    {
        super("SignIn");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;

        if(step == null)
        {
            nextStep = getSignInStep();
        }

        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        Step prevStep = null;

        if(step.getIdentifier().equals(SignInStepIdentifier))
        {
            prevStep = null;
        }

        return prevStep;
    }

    @Override
    public int getNumberOfSteps()
    {
        return MINIMUM_STEPS;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result)
    {
        return new TaskProgress(0, getNumberOfSteps());
    }
}
