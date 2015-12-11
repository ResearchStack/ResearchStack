package co.touchlab.researchstack.glue.task;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.glue.ui.scene.SignInScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpAdditionalInfoScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpEligibleScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpGeneralInfoScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpIneligibleScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpPermissionsPrimingScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpPermissionsScene;

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
    private Step permissionsStep;
    private Step thankyouStep;
    private Step signInStep;

    private boolean eligible = true;
    private boolean customStepIncluded = false;
    private boolean permissionScreenSkipped = false;

    protected int currentStepNumber;

    public OnboardingTask(String identifier, String scheduleId)
    {
        super(identifier, scheduleId);
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
        StepResult<Boolean> stepResult = (StepResult<Boolean>) result.getStepResult(SignUpTask.SignUpInclusionCriteriaStepIdentifier);

        if (stepResult != null)
        {
            return stepResult.getResult();
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
            signInStep.setSceneTitle(R.string.sign_in);
            signInStep.setSceneClass(SignInScene.class);
        }
        return signInStep;
    }

    public Step getThankyouStep()
    {
        if (thankyouStep == null)
        {
            thankyouStep = new Step(SignUpThankYouStepIdentifier);
            thankyouStep.setSceneTitle(R.string.thank_you);
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
            permissionsStep.setSceneTitle(R.string.permissions);
            permissionsStep.setSceneClass(SignUpPermissionsScene.class);
        }
        return permissionsStep;
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
            medicalInfoStep.setSceneTitle(R.string.additional_info);
            medicalInfoStep.setSceneClass(SignUpAdditionalInfoScene.class);
        }
        return medicalInfoStep;
    }

    public Step getGeneralInfoStep()
    {
        if (generalInfoStep == null)
        {
            generalInfoStep = new Step(SignUpGeneralInfoStepIdentifier);
            generalInfoStep.setSceneTitle(R.string.registration);
            generalInfoStep.setSceneClass(SignUpGeneralInfoScene.class);
        }
        return generalInfoStep;
    }

    public Step getPermissionsPrimingStep()
    {
        if (permissionsPrimingStep == null)
        {
            permissionsPrimingStep = new Step(SignUpPermissionsPrimingStepIdentifier);
            permissionsPrimingStep.setSceneTitle(R.string.consent);
            permissionsPrimingStep.setSceneClass(SignUpPermissionsPrimingScene.class);
        }
        return permissionsPrimingStep;
    }

    public Step getIneligibleStep()
    {
        if (ineligibleStep == null)
        {
            ineligibleStep = new Step(SignUpIneligibleStepIdentifier);
            ineligibleStep.setSceneTitle(R.string.ineligible);
            ineligibleStep.setSceneClass(SignUpIneligibleScene.class);
        }
        return ineligibleStep;
    }

    public Step getEligibleStep()
    {
        if (eligibleStep == null)
        {
            eligibleStep = new Step(SignUpEligibleStepIdentifier);
            eligibleStep.setSceneTitle(R.string.sign_up);
            eligibleStep.setSceneClass(SignUpEligibleScene.class);
        }
        return eligibleStep;
    }

    public Step getInclusionCriteriaStep()
    {
        if (inclusionCriteriaStep == null)
        {
            inclusionCriteriaStep = new Step(SignUpInclusionCriteriaStepIdentifier);
            inclusionCriteriaStep.setSceneTitle(R.string.eligibility);
            inclusionCriteriaStep.setSceneClass(ResearchStack.getInstance()
                    .getInclusionCriteriaSceneClass());
        }
        return inclusionCriteriaStep;
    }
}
