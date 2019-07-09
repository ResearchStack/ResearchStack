package org.researchstack.skin;

import android.app.Application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.researchstack.foundation.components.presentation.ITaskProvider;
import org.researchstack.foundation.core.interfaces.ITask;
import org.researchstack.foundation.core.models.task.Task;

/**
 * TaskProvider is used as a way for the Framework to get Tasks needed throughout the onboarding
 * process. This allows you to implement your own Tasks if needed.
 */
public abstract class TaskProvider implements ITaskProvider {
    /**
     * Task ID used by the framework for looking up the "initial" task
     */
    public static final String TASK_ID_INITIAL = "TaskProvider.TASK_ID_INITIAL";

    /**
     * Task ID used by the framework for looking up the consent task
     */
    public static final String TASK_ID_CONSENT = "TaskProvider.TASK_ID_CONSENT";


    /**
     * Task ID used by the framework for looking up the sign-in task
     */
    public static final String TASK_ID_SIGN_IN = "TaskProvider.TASK_ID_SIGN_IN";

    /**
     * Task ID used by the framework for looking up the sign-up task
     */
    public static final String TASK_ID_SIGN_UP = "TaskProvider.TASK_ID_SIGN_UP";

    private static TaskProvider instance;

    /**
     * Initializes the TaskProvider singleton. It is best to call this method inside your {@link
     * Application#onCreate()} method.
     *
     * @param manager an implementation of ResourcePathManager
     */
    public static void init(TaskProvider manager) {
        TaskProvider.instance = manager;
    }

    /**
     * Returns a singleton static instance of the this class
     *
     * @return A singleton static instance of the this class
     */
    public static TaskProvider getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "TaskProvider instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * Used, in combination of {@link #put(String, Task)}, for task lookup and resuse
     *
     * @param taskId the task id
     * @return a task object with an id of {@param taskId}
     */
    public abstract Task get(String taskId);

    @Nullable
    @Override
    public ITask task(@NotNull String identifier) {
        return this.get(identifier);
    }

    /**
     * Used to store a task object for reuse
     *
     * @param id   the task id
     * @param task the task object
     */
    public abstract void put(String id, Task task);

}
