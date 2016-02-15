package co.touchlab.researchstack.skin.task;

import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.skin.TaskProvider;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignInTask extends OnboardingTask
{
    private boolean hasPasscode;

    public static final int    MINIMUM_STEPS = 0;
    public static final String ID_EMAIL      = "ID_EMAIL";
    public static final String ID_PASSWORD   = "ID_PASSWORD";

    public SignInTask()
    {
        super(TaskProvider.TASK_ID_SIGN_IN);
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;

        if(step == null)
        {
            if(! hasPasscode)
            {
                nextStep = getPassCodeCreationStep();
            }
            else
            {
                nextStep = getSignInStep();
            }
        }
        else if(step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier))
        {
            nextStep = getSignInStep();
        }
        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        // go back to onboarding
        return null;
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

    public void setHasPasscode(boolean hasPasscode)
    {
        this.hasPasscode = hasPasscode;
    }
}
