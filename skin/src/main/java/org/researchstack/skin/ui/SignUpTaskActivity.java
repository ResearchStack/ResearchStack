package org.researchstack.skin.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.ui.layout.SignUpEligibleStepLayout;
import org.researchstack.skin.ui.layout.SignUpPermissionsStepLayout;

public class SignUpTaskActivity extends ViewTaskActivity implements ActivityCallback
{

    TaskResult consentResult;

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context, SignUpTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result)
    {
        // Save result to task
        onSaveStepResult(step.getIdentifier(), result);

        // Save Pin to disk, then save our consent info
        if(action == ACTION_NEXT &&
                step.getIdentifier().equals(OnboardingTask.SignUpPassCodeCreationStepIdentifier))
        {
            String pin = (String) result.getResult();
            if(! TextUtils.isEmpty(pin))
            {
                StorageAccess.getInstance().setPinCode(this, pin);
            }

            if(consentResult != null)
            {
                saveConsentResultInfo();
            }
        }

        // Show next step
        onExecuteStepAction(action);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void requestPermissions()
    {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                SignUpPermissionsStepLayout.LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void startConsentTask()
    {
        Task task = TaskProvider.getInstance().get(TaskProvider.TASK_ID_CONSENT);
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, SignUpEligibleStepLayout.CONSENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == SignUpEligibleStepLayout.CONSENT_REQUEST)
        {
            // User has passed through the entire consent flow
            if(resultCode == Activity.RESULT_OK)
            {
                consentResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

                // If they've already created a pincode, save consent, otherwise go to pin creation

                if(StorageAccess.getInstance().hasPinCode(this))
                {
                    saveConsentResultInfo();
                }

                if(getCurrentStep().getIdentifier()
                        .equals(OnboardingTask.SignUpEligibleStepIdentifier))
                {
                    showNextStep();
                }
            }

            // User has exited
            else
            {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveConsentResultInfo()
    {
        DataProvider.getInstance().saveConsent(this, consentResult);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == SignUpPermissionsStepLayout.LOCATION_PERMISSION_REQUEST_CODE)
        {
            StepLayout stepLayout = (StepLayout) findViewById(R.id.rsb_current_step);
            if(stepLayout instanceof SignUpPermissionsStepLayout)
            {
                ((SignUpPermissionsStepLayout) stepLayout).onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
            }
        }
    }
}
