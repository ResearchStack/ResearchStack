package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SchedulesAndTasksModel
{
    public List<ScheduleModel> schedules;

    /**
     * Model of schedule.
     * Example:
     * {
     *     "scheduleType"    : "recurring",
     *     "scheduleString"  : "0 5 * * *",
     *     "delay"           : "P2D",
     *     "interval"        : "P1D",
     * }
     */
    public static class ScheduleModel
    {
        public String                  scheduleType;
        public String                  delay;
        public String                  scheduleString;
        public String                  interval;
        public List<TaskScheduleModel> tasks;
    }

    public static class TaskScheduleModel
    {
        public String taskTitle;
        public String taskID;
        public String taskFileName;
        public String taskClassName;

        @SerializedName("taskCompletionTimeString")
        public String taskCompletionTime;
    }
}
