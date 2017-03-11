package org.researchstack.backbone.model.taskitem;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.task.factory.TaskExcludeOption;
import org.researchstack.backbone.utils.OptionSetUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 3/7/17.
 */

public class ActiveTaskItem extends TaskItem {

    /**
     * localizedSteps are SurveyItems that contain fields to transfer to the ActiveTaskItem
     */
    @SerializedName("localizedSteps")
    private List<SurveyItem> localizedSteps;

    @SerializedName("removeSteps")
    private List<String> removeSteps;

    @SerializedName("predefinedExclusions")
    private int predefinedExclusions;

    @SerializedName("intendedUseDescription")
    private String intendedUseDescription;

    // Purposefully not de-serializing this object, we will be setting it in TaskItemAdapter
    public static final String GSON_TASK_OPTIONS_NAME = "taskOptions";
    @SerializedName("taskOptions")
    private Map<String, Object> taskOptions;

    public ActiveTaskItem() {
        super();
    }

    public Map<String, Object> getTaskOptions() {
        return taskOptions;
    }

    public void setTaskOptions(Map<String, Object> taskOptions) {
        this.taskOptions = taskOptions;
    }

    public List<TaskExcludeOption> createPredefinedExclusions() {
        if (predefinedExclusions == 0) {
            return new ArrayList<>();
        }
        return OptionSetUtils.toEnumList(predefinedExclusions, TaskExcludeOption.values());
    }

    /**
     * @param predefinedExclusions a bitmask representing a List<TaskExcludeOption>, see OptionSetUtils
     */
    public void setPredefinedExclusions(int predefinedExclusions) {
        this.predefinedExclusions = predefinedExclusions;
    }

    public List<SurveyItem> getLocalizedSteps() {
        return localizedSteps;
    }

    public void setLocalizedSteps(List<SurveyItem> localizedSteps) {
        this.localizedSteps = localizedSteps;
    }

    public List<String> getRemoveSteps() {
        return removeSteps;
    }

    public void setRemoveSteps(List<String> removeSteps) {
        this.removeSteps = removeSteps;
    }

    public String getIntendedUseDescription() {
        return intendedUseDescription;
    }

    public void setIntendedUseDescription(String intendedUseDescription) {
        this.intendedUseDescription = intendedUseDescription;
    }
}
