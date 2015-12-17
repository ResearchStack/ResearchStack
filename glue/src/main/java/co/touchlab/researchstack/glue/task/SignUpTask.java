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

    public static final int MINIMUM_STEPS = 7;

    public SignUpTask()
    {
        super("SignUp", "SignUp");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;
        User user = ResearchStack.getInstance().getCurrentUser();

        if (step == null)
        {
            nextStep = getInclusionCriteriaStep();
        }
        else if (step.getIdentifier()
                .equals(SignUpInclusionCriteriaStepIdentifier))
        {
            if (isEligible(result))
            {
                nextStep = getEligibleStep();
            }
            else
            {
                nextStep = getIneligibleStep();
            }
        }
        else if (step.getIdentifier()
                .equals(SignUpEligibleStepIdentifier))
        {
            currentStepNumber += 1;
            nextStep = getPermissionsPrimingStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsPrimingStepIdentifier))
        {
            currentStepNumber += 1;
            nextStep = getGeneralInfoStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpGeneralInfoStepIdentifier))
        {
            currentStepNumber += 1;
            nextStep = getMedicalInfoStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpMedicalInfoStepIdentifier))
        {
            if (isCustomStepIncluded())
            {
                nextStep = getCustomInfoStep();
                currentStepNumber += 1;
            }
            else
            {
                if (isPermissionScreenSkipped())
                {
                    nextStep = null;
                }
                else
                {
                    nextStep = getPermissionsStep();
                    currentStepNumber += 1;
                }
            }

        }
        else if (step.getIdentifier()
                .equals(SignUpCustomInfoStepIdentifier))
        {
            if (isPermissionScreenSkipped())
            {
                nextStep = null;
            }
            else
            {
                nextStep = getPermissionsStep();
                currentStepNumber += 1;
            }

        }

        if(nextStep == null)
            ResearchStack.getInstance().saveUser();

        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        Step prevStep = null;

        if (step.getIdentifier()
                .equals(SignUpInclusionCriteriaStepIdentifier))
        {
            prevStep = null;
        }
        else if (step.getIdentifier()
                .equals(SignUpEligibleStepIdentifier))
        {
            prevStep = getInclusionCriteriaStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpIneligibleStepIdentifier))
        {
            prevStep = getInclusionCriteriaStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsPrimingStepIdentifier))
        {
            prevStep = getEligibleStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpGeneralInfoStepIdentifier))
        {
            prevStep = getPermissionsPrimingStep();

        }
        else if (step.getIdentifier()
                .equals(SignUpMedicalInfoStepIdentifier))
        {
            prevStep = getGeneralInfoStep();
            currentStepNumber -= 1;
        }
        else if (step.getIdentifier()
                .equals(SignUpCustomInfoStepIdentifier))
        {
            prevStep = getMedicalInfoStep();
            currentStepNumber -= 1;
        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsStepIdentifier))
        {
            if (isCustomStepIncluded())
            {
                prevStep = getCustomInfoStep();
            }
            else
            {
                prevStep = getMedicalInfoStep();
            }
            currentStepNumber -= 1;
        }

        return prevStep;
    }

    @Override
    public int getNumberOfSteps()
    {
        return isCustomStepIncluded() ? MINIMUM_STEPS + 1 : MINIMUM_STEPS;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result)
    {
        int stepPosition = 0;

        if (step == null || step.getIdentifier()
                .equals(SignUpInclusionCriteriaStepIdentifier))
        {
            stepPosition = 0;
        }
        else if (step.getIdentifier()
                .equals(SignUpEligibleStepIdentifier))
        {
            stepPosition = 1;

        }
        else if (step.getIdentifier()
                .equals(SignUpIneligibleStepIdentifier))
        {
            stepPosition = 2;

        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsPrimingStepIdentifier))
        {
            stepPosition = 3;

        }
        else if (step.getIdentifier()
                .equals(SignUpGeneralInfoStepIdentifier))
        {
            stepPosition = 4;

        }
        else if (step.getIdentifier()
                .equals(SignUpMedicalInfoStepIdentifier))
        {
            stepPosition = 5;
        }
        else if (step.getIdentifier()
                .equals(SignUpCustomInfoStepIdentifier))
        {
            stepPosition = 6;
        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsStepIdentifier))
        {
            stepPosition = 7;
        }

        return new TaskProgress(stepPosition, getNumberOfSteps());
    }
}
