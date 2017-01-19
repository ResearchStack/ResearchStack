package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SchedulesAndTasksModel {
    public List<ScheduleModel> schedules;

    public static class ScheduleModel {
        public String scheduleType;
        public String delay;
        public String scheduleString;
        public List<TaskScheduleModel> tasks;
    }

    public static class TaskScheduleModel {
        public String taskTitle;
        public String taskID;
        public String taskFileName;
        public String taskClassName;

        @SerializedName("taskCompletionTimeString")
        public String taskCompletionTime;
    }
}
