package org.researchstack.backbone.storage.database;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.util.Date;
import java.util.List;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

@DatabaseTable
public class TaskRecord {
    public static final String TASK_ID = "taskId";
    public static final String COMPLETED = "completed";

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false, columnName = TASK_ID)
    public String taskId;

    @DatabaseField(canBeNull = false)
    public Date started;

    @DatabaseField(columnName = COMPLETED)
    public Date completed;

    @DatabaseField
    public Date uploaded;

    public static TaskResult toTaskResult(TaskRecord taskRecord, List<StepRecord> stepRecords) {
        TaskResult taskResult = new TaskResult(taskRecord.taskId);
        taskResult.setStartDate(taskRecord.started);
        taskResult.setEndDate(taskRecord.completed);

        for (StepRecord record : stepRecords) {
            StepResult result = StepRecord.toStepResult(record);
            taskResult.setStepResultForStepIdentifier(result.getIdentifier(), result);
        }
        return taskResult;
    }
}
