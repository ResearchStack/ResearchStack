package org.researchstack.backbone.model;

import java.util.Date;
import java.util.List;

public class SchedulesAndTasksModel {

    public List<ScheduleModel> schedules;

    public static class ScheduleModel {
        public String scheduleType;
        public String delay;
        public String scheduleString;
        public Date scheduledOn;
        public List<TaskScheduleModel> tasks;
    }

    public static class TaskScheduleModel {
        public String taskTitle;
        public String taskID;
        public String taskFileName;
        public String taskClassName;
        public boolean taskIsOptional;
        public String taskType;
        public String intendedUseDescription;
        public String taskCompletionTime;
    }
}
