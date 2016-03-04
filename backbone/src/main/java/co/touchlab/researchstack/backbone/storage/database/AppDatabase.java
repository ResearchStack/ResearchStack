package co.touchlab.researchstack.backbone.storage.database;
import java.util.List;

import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.result.TaskResult;

public interface AppDatabase
{
    void saveTaskResult(TaskResult result);

    TaskResult loadLatestTaskResult(String taskId);

    List<TaskResult> loadTaskResults(String taskId);

    List<StepResult> loadStepResults(String stepId);

    void setEncryptionKey(String key);

    List<TaskNotification> loadTaskNotifications();

    void saveTaskNotification(TaskNotification notification);

    void deleteTaskNotification(int taskNotificationId);
}
