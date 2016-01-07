package co.touchlab.researchstack.glue.task;

import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.User;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignInTask extends OnboardingTask
{

    public static final int MINIMUM_STEPS = 0;

    public SignInTask()
    {
        super("SignIn", "SignIn");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;
        User user = ResearchStack.getInstance().getCurrentUser();

        if(step == null)
        {
            nextStep = getSignInStep();
        }

        if(nextStep == null)
        {
            ResearchStack.getInstance().saveUser();
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
