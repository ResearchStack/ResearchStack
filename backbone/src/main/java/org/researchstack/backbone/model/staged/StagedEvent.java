package org.researchstack.backbone.model.staged;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mauriciosouto on 7/9/17.
 */

public class StagedEvent implements Serializable {

    private int id;
    private String activityId;
    private Date eventStartDate;
    private Date eventEndDate;
    private StagedActivityState status;
    private Task task;
    private TaskResult result;
    private StagedActivity activity;

    public StagedEvent() {
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

    public StagedActivityState getStatus() {
        return status;
    }

    public void setStatus(StagedActivityState status) {
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

    public void addResult(TaskResult result, StagedActivityState status) {
        this.result = result;
        this.status = status;
    }

    public StagedActivity getActivity() {
        return activity;
    }

    public void setActivity(StagedActivity activity) {
        this.activity = activity;
    }
}
