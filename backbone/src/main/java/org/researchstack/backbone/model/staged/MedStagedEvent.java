package org.researchstack.backbone.model.staged;

import org.researchstack.backbone.task.Task;

import java.util.Date;

/**
 * Created by mauriciosouto on 7/9/17.
 */

public class MedStagedEvent {

    private String activityId;
    private Date eventStartDate;
    private Date eventEndDate;
    private MedStagedActivityState status;
    private Task result;
    private MedStagedActivity activity;

    public MedStagedEvent() {
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

    public Task getResult() {
        return result;
    }

    public void addResult(Task result, MedStagedActivityState status) {
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
