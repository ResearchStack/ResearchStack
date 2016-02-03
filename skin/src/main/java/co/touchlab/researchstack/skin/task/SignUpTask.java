package co.touchlab.researchstack.skin.task;

import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;

/**
 * TODO Needs Refactor
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignUpTask extends OnboardingTask
{
    private boolean hasAuth;

    public static final int MINIMUM_STEPS = 2;

    public static final String ID_EMAIL    = "ID_EMAIL";
    public static final String ID_USERNAME = "ID_EMAIL";
    public static final String ID_PASSWORD = "ID_PASSWORD";


    public SignUpTask()
    {
        super("SignUp");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;

        if(step == null)
        {
            nextStep = getInclusionCriteriaStep();
        }
        else if(step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier))
        {
            if(isEligible(result))
            {
                nextStep = getEligibleStep();
            }
            else
            {
                nextStep = getIneligibleStep();
            }
        }
        else if(step.getIdentifier().equals(SignUpEligibleStepIdentifier))
        {
            if(hasAuth)
            {
                nextStep = getPassCodeCreationStep();
            }
            else
            {
                nextStep = getSignUpStep();
            }
        }
        else if(step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier))
        {
            String pin = (String) result.getStepResult(SignUpPassCodeCreationStepIdentifier)
                    .getResult();

            nextStep =  getPassCodeConfirmationStep(pin);
        }
        else if(step.getIdentifier().equals(SignUpPassCodeConfirmationStepIdentifier))
        {
            nextStep = getSignUpStep();
        }

        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        Step prevStep = null;

        if(step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier))
        {
            prevStep = null;
        }
        else if(step.getIdentifier().equals(SignUpEligibleStepIdentifier))
        {
            prevStep = getInclusionCriteriaStep();

        }
        else if(step.getIdentifier().equals(SignUpIneligibleStepIdentifier))
        {
            prevStep = getInclusionCriteriaStep();

        }
        else if(step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier))
        {
            prevStep = getEligibleStep();
        }
        else if(step.getIdentifier().equals(SignUpPassCodeConfirmationStepIdentifier))
        {
            prevStep = getPassCodeCreationStep();
        }
        else if(step.getIdentifier().equals(SignUpStepIdentifier))
        {
            if(hasAuth)
            {
                // Force user to create a new pin
                prevStep = getPassCodeCreationStep();
            }
            else
            {
                prevStep = getEligibleStep();
            }
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
        int stepPosition = 0;

        if(step == null || step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier))
        {
            stepPosition = 0;
        }
        else if(step.getIdentifier().equals(SignUpEligibleStepIdentifier))
        {
            stepPosition = 1;

        }
        else if(step.getIdentifier().equals(SignUpIneligibleStepIdentifier))
        {
            stepPosition = 1;

        }
        else if(step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier))
        {
            stepPosition = 2;

        }
        else if(step.getIdentifier().equals(SignUpPassCodeConfirmationStepIdentifier))
        {
            stepPosition = 3;

        }
        else if(step.getIdentifier().equals(SignUpStepIdentifier))
        {
            stepPosition = 4;

        }

        return new TaskProgress(stepPosition, getNumberOfSteps());
    }

    public void setHasAuth(boolean hasAuth)
    {
        this.hasAuth = hasAuth;
    }
}
