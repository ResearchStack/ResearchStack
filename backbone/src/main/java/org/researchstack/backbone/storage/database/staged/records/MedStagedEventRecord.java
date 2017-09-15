package org.researchstack.backbone.storage.database.staged.records;

import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedEvent;

import java.util.Date;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by mauriciosouto on 14/9/17.
 */

@DatabaseTable
public class MedStagedEventRecord {

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

    @DatabaseField
    public String taskId;

    public static MedStagedEvent toMedStagedEvent(MedStagedEventRecord record) {
        MedStagedEvent medStagedEvent = new MedStagedEvent();
        medStagedEvent.setActivity(record.stagedActivityId);
        medStagedEvent.setEventStartDate(record.eventStartDate);
        medStagedEvent.setEventEndDate(record.eventEndDate);
        medStagedEvent.setStatus(record.status);

        return medStagedEvent;
    }

    public static MedStagedEventRecord toRecord(MedStagedEvent event) {
        MedStagedEventRecord record = new MedStagedEventRecord();
        record.stagedActivityId = event.getActivity();
        record.eventStartDate = event.getEventStartDate();
        record.eventEndDate = event.getEventEndDate();
        record.status = event.getStatus();
        if (event.getResult() != null) {
            record.taskId = event.getResult().getIdentifier();
        }
        return record;
    }


}
