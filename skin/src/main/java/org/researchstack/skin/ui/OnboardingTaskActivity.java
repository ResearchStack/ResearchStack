package org.researchstack.skin.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
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

        // Onboarding Tasks use Step's title for the title, or a lookup table for Titles
        try {
            setActionBarTitle(getString(step.getStepTitle()));
        } catch (Resources.NotFoundException e) {
            setActionBarTitle(stepTitleForIdentifier(step.getIdentifier()));
        }

        setupCustomStepLayouts(superStepLayout);

        return superStepLayout;
    }

    /**
     * Injects TaskResult information into StepLayouts that need more information
     * @param stepLayout step layout that has just been instantiated
     */
    public void setupCustomStepLayouts(StepLayout stepLayout) {
        // Check here for StepLayouts that need results fed into them
        if (stepLayout instanceof EmailVerificationStepLayout) {
            EmailVerificationStepLayout emailStepLayout = (EmailVerificationStepLayout)stepLayout;
            StepResult passwordResult = StepResultHelper
                    .findStepResult(taskResult, ProfileInfoOption.PASSWORD.getIdentifier());
            if (passwordResult != null) {
                emailStepLayout.setPassword((String)passwordResult.getResult());
            }
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    public String stepTitleForIdentifier(String identifier) {
        switch (identifier) {
            //case
        }
        return "";
    }
}
