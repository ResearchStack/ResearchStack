package co.touchlab.researchstack.common.task;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.Step;
import co.touchlab.researchstack.ui.scene.SignInScene;
import co.touchlab.researchstack.ui.scene.SignUpAdditionalInfoScene;
import co.touchlab.researchstack.ui.scene.SignUpEligibleScene;
import co.touchlab.researchstack.ui.scene.SignUpGeneralInfoScene;
import co.touchlab.researchstack.ui.scene.SignUpIneligibleScene;
import co.touchlab.researchstack.ui.scene.SignUpPasscodeScene;
import co.touchlab.researchstack.ui.scene.SignUpPermissionsPrimingScene;
import co.touchlab.researchstack.ui.scene.SignUpPermissionsScene;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public abstract class OnboardingTask extends Task
{
    public static final String SignUpInclusionCriteriaStepIdentifier = "InclusionCriteria";
    public static final String SignUpEligibleStepIdentifier = "Eligible";
    public static final String SignUpIneligibleStepIdentifier = "Ineligible";
    public static final String SignUpGeneralInfoStepIdentifier = "GeneralInfo";
    public static final String SignUpMedicalInfoStepIdentifier = "MedicalInfo";
    public static final String SignUpCustomInfoStepIdentifier = "CustomInfo";
    public static final String SignUpPasscodeStepIdentifier = "Passcode";
    public static final String SignUpPermissionsStepIdentifier = "Permissions";
    public static final String SignUpThankYouStepIdentifier = "ThankYou";
    public static final String SignInStepIdentifier = "SignIn";
    public static final String SignUpPermissionsPrimingStepIdentifier = "PermissionsPriming";
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

    public boolean isEligible(TaskResult result)
    {
        StepResult stepResult = result.getStepResultForStepIdentifier(SignUpTask.SignUpInclusionCriteriaStepIdentifier);

        if (stepResult != null)
        {
            QuestionResult<Boolean> questionResult = (QuestionResult<Boolean>) stepResult.getResultForIdentifier(SignUpTask.SignUpInclusionCriteriaStepIdentifier);
            return questionResult.getAnswer();
        }

        return false;
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
        if (signInStep == null)
        {
            signInStep = new Step(SignInStepIdentifier);
            signInStep.setSceneClass(SignInScene.class);
        }
        return signInStep;
    }

    public Step getThankyouStep()
    {
        if (thankyouStep == null)
        {
            thankyouStep = new Step(SignUpThankYouStepIdentifier);
            //TODO Create SignUpThankYouScene
//            thankyouStep.setSceneClass(SignUpThankYouScene.class);
        }
        return thankyouStep;
    }

    public Step getPermissionsStep()
    {
        if (permissionsStep == null)
        {
            permissionsStep = new Step(SignUpPermissionsStepIdentifier);
            permissionsStep.setSceneClass(SignUpPermissionsScene.class);
        }
        return permissionsStep;
    }

    public Step getPasscodeStep()
    {
        if (passcodeStep == null)
        {
            passcodeStep = new Step(SignUpPasscodeStepIdentifier);
            passcodeStep.setSceneClass(SignUpPasscodeScene.class);
        }
        return passcodeStep;
    }

    public Step getCustomInfoStep()
    {
        if (customInfoStep == null)
        {
            customInfoStep = new Step(SignUpCustomInfoStepIdentifier);
        }
        return customInfoStep;
    }

    public Step getMedicalInfoStep()
    {
        if (medicalInfoStep == null)
        {
            medicalInfoStep = new Step(SignUpMedicalInfoStepIdentifier);
            medicalInfoStep.setSceneClass(SignUpAdditionalInfoScene.class);
        }
        return medicalInfoStep;
    }

    public Step getGeneralInfoStep()
    {
        if (generalInfoStep == null)
        {
            generalInfoStep = new Step(SignUpGeneralInfoStepIdentifier);
            generalInfoStep.setSceneClass(SignUpGeneralInfoScene.class);
        }
        return generalInfoStep;
    }

    public Step getPermissionsPrimingStep()
    {
        if (permissionsPrimingStep == null)
        {
            permissionsPrimingStep = new Step(SignUpPermissionsPrimingStepIdentifier);
            permissionsPrimingStep.setSceneClass(SignUpPermissionsPrimingScene.class);
        }
        return permissionsPrimingStep;
    }

    public Step getIneligibleStep()
    {
        if (ineligibleStep == null)
        {
            ineligibleStep = new Step(SignUpIneligibleStepIdentifier);
            ineligibleStep.setSceneClass(SignUpIneligibleScene.class);
        }
        return ineligibleStep;
    }

    public Step getEligibleStep()
    {
        if (eligibleStep == null)
        {
            eligibleStep = new Step(SignUpEligibleStepIdentifier);
            eligibleStep.setSceneClass(SignUpEligibleScene.class);
        }
        return eligibleStep;
    }

    public Step getInclusionCriteriaStep()
    {
        if (inclusionCriteriaStep == null)
        {
            inclusionCriteriaStep = new Step(SignUpInclusionCriteriaStepIdentifier);
            inclusionCriteriaStep.setSceneClass(ResearchStackApplication.getInstance()
                    .getInclusionCriteriaSceneClass());
        }
        return inclusionCriteriaStep;
    }
}
