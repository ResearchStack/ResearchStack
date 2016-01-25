package co.touchlab.researchstack.core.storage.database;
import java.util.List;
import java.util.Map;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;

/**
 * Created by kgalligan on 11/28/15.
 */
public interface AppDatabase
{
    @Deprecated
    void saveTaskRecord(TaskRecord taskRecord);

    @Deprecated
    List<TaskRecord> findTaskRecordById(String taskId);

    @Deprecated
    Map<String, TaskRecord> findLatestForAllTypes();

    void saveTaskResult(TaskResult result);

    TaskResult loadTaskResult(String taskResultId);

    List<TaskResult> loadTaskResults(String taskId);

    List<StepResult> loadStepResults(String stepId);

}
