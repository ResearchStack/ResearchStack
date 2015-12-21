package co.touchlab.researchstack.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Constructor;
import java.util.Date;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.storage.database.TaskRecord;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.scene.Scene;
import co.touchlab.researchstack.core.ui.views.SceneSwitcher;

public class ViewTaskActivity extends PassCodeActivity implements SceneCallbacks
{
    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";

    private SceneSwitcher root;

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
        super.setResult(RESULT_CANCELED);
        super.setContentView(R.layout.activity_scene_switcher);

        root = (SceneSwitcher) findViewById(R.id.container);

        if (savedInstanceState == null)
        {
            task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
            taskResult = new TaskResult(task.getIdentifier(), null, null);
        }
        else
        {
            task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initFileAccess();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TASK, task);
        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
        outState.putSerializable(EXTRA_STEP, currentStep);
    }

    private void showNextStep()
    {
        Step nextStep = task.getStepAfterStep(currentStep, taskResult);
        if(nextStep == null)
        {
            saveAndFinish();
        }
        else
        {
            showStep(nextStep);
        }
    }

    private void showPreviousStep()
    {
        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if(previousStep == null)
        {
            finish();
        }
        else
        {
            showStep(previousStep);
        }
    }

    private void showStep(Step step)
    {
        int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult).getCurrent();
        int newStepPosition =  task.getProgressOfCurrentStep(step, taskResult).getCurrent();

        Scene scene = getSceneForStep(step);
        root.show(scene, newStepPosition >= currentStepPosition ? SceneSwitcher.SHIFT_LEFT :
                SceneSwitcher.SHIFT_RIGHT);
        currentStep = step;
    }

    protected Scene getSceneForStep(Step step)
    {
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        onStepTitleChanged(title);

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        // Return the Class & constructor
        Scene scene = createSceneFromStep(step);
        scene.initialize(step, result);
        scene.setCallbacks(this);

        return scene;
    }

    @NonNull
    private Scene createSceneFromStep(Step step)
    {
        try
        {
            Class cls = step.getSceneClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (Scene) constructor.newInstance(this);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void saveAndFinish()
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);

        TaskRecord taskRecord = new TaskRecord();
        taskRecord.started = new Date();
        taskRecord.completed = new Date();
        taskRecord.taskId = task.getScheduleId();
        Gson gson = new GsonBuilder().setDateFormat(StorageManager.DATE_FORMAT_ISO_8601).create();
        taskRecord.result = gson.toJson(taskResult);
        StorageManager.getAppDatabase().saveTaskRecord(taskRecord);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            notifySceneOfBackPress();
            return true;
        }
        else if (item.getItemId() == R.id.menu_cancel)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        notifySceneOfBackPress();
    }

    private void notifySceneOfBackPress()
    {
        Scene currentScene = (Scene) findViewById(R.id.current_scene);
        currentScene.isBackEventConsumed();
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();

        if (currentStep == null)
        {
            currentStep = task.getStepAfterStep(null, taskResult);
        }

        showStep(currentStep);
    }

    @Override
    protected void onDataFailed()
    {
        super.onDataFailed();
        Toast.makeText(this, "Whoops", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result)
    {
        taskResult.setStepResultForStepIdentifier(step.getIdentifier(), result);

        if (action == SceneCallbacks.ACTION_NEXT)
        {
            showNextStep();
        }
        else if (action == SceneCallbacks.ACTION_PREV)
        {
            showPreviousStep();
        }
        else if (action == SceneCallbacks.ACTION_NONE)
        {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        }
        else
        {
            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
                                               "See SceneCallbacks for allowable arguments");
        }
    }

    @Override
    public void onStepTitleChanged(String title)
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
}
