package org.researchstack.backbone.storage.database;

import java.io.Serializable;
import java.util.Date;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

@DatabaseTable
public class TaskNotification implements Serializable {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public Date endDate;

    @DatabaseField
    public String chronTime;
}
