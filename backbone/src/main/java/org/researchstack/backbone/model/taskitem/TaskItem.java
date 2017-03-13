package org.researchstack.backbone.model.taskitem;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.SurveyItem;

import java.util.List;

/**
 * Created by TheMDP on 3/7/17.
 *
 * An TaskItem is the model for a JSON task that can be a number of specific
 * tasks or even a custom one
 */

public class TaskItem {

    @SerializedName("taskIdentifier")
    private String taskIdentifier;

    /**
     * The schemaIdentifier describes the output format of the TaskResult
     * It can be skipped unless you specifically use it when accessing your db storage
     */
    @SerializedName("schemaIdentifier")
    private String schemaIdentifier;

    /**
     * If taskIsOptional is true, there will be a skip task button added to the first step of the task
     */
    @SerializedName("optional")
    private boolean taskIsOptional;

    static final String TASK_TYPE_GSON = "taskType";
    @SerializedName(TASK_TYPE_GSON)
    private TaskItemType taskType;

    /**
     * insertSteps is a list of steps that will be inserted into the Task after it is built
     */
    @SerializedName("insertSteps")
    private List<SurveyItem> insertSteps;

    /**
     * taskSteps are a list of SurveyItems that will be used to build the Task
     * it is similar to insertSteps, except the whole Task will be taskSteps instead of just
     * inserting specific steps into a pre-constructed TaskItemType
     */
    @SerializedName(value = "taskSteps", alternate = {"steps"})
    private List<SurveyItem> taskSteps;

    public TaskItem() {
        super();
    }

    public String getTaskIdentifier() {
        return taskIdentifier;
    }

    public void setTaskIdentifier(String taskIdentifier) {
        this.taskIdentifier = taskIdentifier;
    }

    public String getSchemaIdentifier() {
        return schemaIdentifier;
    }

    public void setSchemaIdentifier(String schemaIdentifier) {
        this.schemaIdentifier = schemaIdentifier;
    }

    public boolean isTaskIsOptional() {
        return taskIsOptional;
    }

    public void setTaskIsOptional(boolean taskIsOptional) {
        this.taskIsOptional = taskIsOptional;
    }

    public List<SurveyItem> getInsertSteps() {
        return insertSteps;
    }

    public void setInsertSteps(List<SurveyItem> insertSteps) {
        this.insertSteps = insertSteps;
    }

    public List<SurveyItem> getTaskSteps() {
        return taskSteps;
    }

    public void setTaskSteps(List<SurveyItem> taskSteps) {
        this.taskSteps = taskSteps;
    }

    public String getTaskTypeIdentifier() {
        return taskType.getValue();
    }

    public TaskItemType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskItemType type) {
        this.taskType = type;
    }
}
