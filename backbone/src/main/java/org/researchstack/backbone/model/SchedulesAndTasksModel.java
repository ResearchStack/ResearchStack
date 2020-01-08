package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SchedulesAndTasksModel {
    public List<ScheduleModel> schedules;

    public static class ScheduleModel {
        public String scheduleType;
        public String delay;
        public String scheduleString;
        public Date scheduledOn;
        public Date expiresOn;
        public List<TaskScheduleModel> tasks;
    }

    public static class TaskScheduleModel {
        public String taskTitle;
        public String taskID;
        public String taskFileName;
        public String taskClassName;
        public boolean taskIsOptional;
        public String taskType;
        public Date taskFinishedOn;

        /**
         * The GUID can distinguish between different instances of models with the same taskID
         */
        public String taskGUID;

        /**
         * The time it takes to complete the task
         */
        @SerializedName("taskCompletionTimeString")
        public String taskCompletionTime;
    }
}
