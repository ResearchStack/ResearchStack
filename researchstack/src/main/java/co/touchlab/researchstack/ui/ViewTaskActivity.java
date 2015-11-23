package co.touchlab.researchstack.ui;

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

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.model.ConsentSignature;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.result.ConsentSignatureResult;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.Step;
import co.touchlab.researchstack.common.task.ConsentTask;
import co.touchlab.researchstack.common.task.Task;
import co.touchlab.researchstack.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.ui.callbacks.StepCallbacks;
import co.touchlab.researchstack.ui.scene.MultiSubSectionScene;
import co.touchlab.researchstack.ui.scene.NotImplementedScene;
import co.touchlab.researchstack.ui.scene.Scene;
import co.touchlab.researchstack.ui.scene.SceneAnimator;
import co.touchlab.researchstack.ui.scene.SignUpEligibleScene;
import co.touchlab.researchstack.ui.scene.SignUpPermissionsScene;

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

        //If we are navigating back, we want to show the last sub-scene for the step.
        if (newScene instanceof MultiSubSectionScene && direction == SceneAnimator.SHIFT_RIGHT)
        {
            int lastSubScene = ((MultiSubSectionScene) newScene).getSceneCount();
            ((MultiSubSectionScene) newScene).showScene(lastSubScene - 1 , false);
        }

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

        LogExt.d(getClass(), "No implementation for this step " + step.getIdentifier());
        return new NotImplementedScene(this, step == null ? new Step("NullStep") : step);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Scene currentScene = (Scene) findViewById(R.id.current_scene);
            if (!currentScene.isBackEventConsumed())
            {
                loadPreviousScene();
            }
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

            if (ResearchStackApplication.getInstance().getCurrentUser() == null)
            {
                ResearchStackApplication.getInstance().loadUser(this);
            }

            User currentUser = ResearchStackApplication.getInstance()
                    .getCurrentUser();

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
