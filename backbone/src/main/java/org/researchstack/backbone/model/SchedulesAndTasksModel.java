package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SchedulesAndTasksModel {

    public static final String TASK_TYPE_ACTIVITY = "Activity";

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

        @SerializedName("taskIdentifier")
        public String taskID;

        public String taskFileName;
        public String taskClassName;
        public boolean taskIsOptional;
        public String taskType;
        public String intendedUseDescription;

        /**
         * A map of optional parameters that should be used when creating and running the task
         */
        public Map<String, Object> taskOptions;

        @SerializedName("taskCompletionTimeString")
        public String taskCompletionTime;
    }
}
