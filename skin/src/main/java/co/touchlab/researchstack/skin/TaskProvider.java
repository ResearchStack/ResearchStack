package co.touchlab.researchstack.skin;
import co.touchlab.researchstack.backbone.task.Task;

public abstract class TaskProvider
{
    public static final String TASK_ID_INITIAL = "TaskProvider.TASK_ID_INITIAL";
    public static final String TASK_ID_CONSENT = "TaskProvider.TASK_ID_CONSENT";
    public static final String TASK_ID_SIGN_IN = "TaskProvider.TASK_ID_SIGN_IN";
    public static final String TASK_ID_SIGN_UP = "TaskProvider.TASK_ID_SIGN_UP";

    private static TaskProvider instance;

    public static void init(TaskProvider manager)
    {
        TaskProvider.instance = manager;
    }

    public static TaskProvider getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public abstract Task get(String taskId);

    public abstract void put(String id, Task task);

}
