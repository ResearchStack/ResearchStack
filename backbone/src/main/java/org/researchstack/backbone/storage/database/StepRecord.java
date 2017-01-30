package org.researchstack.backbone.storage.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.TextUtils;

import java.util.Date;
import java.util.Map;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

@DatabaseTable
public class StepRecord {
    public static final String TASK_RECORD_ID = "taskRecordId";
    public static final String STEP_ID = "stepId";
    private static final Gson GSON = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601)
            .create();
    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false, columnName = StepRecord.TASK_RECORD_ID)
    public int taskRecordId;

    @DatabaseField(canBeNull = false, columnName = TaskRecord.TASK_ID)
    public String taskId;

    @DatabaseField(canBeNull = false, columnName = StepRecord.STEP_ID)
    public String stepId;

    @DatabaseField
    public Date started;

    @DatabaseField
    public Date completed;

    @DatabaseField
    public String result;

    public static StepResult toStepResult(StepRecord record) {
        StepResult result = new StepResult(new Step(record.stepId));
        result.setStartDate(record.started);
        result.setEndDate(record.completed);
        if (!TextUtils.isEmpty(record.result)) {
            result.setResults(GSON.fromJson(record.result, Map.class));
        }

        return result;
    }
}
