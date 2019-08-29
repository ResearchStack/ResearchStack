package org.sagebionetworks.researchstack.backbone.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.sagebionetworks.researchstack.backbone.StorageAccess;
import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.result.TaskResult;
import org.sagebionetworks.researchstack.backbone.step.Step;
import org.sagebionetworks.researchstack.backbone.task.Task;
import org.sagebionetworks.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.sagebionetworks.researchstack.backbone.ui.step.layout.StepLayout;
import org.sagebionetworks.researchstack.backbone.ui.step.layout.StepPermissionRequest;
import org.sagebionetworks.researchstack.backbone.utils.TextUtils;
import org.sagebionetworks.researchstack.backbone.DataProvider;
import org.sagebionetworks.researchstack.backbone.PermissionRequestManager;
import org.sagebionetworks.researchstack.backbone.R;
import org.sagebionetworks.researchstack.backbone.TaskProvider;
import org.sagebionetworks.researchstack.backbone.task.OnboardingTask;
import org.sagebionetworks.researchstack.backbone.task.SignUpTask;
import org.sagebionetworks.researchstack.backbone.ui.layout.SignUpEligibleStepLayout;

@Deprecated // use OnboardingManager.getInstance().launchOnboarding(OnboardingTaskType.REGISTRATION, this);
public class SignUpTaskActivity extends ViewTaskActivity implements ActivityCallback {

    TaskResult consentResult;

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, SignUpTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onDataAuth() {
        if (StorageAccess.getInstance().hasPinCode(this)) {
            super.onDataAuth();
        } else // allow signup/in if no pincode
        {
            onDataReady();
        }
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result) {
        // Save result to task
        onSaveStepResult(step.getIdentifier(), result);

        // Save Pin to disk, then save our consent info
        if (action == ACTION_NEXT &&
                step.getIdentifier().equals(OnboardingTask.SignUpPassCodeCreationStepIdentifier)) {
            String pin = (String) result.getResult();
            if (!TextUtils.isEmpty(pin)) {
                StorageAccess.getInstance().createPinCode(this, pin);
                SignUpTask signUpTask = (SignUpTask)getTask();
                signUpTask.setHasPasscode(true);
            }

            if (consentResult != null) {
                saveConsentResultInfo();
            }
        }

        // Show next step
        onExecuteStepAction(action);
    }

    @Override
    public void startConsentTask() {
        Intent intent = ConsentTaskActivity.newIntent(this,
                TaskProvider.getInstance().get(TaskProvider.TASK_ID_CONSENT));
        startActivityForResult(intent, SignUpEligibleStepLayout.CONSENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SignUpEligibleStepLayout.CONSENT_REQUEST) {
            // User has passed through the entire consent flow
            if (resultCode == Activity.RESULT_OK) {
                consentResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

                // If they've already created a pincode, save consent, otherwise go to pin creation

                if (StorageAccess.getInstance().hasPinCode(this)) {
                    saveConsentResultInfo();
                }

                if (getCurrentStep().getIdentifier()
                        .equals(OnboardingTask.SignUpEligibleStepIdentifier)) {
                    showNextStep();
                }
            }

            // User has exited
            else {
                finish();
            }
        } else if (PermissionRequestManager.getInstance()
                .onNonSystemPermissionResult(this, requestCode, resultCode, data)) {
            updateStepLayoutForPermission();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void saveConsentResultInfo() {
        DataProvider.getInstance().saveConsent(this, consentResult);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermission(String id) {
        if (PermissionRequestManager.getInstance().isNonSystemPermission(id)) {
            PermissionRequestManager.getInstance().onRequestNonSystemPermission(this, id);
        } else {
            requestPermissions(new String[]{id}, PermissionRequestManager.PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionRequestManager.PERMISSION_REQUEST_CODE) {
            updateStepLayoutForPermission();
        }
    }

    private void updateStepLayoutForPermission() {
        StepLayout stepLayout = (StepLayout) findViewById(R.id.rsb_current_step);

        if (stepLayout instanceof StepPermissionRequest) {
            ((StepPermissionRequest) stepLayout)
                    .onUpdateForPermissionResult();
        }
    }
}
