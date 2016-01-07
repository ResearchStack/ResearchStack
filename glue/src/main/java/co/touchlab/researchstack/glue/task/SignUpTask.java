package co.touchlab.researchstack.glue.task;

import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.User;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignUpTask extends OnboardingTask
{

    public static final int MINIMUM_STEPS = 2;

    public SignUpTask()
    {
        super("SignUp", "SignUp");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;
        User user = ResearchStack.getInstance().getCurrentUser();

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
            currentStepNumber += 1;
            nextStep = getSignUpStep();
        }

        // TODO get rid of this
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
        else if(step.getIdentifier().equals(SignUpStepIdentifier))
        {
            prevStep = getEligibleStep();

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
        else if(step.getIdentifier().equals(SignUpStepIdentifier))
        {
            stepPosition = 2;

        }

        return new TaskProgress(stepPosition, getNumberOfSteps());
    }
}
