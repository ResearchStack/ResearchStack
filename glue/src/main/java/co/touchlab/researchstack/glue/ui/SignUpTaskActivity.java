package co.touchlab.researchstack.glue.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.ConsentSignatureResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.scene.Scene;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStackApplication;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.glue.task.ConsentTask;
import co.touchlab.researchstack.glue.ui.scene.SignUpEligibleScene;
import co.touchlab.researchstack.glue.ui.scene.SignUpPermissionsScene;

public class SignUpTaskActivity extends ViewTaskActivity implements ActivityCallback
{

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context,
                SignUpTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void requestPermissions()
    {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                           SignUpPermissionsScene.LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void startConsentTask()
    {
        ConsentTask task = new ConsentTask(this);
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, SignUpEligibleScene.CONSENT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SignUpEligibleScene.CONSENT_REQUEST && resultCode == Activity.RESULT_OK)
        {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            StepResult<Boolean> sharingResult = result.getStepResultForStepIdentifier("sharing");
            boolean sharing = sharingResult.getResultForIdentifier(StepResult.DEFAULT_KEY);

            ConsentSignatureResult signatureResult = ((ConsentSignatureResult) result.getStepResultForStepIdentifier("reviewStep"));
            ConsentSignature signature = signatureResult.getSignature();
            boolean consented = signatureResult.isConsented();

            if (ResearchStackApplication.getInstance().getCurrentUser() == null)
            {
                ResearchStackApplication.getInstance().loadUser();
            }

            User currentUser = ResearchStackApplication.getInstance().getCurrentUser();

            // TODO check for valid signature/names
            if (consented)
            {
                // TODO just use full name to begin with and don't concat names like this
                // TODO get signature date
                currentUser.setName(signature.getFullName());
                currentUser.setConsentSignatureName(signature.getFullName());
                currentUser.setConsentSignatureImage(signature.getSignatureImage());
                currentUser.setUserConsented(true);

                Scene scene = (Scene) findViewById(R.id.current_scene);
                if (scene != null && scene instanceof SignUpEligibleScene)
                {
                    onNextPressed(scene.getStep());
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

        if (requestCode ==  SignUpPermissionsScene.LOCATION_PERMISSION_REQUEST_CODE)
        {
            Scene scene = (Scene) findViewById(R.id.current_scene);
            if(scene instanceof SignUpPermissionsScene)
            {
                ((SignUpPermissionsScene) scene)
                        .onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
