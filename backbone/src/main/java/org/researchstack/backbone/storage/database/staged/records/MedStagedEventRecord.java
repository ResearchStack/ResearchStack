package org.researchstack.backbone.storage.database.staged.records;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedEvent;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.TextUtils;

import java.util.Date;
import java.util.Map;

import co.touchlab.squeaky.field.DataType;
import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by mauriciosouto on 14/9/17.
 */

@DatabaseTable
public class MedStagedEventRecord {

    private static final Gson GSON = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601).create();

    public static final String ACTIVITY_ID_COLUMN = "stagedActivityId";
    public static final String STATUS_COLUMN = "stagedActivityId";

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String stagedActivityId;

    @DatabaseField
    public Date eventStartDate;

    @DatabaseField(canBeNull = false)
    public Date eventEndDate;

    @DatabaseField
    public MedStagedActivityState status;

    @DatabaseField(canBeNull = true)
    public String taskId;

    @DatabaseField(dataType = DataType.SERIALIZABLE, canBeNull = true)
    public Object task;

    @DatabaseField(canBeNull = true)
    public String taskResultId;

    public static MedStagedEvent toMedStagedEvent(MedStagedEventRecord record, TaskResult result) {
        MedStagedEvent medStagedEvent = new MedStagedEvent();
        medStagedEvent.setId(record.id);
        medStagedEvent.setActivityId(record.stagedActivityId);
        medStagedEvent.setEventStartDate(record.eventStartDate);
        medStagedEvent.setEventEndDate(record.eventEndDate);
        medStagedEvent.setTask((Task) record.task);
        medStagedEvent.addResult(result, record.status);
        return medStagedEvent;
    }

    public static MedStagedEventRecord toRecord(MedStagedEvent event) {
        MedStagedEventRecord record = new MedStagedEventRecord();
        record.id = event.getId();
        record.stagedActivityId = event.getActivityId();
        record.eventStartDate = event.getEventStartDate();
        record.eventEndDate = event.getEventEndDate();
        record.status = event.getStatus();
        if (event.getTask() != null) {
            record.task = event.getTask();
            record.taskId = event.getTask().getIdentifier();
        }
        if (event.getResult() != null) {
            record.taskResultId = event.getResult().getIdentifier();
        }
        return record;
    }


}
