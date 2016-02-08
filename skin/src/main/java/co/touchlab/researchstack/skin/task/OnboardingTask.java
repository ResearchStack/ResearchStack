package co.touchlab.researchstack.skin.task;

import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.task.Task;
import co.touchlab.researchstack.backbone.ui.step.body.NotImplementedStepBody;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.UiManager;
import co.touchlab.researchstack.skin.step.PassCodeConfirmationStep;
import co.touchlab.researchstack.skin.ui.layout.SignInStepLayout;
import co.touchlab.researchstack.skin.ui.layout.SignUpEligibleStepLayout;
import co.touchlab.researchstack.skin.ui.layout.SignUpIneligibleStepLayout;
import co.touchlab.researchstack.skin.ui.layout.SignUpPinCodeCreationStepLayout;
import co.touchlab.researchstack.skin.ui.layout.SignUpStepLayout;

/**
 * Created by bradleymcdermott on 10/16/15.
 */
public abstract class OnboardingTask extends Task
{
    public static final String SignUpInclusionCriteriaStepIdentifier    = "InclusionCriteria";
    public static final String SignUpEligibleStepIdentifier             = "Eligible";
    public static final String SignUpIneligibleStepIdentifier           = "Ineligible";
    public static final String SignUpPassCodeCreationStepIdentifier     = "PassCodeCreation";
    public static final String SignUpPassCodeConfirmationStepIdentifier = "PassCodeConfirmation";
    public static final String SignUpThankYouStepIdentifier             = "ThankYou";
    public static final String SignInStepIdentifier                     = "SignIn";
    public static final String SignUpStepIdentifier                     = "SignUp";
    //    public static final String SignUpGeneralInfoStepIdentifier        = "GeneralInfo";
    //    public static final String SignUpMedicalInfoStepIdentifier        = "MedicalInfo";
    //    public static final String SignUpCustomInfoStepIdentifier         = "CustomInfo";
    //    public static final String SignUpPermissionsStepIdentifier        = "Permissions";
    //    public static final String SignUpPermissionsPrimingStepIdentifier = "PermissionsPriming";

    @Deprecated // TODO isnt used anywhere and its value is never read.
    protected int          currentStepNumber;
    private   QuestionStep inclusionCriteriaStep;
    private   Step         eligibleStep;
    private   Step         ineligibleStep;
    private   Step         passcodeCreationStep;
    private   PassCodeConfirmationStep         passcodeConfirmationStep;
    private   Step         signUpStep;
    private   Step         permissionsPrimingStep;
    private   Step         generalInfoStep;
    private   Step         medicalInfoStep;
    private   Step         customInfoStep;
    private   Step         permissionsStep;
    private   Step         thankyouStep;
    private   Step         signInStep;
    private boolean eligible                = true;
    private boolean customStepIncluded      = false;
    private boolean permissionScreenSkipped = false;


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
        StepResult<Boolean> stepResult = (StepResult<Boolean>) result.getStepResult(SignUpTask.SignUpInclusionCriteriaStepIdentifier);

        if(stepResult != null)
        {
            return stepResult.getResult();
        }

        return false;
    }

    public Step getSignInStep()
    {
        if(signInStep == null)
        {
            signInStep = new Step(SignInStepIdentifier);
            signInStep.setStepTitle(R.string.sign_in);
            signInStep.setStepLayoutClass(SignInStepLayout.class);
        }
        return signInStep;
    }

    public Step getThankyouStep()
    {
        if(thankyouStep == null)
        {
            thankyouStep = new Step(SignUpThankYouStepIdentifier);
            thankyouStep.setStepTitle(R.string.thank_you);
            thankyouStep.setStepLayoutClass(NotImplementedStepBody.class);
            //            TODO Create SignUpThankYouStepLayout
            //            thankyouStep.setStepLayoutClass(SignUpThankYouStepLayout.class);
        }
        return thankyouStep;
    }

    public Step getIneligibleStep()
    {
        if(ineligibleStep == null)
        {
            ineligibleStep = new Step(SignUpIneligibleStepIdentifier);
            ineligibleStep.setStepTitle(R.string.ineligible);
            ineligibleStep.setStepLayoutClass(SignUpIneligibleStepLayout.class);
        }
        return ineligibleStep;
    }

    public Step getEligibleStep()
    {
        if(eligibleStep == null)
        {
            eligibleStep = new Step(SignUpEligibleStepIdentifier);
            eligibleStep.setStepTitle(R.string.eligibility);
            eligibleStep.setStepLayoutClass(SignUpEligibleStepLayout.class);
        }
        return eligibleStep;
    }

    /**
     * TODO Question is currently a placeholder. Implement unique UI for step.
     * TODO Move string to Resources
     */
    public Step getInclusionCriteriaStep()
    {
        return UiManager.getInstance().getInclusionCriteriaStep();
    }

    //TODO Move string to Resources
    public Step getPassCodeCreationStep()
    {
        if(passcodeCreationStep == null)
        {
            passcodeCreationStep = new Step(SignUpPassCodeCreationStepIdentifier);
            passcodeCreationStep.setStepTitle(R.string.passcode);
            passcodeCreationStep.setTitle("Choose a passcode");
            passcodeCreationStep.setText(
                    "Enter a secure code to protect your data and log in faster.");
            passcodeCreationStep.setStepLayoutClass(SignUpPinCodeCreationStepLayout.class);
        }
        return passcodeCreationStep;
    }

    //TODO Move string to Resources
    public PassCodeConfirmationStep getPassCodeConfirmationStep(String pin)
    {
        if(passcodeConfirmationStep == null)
        {
            passcodeConfirmationStep = new PassCodeConfirmationStep(SignUpPassCodeConfirmationStepIdentifier);
            passcodeConfirmationStep.setTitle("Confirm your passcode");
            passcodeConfirmationStep.setText("Enter your code one more time to confirm.");
        }

        passcodeConfirmationStep.setPin(pin);

        return passcodeConfirmationStep;
    }

    public Step getSignUpStep()
    {
        if(signUpStep == null)
        {
            signUpStep = new Step(SignUpStepIdentifier);
            signUpStep.setStepTitle(R.string.sign_up);
            signUpStep.setStepLayoutClass(SignUpStepLayout.class);
        }
        return signUpStep;
    }
}
