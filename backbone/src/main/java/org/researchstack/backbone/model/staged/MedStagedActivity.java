package org.researchstack.backbone.model.staged;

import java.util.Map;

/**
 * Created by mauriciosouto on 7/9/17.
 *
 * Staged is content that is scheduled, but not only for a specific day, it  arrives and hangs around until it is handled.
 * Each StagedActivity is tied to time intervals that may span days, weeks, months, or be open ended.
 *
 * - id: a unique ID for the activity
 * - type: one of survey, activeTask, or insight
 * - schedule: an MedStagedSchedule for this activity
 * - activityInfo: dictionary with type specific information - ["title" : TITLE_STRING, "text": TEXT_STRING, "instructions": INSTRUCTION_STRING, "task": TASK]
 *
 */

public class MedStagedActivity {

    private String id;
    private MedStagedActivityType type;
    private MedStagedSchedule schedule;
    private Map<String, Object> activityInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MedStagedActivityType getType() {
        return type;
    }

    public void setType(MedStagedActivityType type) {
        this.type = type;
    }

    public MedStagedSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(MedStagedSchedule schedule) {
        this.schedule = schedule;
    }

    public Map<String, Object> getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(Map<String, Object> activityInfo) {
        this.activityInfo = activityInfo;
    }
}
