package org.researchstack.backbone.storage.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

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


    public static class SortByCompleted implements Comparator<TaskRecord> {
        public int compare(TaskRecord a, TaskRecord b) {
            if (a.completed == null && b.completed == null) {
                return 0; // Both are null, consider them equal
            }
            if (a.completed == null) {
                return -1; // a is null, so it should come before b
            }
            if (b.completed == null) {
                return 1; // b is null, so a should come before it
            }
            return a.completed.compareTo(b.completed); // Both are non-null, compare normally
        }
    }

}
