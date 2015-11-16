package co.touchlab.researchstack.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SchedulesAndTasksModel
{
    @SerializedName("schedules")
    public List<ScheduleModel> schedules;

    public static class ScheduleModel
    {

        @SerializedName("scheduleType")
        public String scheduleType;

        @SerializedName("delay")
        public String delay;

        @SerializedName("tasks")
        public List<TaskModel> tasks;
    }

    public static class TaskModel
    {
        @SerializedName("taskTitle")
        public String taskTitle;

        @SerializedName("taskID")
        public String taskID;

        @SerializedName("taskFileName")
        public String taskFileName;

        @SerializedName("taskClassName")
        public String taskClassName;

        @SerializedName("taskCompletionTimeString")
        public String taskCompletionTime;
    }
}
