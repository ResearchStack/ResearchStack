package co.touchlab.researchstack.glue.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.step.layout.ConsentSignatureStepLayout;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;
import co.touchlab.researchstack.core.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.glue.task.ConsentTask;
import co.touchlab.researchstack.glue.ui.scene.SignUpEligibleStepLayout;
import co.touchlab.researchstack.glue.ui.scene.SignUpPermissionsStepLayout;

public class SignUpTaskActivity extends ViewTaskActivity implements ActivityCallback
{

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context, SignUpTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
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
        if(requestCode == SignUpEligibleStepLayout.CONSENT_REQUEST &&
                resultCode == Activity.RESULT_OK)
        {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            boolean consented = (boolean) result.getStepResult(ConsentTask.ID_CONSENT_DOC)
                    .getResult();

            if(ResearchStack.getInstance().getCurrentUser() == null)
            {
                ResearchStack.getInstance().loadUser();
            }

            User currentUser = ResearchStack.getInstance().getCurrentUser();

            // TODO check for valid signature/names
            if(consented)
            {
                StepResult<StepResult<String>> formResult = (StepResult<StepResult<String>>) result.getStepResult(
                        ConsentTask.ID_FORM);
                String fullName = formResult.getResultForIdentifier(ConsentTask.ID_FORM_NAME)
                        .getResult();
                String birthDate = formResult.getResultForIdentifier(ConsentTask.ID_FORM_DOB)
                        .getResult();
                String base64Image = (String) result.getStepResult(ConsentTask.ID_SIGNATURE)
                        .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);
                String signatureDate = (String) result.getStepResult(ConsentTask.ID_SIGNATURE)
                        .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);
                boolean sharing = (boolean) result.getStepResult(ConsentTask.ID_SHARING).getResult();

                currentUser.setName(fullName);
                currentUser.setConsentSignatureName(fullName);
                currentUser.setConsentSignatureDate(signatureDate);
                currentUser.setConsentSignatureBirthDate(birthDate);
                currentUser.setConsentSignatureImage(base64Image);
                currentUser.setUserConsented(true);

                StepLayoutImpl scene = (StepLayoutImpl) findViewById(R.id.rsc_current_scene);
                if(scene != null && scene instanceof SignUpEligibleStepLayout)
                {
                    // TODO this is weird, activity calling a callback method itself
                    onSaveStep(ACTION_NEXT, scene.getStep(), null);
                }
            }
            else
            {
                // Clear activity and show Welcome screen
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == SignUpPermissionsStepLayout.LOCATION_PERMISSION_REQUEST_CODE)
        {
            StepLayout stepLayout = (StepLayout) findViewById(R.id.rsc_current_scene);
            if(stepLayout instanceof SignUpPermissionsStepLayout)
            {
                ((SignUpPermissionsStepLayout) stepLayout).onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
            }
        }
    }
}
