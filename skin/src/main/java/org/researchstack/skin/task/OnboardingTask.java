package org.researchstack.skin.task;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.step.body.NotImplementedStepBody;
import org.researchstack.skin.R;
import org.researchstack.backbone.step.PassCodeCreationStep;
import org.researchstack.backbone.ui.step.layout.PermissionStepLayout;
import org.researchstack.skin.ui.layout.SignInStepLayout;
import org.researchstack.skin.ui.layout.SignUpStepLayout;

@Deprecated // No longer needed with new OnboardingManager
public abstract class OnboardingTask extends Task {
    public static final String SignUpInclusionCriteriaStepIdentifier = "InclusionCriteria";
    public static final String SignUpEligibleStepIdentifier = "Eligible";
    public static final String SignUpIneligibleStepIdentifier = "Ineligible";
    public static final String SignUpPassCodeCreationStepIdentifier = "PassCodeCreation";
    public static final String SignUpThankYouStepIdentifier = "ThankYou";
    public static final String SignInStepIdentifier = "SignIn";
    public static final String SignUpStepIdentifier = "SignUp";
    //    public static final String SignUpGeneralInfoStepIdentifier        = "GeneralInfo";
    //    public static final String SignUpMedicalInfoStepIdentifier        = "MedicalInfo";
    //    public static final String SignUpCustomInfoStepIdentifier         = "CustomInfo";
    public static final String SignUpPermissionsStepIdentifier = "Permissions";
    //    public static final String SignUpPermissionsPrimingStepIdentifier = "PermissionsPriming";

    @Deprecated
    protected int currentStepNumber;
    private Step eligibleStep;
    private Step ineligibleStep;
    private PassCodeCreationStep passcodeCreationStep;
    private Step signUpStep;
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


    public OnboardingTask(String identifier) {
        super(identifier);
    }

    @Override
    public Step getStepWithIdentifier(String identifier) {
        return null;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
        return null;
    }

    @Override
    public void validateParameters() {

    }

    public boolean isEligible(TaskResult result) {
        StepResult<Boolean> stepResult = (StepResult<Boolean>) result.getStepResult(SignUpTask.SignUpInclusionCriteriaStepIdentifier);

        if (stepResult != null) {
            return stepResult.getResult();
        }

        return false;
    }

    public Step getSignInStep() {
        if (signInStep == null) {
            signInStep = new Step(SignInStepIdentifier);
            signInStep.setStepTitle(R.string.rss_sign_in);
            signInStep.setStepLayoutClass(SignInStepLayout.class);
        }
        return signInStep;
    }

    public Step getThankyouStep() {
        if (thankyouStep == null) {
            thankyouStep = new Step(SignUpThankYouStepIdentifier);
            thankyouStep.setStepTitle(R.string.rss_thank_you);
            thankyouStep.setStepLayoutClass(NotImplementedStepBody.class);
            //            thankyouStep.setStepLayoutClass(SignUpThankYouStepLayout.class);
        }
        return thankyouStep;
    }

    public Step getPassCodeCreationStep() {
        if (passcodeCreationStep == null) {
            passcodeCreationStep = new PassCodeCreationStep(SignUpPassCodeCreationStepIdentifier,
                    R.string.rsb_passcode);
        }
        return passcodeCreationStep;
    }

    public Step getSignUpStep() {
        if (signUpStep == null) {
            signUpStep = new Step(SignUpStepIdentifier);
            signUpStep.setStepTitle(R.string.rss_sign_up);
            signUpStep.setStepLayoutClass(SignUpStepLayout.class);
        }
        return signUpStep;
    }

    public Step getPermissionStep() {
        if (permissionsStep == null) {
            permissionsStep = new Step(SignUpPermissionsStepIdentifier);
            permissionsStep.setStepTitle(R.string.rsb_permissions);
            permissionsStep.setStepLayoutClass(PermissionStepLayout.class);
        }
        return permissionsStep;
    }
}
