package org.researchstack.backbone.storage.database;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.util.List;

public interface AppDatabase
{
    void saveTaskResult(TaskResult result);

    TaskResult loadLatestTaskResult(String taskId);

    List<TaskResult> loadTaskResults(String taskId);

    List<StepResult> loadStepResults(String stepId);

    void setEncryptionKey(String key);

}
