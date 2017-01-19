package org.researchstack.skin.task;

import android.content.Context;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.skin.PermissionRequestManager;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.UiManager;


public class SignUpTask extends OnboardingTask {
    public static final int MINIMUM_STEPS = 2;
    public static final String ID_EMAIL = "ID_EMAIL";
    public static final String ID_PASSWORD = "ID_PASSWORD";
    private boolean hasPasscode;
    private Step inclusionCriteriaStep;

    public SignUpTask(Context context) {
        super(TaskProvider.TASK_ID_SIGN_UP);
        // creating here so it has access to context
        inclusionCriteriaStep = UiManager.getInstance().getInclusionCriteriaStep(context);
        inclusionCriteriaStep.setOptional(false);
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result) {
        Step nextStep = null;

        if (step == null) {
            nextStep = inclusionCriteriaStep;
        } else if (step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier)) {
            if (UiManager.getInstance().isInclusionCriteriaValid(result.getStepResult(step.getIdentifier()))) {
                nextStep = getEligibleStep();
            } else {
                nextStep = getIneligibleStep();
            }
        } else if (step.getIdentifier().equals(SignUpEligibleStepIdentifier)) {
            if (!hasPasscode) {
                nextStep = getPassCodeCreationStep();
            } else if (!PermissionRequestManager.getInstance().getPermissionRequests().isEmpty()) {
                nextStep = getPermissionStep();
            } else {
                nextStep = getSignUpStep();
            }
        } else if (step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier)) {
            if (!PermissionRequestManager.getInstance().getPermissionRequests().isEmpty()) {
                nextStep = getPermissionStep();
            } else {
                nextStep = getSignUpStep();
            }
        } else if (step.getIdentifier().equals(SignUpPermissionsStepIdentifier)) {
            nextStep = getSignUpStep();
        }

        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result) {
        Step prevStep = null;

        if (step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier)) {
            prevStep = null;
        } else if (step.getIdentifier().equals(SignUpEligibleStepIdentifier)) {
            prevStep = inclusionCriteriaStep;

        } else if (step.getIdentifier().equals(SignUpIneligibleStepIdentifier)) {
            prevStep = inclusionCriteriaStep;

        } else if (step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier)) {
            prevStep = getEligibleStep();
        } else if (step.getIdentifier().equals(SignUpPermissionsStepIdentifier)) {
            if (hasPasscode) {
                // Force user to create a new pin
                prevStep = getPassCodeCreationStep();
            } else {
                prevStep = getEligibleStep();
            }
        } else if (step.getIdentifier().equals(SignUpStepIdentifier)) {
            if (!PermissionRequestManager.getInstance().getPermissionRequests().isEmpty()) {
                prevStep = getPermissionStep();
            } else if (hasPasscode) {
                // Force user to create a new pin
                prevStep = getPassCodeCreationStep();
            } else {
                prevStep = getEligibleStep();
            }
        }

        return prevStep;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
        int stepPosition = 0;

        if (step == null || step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier)) {
            stepPosition = 0;
        } else if (step.getIdentifier().equals(SignUpEligibleStepIdentifier)) {
            stepPosition = 1;

        } else if (step.getIdentifier().equals(SignUpIneligibleStepIdentifier)) {
            stepPosition = 1;

        } else if (step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier)) {
            stepPosition = 2;

        } else if (step.getIdentifier().equals(SignUpPermissionsStepIdentifier)) {
            stepPosition = 3;

        } else if (step.getIdentifier().equals(SignUpStepIdentifier)) {
            stepPosition = 4;

        }

        return new TaskProgress(stepPosition, MINIMUM_STEPS);
    }

    public void setHasPasscode(boolean hasPasscode) {
        this.hasPasscode = hasPasscode;
    }
}
