package co.touchlab.researchstack.glue.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Date;

import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.step.layout.ConsentSignatureStepLayout;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.glue.DataProvider;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.task.ConsentTask;
import co.touchlab.researchstack.glue.task.OnboardingTask;
import co.touchlab.researchstack.glue.ui.scene.SignUpEligibleStepLayout;
import co.touchlab.researchstack.glue.ui.scene.SignUpPermissionsStepLayout;

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
        if (step.getIdentifier().equals(OnboardingTask.SignUpPassCodeConfirmationStepIdentifier))
        {
            String pin = (String) result.getResult();
            ((AuthDataAccess) StorageAccess.getInstance()).setPinCode(this, pin);

            saveConsentResultInfo();
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
        ConsentTask task = new ConsentTask(this);
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, SignUpEligibleStepLayout.CONSENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == SignUpEligibleStepLayout.CONSENT_REQUEST)
        {
            // User has passed through the entire consent flow
            if (resultCode == Activity.RESULT_OK)
            {
                consentResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

                // (If we aren't using an AuthFileAccess) OR (if we are, and we have a pincode)
                // THEN SaveConsentResultInfo. If we don't have a pincode then pincode-creation
                // steps will appear when we call "showNextStep"

                //TODO This is now misleading.
                boolean doesNotHaveAuth = ! (StorageAccess.getInstance() instanceof AuthDataAccess);
                boolean hasPinCode = StorageAccess.getInstance().hasPinCode(this);

                if (doesNotHaveAuth || hasPinCode)
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
        StepResult<StepResult> formResult = (StepResult<StepResult>) consentResult.getStepResult(
                ConsentTask.ID_FORM);

        String fullName = (String) formResult.getResultForIdentifier(ConsentTask.ID_FORM_NAME)
                .getResult();

        Long birthdateInMillis = (Long) formResult.getResultForIdentifier(ConsentTask.ID_FORM_DOB)
                .getResult();

        String sharingScope = (String) consentResult.getStepResult(ConsentTask.ID_SHARING)
                .getResult();

        String base64Image = (String) consentResult.getStepResult(ConsentTask.ID_SIGNATURE)
                .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

        String signatureDate = (String) consentResult.getStepResult(ConsentTask.ID_SIGNATURE)
                .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

        // Save Consent Information
        DataProvider.getInstance()
                .saveConsent(this,
                        fullName,
                        new Date(birthdateInMillis),
                        base64Image,
                        signatureDate,
                        sharingScope);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == SignUpPermissionsStepLayout.LOCATION_PERMISSION_REQUEST_CODE)
        {
            StepLayout stepLayout = (StepLayout) findViewById(R.id.rsc_current_step);
            if(stepLayout instanceof SignUpPermissionsStepLayout)
            {
                ((SignUpPermissionsStepLayout) stepLayout).onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
            }
        }
    }
}
