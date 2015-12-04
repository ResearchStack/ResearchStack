package co.touchlab.researchstack.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Constructor;
import java.util.Date;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.ResearchStackCoreApplication;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.storage.database.TaskRecord;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.scene.MultiSubSectionScene;
import co.touchlab.researchstack.core.ui.scene.NotImplementedScene;
import co.touchlab.researchstack.core.ui.scene.Scene;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.core.ui.scene.SceneAnimator;

public class ViewTaskActivity extends PassCodeActivity implements SceneCallbacks
{

    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";

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

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        initFileAccess();
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();
        loadNextScene();
    }

    @Override
    protected void onDataFailed()
    {
        super.onDataFailed();
        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
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
        newScene.getView().setId(R.id.current_scene);

        //If we are navigating back, we want to show the last sub-scene for the step.
        if (newScene instanceof MultiSubSectionScene && direction == SceneAnimator.SHIFT_RIGHT)
        {
            int lastSubScene = ((MultiSubSectionScene) newScene).getSceneCount();
            ((MultiSubSectionScene) newScene).showScene(lastSubScene - 1 , false);
        }

        if (oldScene != null)
        {
            oldScene.getView().setId(R.id.old_scene);
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
            if (step.getSceneTitle() <= 0)
            {
                LogExt.e(getClass(), "[" + step.getClass().getSimpleName() + ":"+step.getIdentifier()+"] No title set for step");
            }

            // Change the title on the activity
            String title = task.getTitleForStep(this, step);
            onChangeStepTitle(title);

            // Return the View
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor(Context.class, Step.class);
            Scene scene = (Scene) constructor.newInstance(this, step);
            scene.setCallbacks(this);

            return scene;
        }

        //TODO Throw RuntimeException here .. eventually .. when stable enough
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
            SceneImpl currentScene = (SceneImpl) findViewById(R.id.current_scene);
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
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);

        TaskRecord taskRecord = new TaskRecord();
        taskRecord.started = new Date();
        taskRecord.completed = new Date();
        taskRecord.taskId = task.getScheduleId();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        taskRecord.result = gson.toJson(taskResult);
        ResearchStackCoreApplication.getInstance().getAppDatabase().saveTaskRecord(taskRecord);

        setResult(RESULT_OK, resultIntent);
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
    public void onChangeStepTitle(String title)
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(title);
        }
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

}
