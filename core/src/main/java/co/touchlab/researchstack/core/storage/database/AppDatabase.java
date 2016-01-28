package co.touchlab.researchstack.core.storage.database;
import java.util.List;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;

public interface AppDatabase
{
    void saveTaskResult(TaskResult result);

    TaskResult loadLatestTaskResult(String taskId);

    List<TaskResult> loadTaskResults(String taskId);

    List<StepResult> loadStepResults(String stepId);

}
