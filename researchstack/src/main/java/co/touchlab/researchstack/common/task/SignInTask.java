package co.touchlab.researchstack.common.task;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.Step;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public class SignInTask extends OnboardingTask
{

    public static final int MINIMUM_STEPS = 7;

    public SignInTask()
    {
        super("SignUp", "SignUp");
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result)
    {
        Step nextStep = null;
        User user = ResearchStackApplication.getInstance()
                .getCurrentUser();

        if (step == null)
        {
            nextStep = getSignInStep();
        }
        else if (step.getIdentifier()
                .equals(SignInStepIdentifier))
        {
            currentStepNumber += 1;
            nextStep = getPermissionsPrimingStep();
        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsPrimingStepIdentifier))
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
            nextStep = getPermissionsStep();
            user.setSecondaryInfoSaved(true);
            currentStepNumber += 1;
        }
        else if (step.getIdentifier()
                .equals(SignUpPermissionsStepIdentifier))
        {
            nextStep = getThankyouStep();
            currentStepNumber += 1;
        }
        else if (step.getIdentifier()
                .equals(SignUpThankYouStepIdentifier))
        {
            nextStep = null;
        }

        if(nextStep == null)
            ResearchStackApplication.getInstance().saveUser();

        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result)
    {
        Step prevStep = null;

        if (step.getIdentifier()
                .equals(SignUpMedicalInfoStepIdentifier))
        {
            prevStep = null;
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
