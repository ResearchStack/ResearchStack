package co.touchlab.researchstack.core.storage.database;
import java.util.Date;
import java.util.List;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

@DatabaseTable
public class TaskRecord
{
    public static final String TASK_RESULT_ID = "taskResultId";

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false, columnName = TASK_RESULT_ID)
    public String taskResultId;

    @DatabaseField(canBeNull = false)
    public Date started;

    @DatabaseField
    public Date completed;

    @DatabaseField
    public Date uploaded;

    public static TaskResult toTaskResult(TaskRecord taskRecord, List<StepRecord> stepRecords)
    {
        TaskResult taskResult = new TaskResult(taskRecord.taskResultId);
        taskResult.setStartDate(taskRecord.started);
        taskResult.setEndDate(taskRecord.completed);

        for(StepRecord record : stepRecords)
        {
            StepResult result = StepRecord.toStepResult(record);
            taskResult.setStepResultForStepIdentifier(result.getIdentifier(), result);
        }
        return taskResult;
    }
}
