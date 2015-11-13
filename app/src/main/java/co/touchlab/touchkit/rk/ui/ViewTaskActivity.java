package co.touchlab.touchkit.rk.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import co.touchlab.touchkit.rk.common.model.ConsentSignature;
import co.touchlab.touchkit.rk.common.model.User;
import co.touchlab.touchkit.rk.common.result.ConsentSignatureResult;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.common.task.ConsentTask;
import co.touchlab.touchkit.rk.common.task.SignUpTask;
import co.touchlab.touchkit.rk.common.task.Task;
import co.touchlab.touchkit.rk.ui.callbacks.ActivityCallback;
import co.touchlab.touchkit.rk.ui.callbacks.StepCallbacks;
import co.touchlab.touchkit.rk.ui.scene.MultiStateScene;
import co.touchlab.touchkit.rk.ui.scene.NotImplementedScene;
import co.touchlab.touchkit.rk.ui.scene.Scene;
import co.touchlab.touchkit.rk.ui.scene.SceneAnimator;
import co.touchlab.touchkit.rk.ui.scene.SignInScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpAdditionalInfoScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpEligibleScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpGeneralInfoScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpInclusionCriteriaScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpIneligibleScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpPasscodeScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpPermissionsPrimingScene;
import co.touchlab.touchkit.rk.ui.scene.SignUpPermissionsScene;

public class ViewTaskActivity extends AppCompatActivity implements StepCallbacks, ActivityCallback
{

    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final int REQUEST_CODE = 100;

    private SceneAnimator animator;

    private Step currentStep;
    private Task task;
    private TaskResult taskResult;

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context,
                ViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_fragment);
        super.setResult(RESULT_CANCELED);

        ViewGroup root = (ViewGroup) findViewById(R.id.container);
        animator = new SceneAnimator(root);

        task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
        taskResult = new TaskResult(task.getIdentifier(), null, null);

        loadNextScene();

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
    }

    private void loadNextScene()
    {
        Scene scene = (Scene) findViewById(R.id.current_scene);

        if (scene instanceof MultiStateScene)
        {
            MultiStateScene multiStateScene = (MultiStateScene) scene;
            int current = multiStateScene.getCurrentPosition();
            int count = multiStateScene.getSceneCount();
            if (current < count - 1)
            {
                multiStateScene.loadNextScene();
                return;
            }
        }

        Step nextStep = task.getStepAfterStep(currentStep, taskResult);
        if(nextStep == null)
        {
            saveAndFinish();
        }
        else
        {
            showScene(nextStep, SceneAnimator.SHIFT_LEFT);
        }
    }

    private void loadPreviousScene()
    {
        Scene scene = (Scene) findViewById(R.id.current_scene);

        if (scene instanceof MultiStateScene)
        {
            MultiStateScene multiStateScene = (MultiStateScene) scene;
            int current = multiStateScene.getCurrentPosition();
            if (current > 0)
            {
                multiStateScene.loadPreviousScene();
                return;
            }
        }

        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if(previousStep == null)
        {
            onBackPressed();
        }
        else
        {
            showScene(previousStep, SceneAnimator.SHIFT_RIGHT);
        }
    }

    private void showScene(Step step, int direction)
    {
        Scene oldScene = (Scene) findViewById(R.id.current_scene);

        Scene newScene = getSceneForStep(step);
        newScene.setId(R.id.current_scene);

        if (oldScene != null)
        {
            oldScene.setId(R.id.old_scene);
            animator.animate(oldScene, newScene, direction);
        }
        else
        {
            animator.show(oldScene, newScene);
        }

        currentStep = step;
    }

    protected Scene getSceneForStep(Step step)
    {
        if(step == null)
        {
            return new NotImplementedScene(this, new Step("NullStep"));
        }

        LogExt.d(getClass(), "getSceneForStep() - " + step);

        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor(Context.class, Step.class);
            Scene scene = (Scene) constructor.newInstance(this, step);
            scene.setCallbacks(this);
            return scene;
        }
        catch(Exception e)
        {
            LogExt.e(getClass(), e);
        }

        if (step.getIdentifier()
                .equals(SignUpTask.SignUpInclusionCriteriaStepIdentifier))
        {
            return new SignUpInclusionCriteriaScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpIneligibleStepIdentifier))
        {
            return new SignUpIneligibleScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpEligibleStepIdentifier))
        {
            return new SignUpEligibleScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpPermissionsPrimingStepIdentifier))
        {
            return new SignUpPermissionsPrimingScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpGeneralInfoStepIdentifier))
        {
            return new SignUpGeneralInfoScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpMedicalInfoStepIdentifier))
        {
            return new SignUpAdditionalInfoScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpPasscodeStepIdentifier))
        {
            return new SignUpPasscodeScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignUpPermissionsStepIdentifier))
        {
            return new SignUpPermissionsScene(this, step);
        }
        else if (step.getIdentifier()
                .equals(SignUpTask.SignInStepIdentifier))
        {
            return new SignInScene(this, step);
        }
        else
        {
            LogExt.d(getClass(), "No implementation for this step " + step.getIdentifier());
            return new NotImplementedScene(this, step);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            loadPreviousScene();
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    private void saveAndFinish()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT,
                taskResult);
        setResult(RESULT_OK,
                resultIntent);
        finish();
    }

    @Override
    public void onNextPressed(Step step)
    {
        loadNextScene();
    }

    @Override
    public void onStepResultChanged(Step step, StepResult result)
    {
        taskResult.setStepResultForStepIdentifier(step.getIdentifier(), result);
    }

    @Override
    public void onSkipStep(Step step)
    {
        onStepResultChanged(step, null);
        onNextPressed(step);
    }

    @Override
    public void onCancelStep()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public StepResult getResultStep(String stepId)
    {
        return taskResult.getStepResultForStepIdentifier(stepId);
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

            boolean sharing = ((QuestionResult<Boolean>) result.getStepResultForStepIdentifier("sharing")
                    .getResultForIdentifier("sharing")).getAnswer();

            ConsentSignatureResult signatureResult = ((ConsentSignatureResult) result.getStepResultForStepIdentifier("reviewStep"));
            ConsentSignature signature = signatureResult.getSignature();
            boolean consented = signatureResult.isConsented();

            if (AppDelegate.getInstance().getCurrentUser() == null)
            {
                AppDelegate.getInstance().loadUser(this);
            }

            User currentUser = AppDelegate.getInstance()
                    .getCurrentUser();

            // TODO check for valid signature/names
            if (consented)
            {
                // TODO just use full name to begin with and don't concat names like this
                // TODO get signature date
                String fullName = signature.getGivenName() + " " + signature.getFamilyName();
                currentUser.setName(fullName);
                currentUser.setConsentSignatureName(fullName);
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
