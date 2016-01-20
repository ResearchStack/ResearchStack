package co.touchlab.researchstack.glue.task;

import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.step.body.NotImplementedStepBody;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.step.PassCodeConfirmationStep;
import co.touchlab.researchstack.glue.ui.scene.SignInStepLayout;
import co.touchlab.researchstack.glue.ui.scene.SignUpEligibleStepLayout;
import co.touchlab.researchstack.glue.ui.scene.SignUpIneligibleStepLayout;
import co.touchlab.researchstack.glue.ui.scene.SignUpPinCodeCreationStepLayout;
import co.touchlab.researchstack.glue.ui.scene.SignUpStepLayout;

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
            signInStep.setSceneTitle(R.string.sign_in);
            signInStep.setSceneClass(SignInStepLayout.class);
        }
        return signInStep;
    }

    public Step getThankyouStep()
    {
        if(thankyouStep == null)
        {
            thankyouStep = new Step(SignUpThankYouStepIdentifier);
            thankyouStep.setSceneTitle(R.string.thank_you);
            thankyouStep.setSceneClass(NotImplementedStepBody.class);
            //            TODO Create SignUpThankYouScene
            //            thankyouStep.setSceneClass(SignUpThankYouScene.class);
        }
        return thankyouStep;
    }

    public Step getIneligibleStep()
    {
        if(ineligibleStep == null)
        {
            ineligibleStep = new Step(SignUpIneligibleStepIdentifier);
            ineligibleStep.setSceneTitle(R.string.ineligible);
            ineligibleStep.setSceneClass(SignUpIneligibleStepLayout.class);
        }
        return ineligibleStep;
    }

    public Step getEligibleStep()
    {
        if(eligibleStep == null)
        {
            eligibleStep = new Step(SignUpEligibleStepIdentifier);
            eligibleStep.setSceneTitle(R.string.eligibility);
            eligibleStep.setSceneClass(SignUpEligibleStepLayout.class);
        }
        return eligibleStep;
    }

    /**
     * TODO Question is currently a placeholder. Implement unique UI for step.
     * TODO Move string to Resources
     */
    public Step getInclusionCriteriaStep()
    {
        if(inclusionCriteriaStep == null)
        {
            Choice<Boolean> human = new Choice<>("Yes, I am a human.", true, null);
            Choice<Boolean> robot = new Choice<>("No, I am a robot but I am sentient and concerned about my health.", true, null);
            Choice<Boolean> alien = new Choice<>("No, I’m an alien.", false, null);

            inclusionCriteriaStep = new QuestionStep(SignUpInclusionCriteriaStepIdentifier);
            inclusionCriteriaStep.setSceneTitle(R.string.eligibility);
            inclusionCriteriaStep.setSceneClass(ResearchStack.getInstance()
                    .getInclusionCriteriaSceneClass());
            inclusionCriteriaStep.setTitle(
                    "Were you born somewhere on planet earth and are you a human-ish?");
            inclusionCriteriaStep.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                    human,
                    robot,
                    alien));
        }
        return inclusionCriteriaStep;
    }

    //TODO Move string to Resources
    public Step getPassCodeCreationStep()
    {
        if(passcodeCreationStep == null)
        {
            passcodeCreationStep = new Step(SignUpPassCodeCreationStepIdentifier);
            passcodeCreationStep.setSceneTitle(R.string.passcode);
            passcodeCreationStep.setTitle("Choose a passcode");
            passcodeCreationStep.setText(
                    "Enter a secure code to protect your data and log in faster.");
            passcodeCreationStep.setSceneClass(SignUpPinCodeCreationStepLayout.class);
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
            passcodeConfirmationStep.setPin(pin);
        }
        return passcodeConfirmationStep;
    }

    public Step getSignUpStep()
    {
        if(signUpStep == null)
        {
            signUpStep = new Step(SignUpStepIdentifier);
            signUpStep.setSceneTitle(R.string.sign_up);
            signUpStep.setSceneClass(SignUpStepLayout.class);
        }
        return signUpStep;
    }
}
