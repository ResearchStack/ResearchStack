package org.researchstack.backbone.storage.database.staged.records;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.model.staged.MedStagedActivity;
import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedActivityType;
import org.researchstack.backbone.model.staged.MedStagedSchedule;
import org.researchstack.backbone.utils.FormatHelper;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

/**
 * Created by mauriciosouto on 14/9/17.
 */

@DatabaseTable
public class MedStagedActivityRecord {

    public static final String STAGED_ACTIVITY_ID = "stagedActivityId";

    private static final Gson GSON = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601)
            .create();

    @DatabaseField(id = true, columnName = STAGED_ACTIVITY_ID)
    public String stagedActivityId;

    @DatabaseField(canBeNull = false)
    public MedStagedActivityType type;

    @DatabaseField
    public MedStagedActivityState status;

    @DatabaseField
    public String title;

    @DatabaseField
    public String text;

    @DatabaseField
    public String instructions;

    @DatabaseField
    public int tintColor;

    @DatabaseField
    public String schedule;

    public String getStagedActivityId() {
        return this.stagedActivityId;
    }

    public void setStagedActivityId(String stagedActivityId) {
        this.stagedActivityId = stagedActivityId;
    }

    public MedStagedActivityType getType() {
        return type;
    }

    public void setType(MedStagedActivityType type) {
        this.type = type;
    }

    public MedStagedActivityState getStatus() {
        return status;
    }

    public void setStatus(MedStagedActivityState status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getTintColor() {
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public static MedStagedActivity toMedStagedActivity(MedStagedActivityRecord record) {

        MedStagedActivity activity = null;
        if (record != null){
            activity = new MedStagedActivity();
            activity.setId(record.stagedActivityId);
            activity.setType(record.type);
            activity.setStatus(record.status);
            activity.setTitle(record.title);
            activity.setText(record.text);
            activity.setInstructions(record.instructions);
            activity.setTintColor(record.tintColor);
            activity.setSchedule(GSON.fromJson(record.schedule, MedStagedSchedule.class));
        }
        return activity;
    }

    public static MedStagedActivityRecord toRecord(MedStagedActivity activity) {
        MedStagedActivityRecord record = new MedStagedActivityRecord();
        record.stagedActivityId = activity.getId();
        record.type = activity.getType();
        record.status = activity.getStatus();
        record.title = activity.getTitle();
        record.text = activity.getText();
        record.instructions = activity.getInstructions();
        record.tintColor = activity.getTintColor();
        return record;
    }
}
