package org.researchstack.backbone.model.staged;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mauriciosouto on 7/9/17.
 */

public class MedStagedEvent implements Serializable {

    private int id;
    private String activityId;
    private Date eventStartDate;
    private Date eventEndDate;
    private MedStagedActivityState status;
    private Task task;
    private TaskResult result;
    private MedStagedActivity activity;

    public MedStagedEvent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public MedStagedActivityState getStatus() {
        return status;
    }

    public void setStatus(MedStagedActivityState status) {
        this.status = status;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskResult getResult() {
        return result;
    }

    public void addResult(TaskResult result, MedStagedActivityState status) {
        this.result = result;
        this.status = status;
    }

    public MedStagedActivity getActivity() {
        return activity;
    }

    public void setActivity(MedStagedActivity activity) {
        this.activity = activity;
    }
}
