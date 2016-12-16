package org.researchstack.backbone.storage.database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.TextUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.touchlab.squeaky.field.DatabaseField;
import co.touchlab.squeaky.table.DatabaseTable;

@DatabaseTable
public class StepRecord
{
    private static final Gson GSON = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_RFC_822)
            .create();

    public static final String TASK_RECORD_ID = "taskRecordId";

    public static final String STEP_ID = "stepId";

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

    public static StepResult toStepResult(StepRecord record)
    {
        StepResult result = new StepResult(new Step(record.stepId));
        result.setStartDate(record.started);
        result.setEndDate(record.completed);
        if(! TextUtils.isEmpty(record.result))
        {
            Map<String, Object> mapresult = GSON.fromJson(record.result, Map.class);
            for(String varName : mapresult.keySet()) {
                if(mapresult.get(varName) instanceof Map) {
                    Map<String, Object> varValue = (Map) mapresult.get(varName);
                    if(varValue.containsKey("startDate") &&
                            varValue.containsKey("endDate") &&
                            varValue.containsKey("results") &&
                            varValue.containsKey("identifier")){
                        // it should be a StepResult!
                        StepResult reslt = fromMap(varValue);
                        //substitute the map with a StepResult
                        mapresult.put(varName, reslt);
                    }
                }
            }
            result.setResults(mapresult);
        }

        return result;
    }

    public static StepResult fromMap(Map<String, Object> map) {
        String id = (String) map.get("identifier");
        StepResult res = new StepResult(new Step(id));
        String sdString = (String) map.get("startDate");
        String edString = (String) map.get("endDate");
        try {
            Date sd = FormatHelper.DEFAULT_FORMAT.parse(sdString);
            res.setStartDate(sd);
        } catch (ParseException e) {
            LogExt.e(StepRecord.class, "Cannot parse start date from Map " + sdString);
        }
        try {
            Date ed = FormatHelper.DEFAULT_FORMAT.parse(edString);
            res.setStartDate(ed);
        } catch (ParseException e) {
            LogExt.e(StepRecord.class, "Cannot parse end date from Map " + edString);
        }
        Map results = (Map) map.get("results");
        res.setResults(results);

        return res;
    }
}
