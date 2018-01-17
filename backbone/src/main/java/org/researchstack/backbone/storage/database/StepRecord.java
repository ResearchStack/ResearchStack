package org.researchstack.backbone.storage.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.TextUtils;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
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

    @DatabaseField(canBeNull = false)
    public String answerFormatClass;

    @DatabaseField
    public String answerFormat;

    @DatabaseField
    public String result;

    public static StepResult toStepResult(StepRecord record) {
        StepResult result = new StepResult(new Step(record.stepId));
        result.setStartDate(record.started);
        result.setEndDate(record.completed);

        AnswerFormat answerFormat = null;
        if (record.answerFormatClass != null) {
            try {
                answerFormat = (AnswerFormat) GSON.fromJson(record.answerFormat, Class.forName(record.answerFormatClass));
                result.setAnswerFormat(answerFormat);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(record.result)) {
            Map resultValues = GSON.fromJson(record.result, Map.class);
            for (Object resultKey : resultValues.keySet()) {
                if (resultValues.get(resultKey) instanceof List) {
                    Object[] value = ((List) resultValues.get(resultKey)).toArray();
                    resultValues.put(resultKey, value);
                }
            }
            result.setResults(resultValues);
        }
        return result;
    }
}
