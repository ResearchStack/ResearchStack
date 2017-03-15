package org.researchstack.backbone.model.taskitem;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.task.factory.TaskExcludeOption;
import org.researchstack.backbone.utils.OptionSetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 3/7/17.
 *
 * An ActiveTaskItem represents a TaskItem that will be used to build a specific ResearchStack
 * task like the ones defined in TaskItemType.  Currently, these include the Tremor Task,
 * Tapping Task, Audio Task, and several Walking Tasks. The list can and will continue to grow.
 */

public class ActiveTaskItem extends TaskItem {

    /**
     * localizedSteps are SurveyItem steps that contain fields to transfer to their
     * corresponding steps after the Task is built
     */
    @SerializedName("localizedSteps")
    private List<SurveyItem> localizedSteps;

    /**
     * This list of String identifiers can be used to remove steps with these identifiers
     * from the task after the Task is built
     */
    @SerializedName("removeSteps")
    private List<String> removeSteps;

    /**
     * This is an OptionSet in iOS, but in Android we must treat it as a bit-masked int
     * See TaskExcludeOption class for bit values which are converted by OptionSetUtils class
     */
    @SerializedName("predefinedExclusions")
    private int predefinedExclusions;

    @SerializedName("intendedUseDescription")
    private String intendedUseDescription;

    /**
     * The taskOptions contain and open-ended map that can be used to pass custom options from
     * JSON to the task builder method in TaskItemFactory
     */
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
