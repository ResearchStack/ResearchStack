package co.touchlab.researchstack.glue.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.result.TextQuestionResult;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.scene.Scene;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
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
            TaskResult result = (TaskResult) data.getSerializableExtra(
                    ViewTaskActivity.EXTRA_TASK_RESULT);

            boolean sharing = (boolean) result.getStepResult(ConsentTask.ID_SHARING).getResult();
            boolean consented = (boolean) result.getStepResult(ConsentTask.ID_CONSENT_DOC).getResult();

            if (ResearchStack.getInstance().getCurrentUser() == null)
            {
                ResearchStack.getInstance().loadUser();
            }

            User currentUser = ResearchStack.getInstance().getCurrentUser();

            // TODO check for valid signature/names
            if (consented)
            {
                TextQuestionResult formResult = (TextQuestionResult) result
                        .getStepResult(ConsentTask.ID_FORM_NAME).getResult();
                String fullName = formResult.getTextAnswer();
                String base64Image = (String) result.getStepResult(ConsentTask.ID_SIGNATURE).getResult();

                // TODO get signature date
                currentUser.setName(fullName);
                currentUser.setConsentSignatureName(fullName);
                currentUser.setConsentSignatureImage(base64Image);
                currentUser.setUserConsented(true);

                SceneImpl scene = (SceneImpl) findViewById(R.id.current_scene);
                if (scene != null && scene instanceof SignUpEligibleScene)
                {
                    // TODO this is weird, activity calling a callback method itself
                    onNextStep(scene.getStep(), null);
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
