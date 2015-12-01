package co.touchlab.researchstack.core.storage.database;
import java.util.List;
import java.util.Map;

/**
 * Created by kgalligan on 11/28/15.
 */
public interface AppDatabase
{
    void saveTaskRecord(TaskRecord taskRecord);

    List<TaskRecord> findTaskRecordById(String taskId);

    Map<String, TaskRecord> findLatestForAllTypes();
}
