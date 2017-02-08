package org.researchstack.backbone.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.researchstack.backbone.ui.step.layout.EmailVerificationStepLayout;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.step.layout.StepPermissionRequest;
import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.StepResultHelper;

/**
 * Created by TheMDP on 1/14/17.
 *
 * OnboardingTaskActivity serves as the root task activity during the onboarding process
 * It is not much different than its base class ViewTaskActivity, except
 * that it does not allow the pin code view to be shown
 */

public class OnboardingTaskActivity extends ViewTaskActivity implements ActivityCallback {

    /**
     * Used to maintain the step title from the previous step to show on the next step
     * in the case that the next step does not have a valid step title
     */
    protected String previousStepTitle;

    /**
     * @param context used to create intent
     * @param task any task will be displayed correctly
     * @return launchable intent for OnboardingTaskActivity
     */
    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, OnboardingTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onDataAuth()
    {
        // Onboarding tasks skip pin code data auth and go right to data ready
        onDataReady();
    }

    @Override
    protected StepLayout getLayoutForStep(Step step)
    {
        StepLayout superStepLayout = super.getLayoutForStep(step);

        // Onboarding Tasks will keep the previous step title if none is available
        String title = task.getTitleForStep(this, step);
        if (title == null) {
            setActionBarTitle(previousStepTitle);
        } else {
            previousStepTitle = title;
        }

        setupCustomStepLayouts(step, superStepLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(shouldShowBackButton(step));
        }

        return superStepLayout;
    }

    @Override
    @SuppressWarnings("unchecked")  // needed for unchecked StepResult generic type casting
    public void onSaveStep(int action, Step step, StepResult result) {
        if (step instanceof EmailVerificationStep) {
            StepResult passwordResult = StepResultHelper
                    .findStepResult(result, ProfileInfoOption.PASSWORD.getIdentifier());

            // If there is a new password from the EmailVerificationStep,
            // that means that the user has changed their email and password,
            // and we need to replace the password result from the previous RegistrationStep's step result
            if (passwordResult != null) {
                StepResult originalPasswordResult = StepResultHelper
                        .findStepResult(taskResult, ProfileInfoOption.PASSWORD.getIdentifier());
                if (originalPasswordResult != null) {
                    originalPasswordResult.setResult(passwordResult.getResult());
                }
            }
        }

        super.onSaveStep(action, step, result);
    }

    /**
     * Injects TaskResult information into StepLayouts that need more information
     * @param step the step that is about to be displayed
     * @param stepLayout step layout that has just been instantiated
     */
    public void setupCustomStepLayouts(Step step, StepLayout stepLayout) {
        // Check here for StepLayouts that need results fed into them
        if (stepLayout instanceof EmailVerificationStepLayout) {
            EmailVerificationStepLayout emailStepLayout = (EmailVerificationStepLayout)stepLayout;
            // Try and find the password step result, but exclude the EmailVerificationStep
            // as a source for the password, since it will be handled internally by that class
            if (taskResult != null) {
                StepResult emailStepResult = taskResult.getResults().get(step.getIdentifier());
                if (emailStepResult != null) {
                    taskResult.getResults().remove(step.getIdentifier());
                }

                StepResult passwordResult = StepResultHelper
                        .findStepResult(taskResult, ProfileInfoOption.PASSWORD.getIdentifier());
                if (passwordResult != null) {
                    emailStepLayout.setPassword((String) passwordResult.getResult());
                }

                // Re-add email step task result
                if (emailStepResult != null) {
                    taskResult.getResults().put(step.getIdentifier(), emailStepResult);
                }
            }
        }
    }

    /**
     * Clear out all the data that has been saved by this Activity
     * And push user back to the Overview screen, or whatever screen was below this Activity
     */
    @Override
    protected void discardResultsAndFinish() {
        DataProvider.getInstance().signOut(this);
        StorageAccess.getInstance().removePinCode(this);
        super.discardResultsAndFinish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermission(String id) {
        if (PermissionRequestManager.getInstance().isNonSystemPermission(id)) {
            PermissionRequestManager.getInstance().onRequestNonSystemPermission(this, id);
        } else {
            requestPermissions(new String[] {id}, PermissionRequestManager.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionRequestManager.PERMISSION_REQUEST_CODE) {
            updateStepLayoutForPermission();
        }
    }

    protected void updateStepLayoutForPermission() {
        StepLayout stepLayout = (StepLayout) findViewById(R.id.rsb_current_step);
        if(stepLayout instanceof StepPermissionRequest) {
            ((StepPermissionRequest) stepLayout).onUpdateForPermissionResult();
        }
    }

    @Override
    @Deprecated
    public void startConsentTask() {
        // deprecated
    }

    public boolean shouldShowBackButton(Step step) {
        switch (step.getIdentifier()) {
            case OnboardingSection.EMAIL_VERIFICATION_IDENTIFIER:
                return false;
            case OnboardingSection.REGISTRATION_IDENTIFIER:
                return false;
        }
        return true;
    }
}
