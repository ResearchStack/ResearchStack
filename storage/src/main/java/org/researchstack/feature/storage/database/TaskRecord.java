package org.researchstack.feature.storage.database;

import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.result.TaskResult;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

import static org.threeten.bp.DateTimeUtils.toInstant;

@DatabaseTable
public class TaskRecord {
    public static final String TASK_ID = "taskId";
    public static final String COMPLETED = "completed";

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false, columnName = TASK_ID)
    public String taskId;

    @DatabaseField(canBeNull = false)
    public String taskRunUUID;

    @DatabaseField(canBeNull = false)
    public Date started;

    @DatabaseField(columnName = COMPLETED)
    public Date completed;

    @DatabaseField
    public Date uploaded;

    public static TaskResult toTaskResult(TaskRecord taskRecord, List<StepRecord> stepRecords) {
        TaskResult taskResult = new TaskResult(taskRecord.taskId,
                UUID.fromString(taskRecord.taskRunUUID));

        taskResult.setStartTimestamp(toInstant(taskRecord.started));
        taskResult.setEndTimestamp(toInstant(taskRecord.completed));

        for (StepRecord record : stepRecords) {
            StepResult result = StepRecord.toStepResult(record);
            taskResult.setStepResultForStepIdentifier(result.getIdentifier(), result);
        }
        return taskResult;
    }
}
