package org.researchstack.backbone.storage.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;


@DatabaseTable
public class TaskNotification implements Serializable {
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public Date endDate;

    @DatabaseField
    public String chronTime;
}
