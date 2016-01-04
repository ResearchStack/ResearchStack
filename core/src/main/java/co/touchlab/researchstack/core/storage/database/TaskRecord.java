package co.touchlab.researchstack.core.storage.database;
import java.util.Date;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by kgalligan on 11/27/15.
 */
@DatabaseTable public class TaskRecord
{
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false)
    public String taskId;

    @DatabaseField(canBeNull = false)
    public Date started;

    @DatabaseField
    public Date completed;

    @DatabaseField
    public String result;

    @DatabaseField
    public Date uploaded;
}
