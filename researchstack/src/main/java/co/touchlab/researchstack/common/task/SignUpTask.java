package co.touchlab.researchstack.common.task;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.Step;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignUpTask extends OnboardingTask
{

    public static final int MINIMUM_STEPS = 7;

    public SignUpTask()
    {
        super("SignUp");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;
        User user = ResearchStackApplication.getInstance().getCurrentUser();

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
            currentStepNumber += 1;
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
            currentStepNumber += 1;
        }

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
}
