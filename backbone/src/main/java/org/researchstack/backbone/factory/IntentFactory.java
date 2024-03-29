package org.researchstack.backbone.factory;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;

/**
 * This class encapsulates creating Intent instances. This is needed to enable unit tests, since
 * Intents in the unit test environment will always throw.
 */
public class IntentFactory {
    /** Singleton instance. */
    public static final IntentFactory INSTANCE = new IntentFactory();

    /**
     * Private constructor, to enforce the singleton property. This prevents creating additional
     * instances, but the factory can still be mocked.
     */
    private IntentFactory() {
    }

    /**
     * Creates an Intent for a task activity.
     *
     * @param context activity context
     * @param clazz   activity class
     * @param task    task activity
     * @return an Intent for this task activity
     */
    @NonNull
    public Intent newTaskIntent(Context context, Class<? extends ViewTaskActivity> clazz, Task task) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(ViewTaskActivity.EXTRA_TASK, task);
        return intent;
    }

    /**
     * Creates an Intent for a task activity with an existing task result
     *
     * @param context activity context
     * @param clazz   activity class
     * @param task    task activity
     * @param result  existing task result
     * @return an Intent for this task activity
     */
    @NonNull
    public Intent newTaskIntent(Context context, Class<? extends ViewTaskActivity> clazz, Task task, TaskResult result) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(ViewTaskActivity.EXTRA_TASK, task);
        if (result != null) {
            intent.putExtra(ViewTaskActivity.EXTRA_TASK_RESULT, result);
        }
        return intent;
    }

    /**
     * Creates an Intent for a task activity with an existing task result
     *
     * @param context activity context
     * @param clazz   activity class
     * @param task    task activity
     * @param result  existing task result
     * @param step    the step the task should start on
     * @return an Intent for this task activity
     */
    @NonNull
    public Intent newTaskIntent(Context context,
                                Class<? extends ViewTaskActivity> clazz,
                                Task task, TaskResult result, Step step) {

        Intent intent = new Intent(context, clazz);
        intent.putExtra(ViewTaskActivity.EXTRA_TASK, task);
        if (result != null) {
            intent.putExtra(ViewTaskActivity.EXTRA_TASK_RESULT, result);
        }
        if (step != null) {
            intent.putExtra(ViewTaskActivity.EXTRA_STEP, step);
        }
        return intent;
    }
}
