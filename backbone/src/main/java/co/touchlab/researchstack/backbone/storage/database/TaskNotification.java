package co.touchlab.researchstack.backbone.storage.database;

import java.io.Serializable;
import java.util.Date;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

@DatabaseTable
public class TaskNotification implements Serializable
{
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false, columnName = TaskRecord.TASK_ID)
    public String taskId;

    @DatabaseField
    public Date endDate;

    @DatabaseField
    public String chronoTime;
}
