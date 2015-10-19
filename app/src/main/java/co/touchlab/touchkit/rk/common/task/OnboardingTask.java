package co.touchlab.touchkit.rk.common.task;

import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.Step;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public abstract class OnboardingTask extends Task
{
    public static final String SignUpInclusionCriteriaStepIdentifier   = "InclusionCriteria";
    public static final String SignUpEligibleStepIdentifier            = "Eligible";
    public static final String SignUpIneligibleStepIdentifier          = "Ineligible";
    public static final String SignUpGeneralInfoStepIdentifier         = "GeneralInfo";
    public static final String SignUpMedicalInfoStepIdentifier         = "MedicalInfo";
    public static final String SignUpCustomInfoStepIdentifier          = "CustomInfo";
    public static final String SignUpPasscodeStepIdentifier            = "Passcode";
    public static final String SignUpPermissionsStepIdentifier         = "Permissions";
    public static final String SignUpThankYouStepIdentifier            = "ThankYou";
    public static final String SignInStepIdentifier                    = "SignIn";
    public static final String SignUpPermissionsPrimingStepIdentifier  = "PermissionsPriming";
    private Step inclusionCriteriaStep;
    private Step eligibleStep;
    private Step ineligibleStep;
    private Step permissionsPrimingStep;
    private Step generalInfoStep;
    private Step medicalInfoStep;
    private Step customInfoStep;
    private Step passcodeStep;
    private Step permissionsStep;
    private Step thankyouStep;
    private Step signInStep;

    private boolean eligible = true;
    private boolean customStepIncluded = false;
    private boolean permissionScreenSkipped = false;

    protected int currentStepNumber;

    public OnboardingTask(String identifier)
    {
        super(identifier);
    }

    @Override
    public Step getStepWithIdentifier(String identifier)
    {
        return null;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result)
    {
        return null;
    }

    @Override
    public void validateParameters()
    {

    }

    public boolean isEligible()
    {
        return eligible;
    }

    public void setEligible(boolean eligible)
    {
        this.eligible = eligible;
    }

    public boolean isCustomStepIncluded()
    {
        return customStepIncluded;
    }

    public void setCustomStepIncluded(boolean customStepIncluded)
    {
        this.customStepIncluded = customStepIncluded;
    }

    public boolean isPermissionScreenSkipped()
    {
        return permissionScreenSkipped;
    }

    public void setPermissionScreenSkipped(boolean permissionScreenSkipped)
    {
        this.permissionScreenSkipped = permissionScreenSkipped;
    }

    public Step getSignInStep()
    {
        if(signInStep == null)
        {
            signInStep = new Step(SignInStepIdentifier);
        }
        return signInStep;
    }

    public Step getThankyouStep()
    {
        if(thankyouStep == null)
        {
            thankyouStep = new Step(SignUpThankYouStepIdentifier);
        }
        return thankyouStep;
    }

    public Step getPermissionsStep()
    {
        if(permissionsStep == null)
        {
            permissionsStep = new Step(SignUpPermissionsStepIdentifier);
        }
        return permissionsStep;
    }

    public Step getPasscodeStep()
    {
        if(passcodeStep == null)
        {
            passcodeStep = new Step(SignUpPasscodeStepIdentifier);
        }
        return passcodeStep;
    }

    public Step getCustomInfoStep()
    {
        if(customInfoStep == null)
        {
            customInfoStep = new Step(SignUpCustomInfoStepIdentifier);
        }
        return customInfoStep;
    }

    public Step getMedicalInfoStep()
    {
        if(medicalInfoStep == null)
        {
            medicalInfoStep = new Step(SignUpMedicalInfoStepIdentifier);
        }
        return medicalInfoStep;
    }

    public Step getGeneralInfoStep()
    {
        if(generalInfoStep == null)
        {
            generalInfoStep = new Step(SignUpGeneralInfoStepIdentifier);
        }
        return generalInfoStep;
    }

    public Step getPermissionsPrimingStep()
    {
        if(permissionsPrimingStep == null)
        {
            permissionsPrimingStep = new Step(SignUpPermissionsPrimingStepIdentifier);
        }
        return permissionsPrimingStep;
    }

    public Step getIneligibleStep()
    {
        if(ineligibleStep == null)
        {
            ineligibleStep = new Step(SignUpIneligibleStepIdentifier);
        }
        return ineligibleStep;
    }

    public Step getEligibleStep()
    {
        if(eligibleStep == null)
        {
            eligibleStep = new Step(SignUpEligibleStepIdentifier);
        }
        return eligibleStep;
    }

    public Step getInclusionCriteriaStep()
    {
        if(inclusionCriteriaStep == null)
        {
            inclusionCriteriaStep = new Step(SignUpInclusionCriteriaStepIdentifier);
        }
        return inclusionCriteriaStep;
    }
}
