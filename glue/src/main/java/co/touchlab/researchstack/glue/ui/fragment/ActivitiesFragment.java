package co.touchlab.researchstack.glue.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.database.TaskRecord;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.SchedulesAndTasksModel;
import co.touchlab.researchstack.glue.model.TaskModel;
import co.touchlab.researchstack.glue.schedule.ScheduleHelper;
import co.touchlab.researchstack.glue.task.SmartSurveyTask;
import co.touchlab.researchstack.glue.utils.JsonUtils;

/**
 * Created by bradleymcdermott on 10/28/15.
 */
public class ActivitiesFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_activities,
                container,
                false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new TaskAdapter(loadTasksAndSchedules()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        return view;
    }

    private ArrayList<SchedulesAndTasksModel.TaskModel> loadTasksAndSchedules()
    {
        SchedulesAndTasksModel schedulesAndTasksModel = JsonUtils.loadClass(getContext(),
                                                                            SchedulesAndTasksModel.class,
                                                                            "tasks_and_schedules");
        Map<String, TaskRecord> latestForAllTypes = StorageManager
                .getAppDatabase()
                .findLatestForAllTypes();

        ArrayList<SchedulesAndTasksModel.TaskModel> tasks = new ArrayList<>();
        for (SchedulesAndTasksModel.ScheduleModel schedule : schedulesAndTasksModel.schedules)
        {
            for (SchedulesAndTasksModel.TaskModel task : schedule.tasks)
            {
                TaskRecord taskRecord = latestForAllTypes.get(task.taskID);
                if(taskRecord == null)
                {
                    tasks.add(task);
                }
                else if(StringUtils.isNotEmpty(schedule.scheduleString))
                {
                    Date date = ScheduleHelper
                            .nextSchedule(schedule.scheduleString, taskRecord.completed);
                    if(date.before(new Date()))
                    {
                        tasks.add(task);
                    }
                }
            }
        }

        return tasks;
    }

    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
    {
        List<SchedulesAndTasksModel.TaskModel> tasks;

        public TaskAdapter(List<SchedulesAndTasksModel.TaskModel> tasks)
        {
            super();
            this.tasks = tasks;
        }

        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule,
                            parent,
                            false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position)
        {
            SchedulesAndTasksModel.TaskModel task = tasks.get(position);
            holder.title.setText(task.taskTitle);
            holder.completionTime.setText(task.taskCompletionTime);

            // TODO fix this, just for looks atm
            holder.dailyIndicator.setBackgroundResource(task.taskTitle.equals("Daily Survey") ? R.color.recurring_color : R.color.one_time_color);

            holder.itemView.setOnClickListener(v -> {
                // TODO this is just a simple implementation to get it working, we'll need
                // TODO to do something with the activity result later
                LogExt.d(getClass(),
                        "Item clicked: " + task.taskID);
                TaskModel taskModel = JsonUtils.loadClass(v.getContext(), TaskModel.class,
                                                          task.taskFileName);
                SmartSurveyTask newTask = new SmartSurveyTask(taskModel, task.taskID);

                v.getContext()
                        .startActivity(ViewTaskActivity.newIntent(v.getContext(),
                                newTask));
            });
        }

        @Override
        public int getItemCount()
        {
            return tasks.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            View dailyIndicator;
            AppCompatCheckBox completed;
            AppCompatTextView title;
            AppCompatTextView completionTime;

            public ViewHolder(View itemView)
            {
                super(itemView);
                dailyIndicator = itemView.findViewById(R.id.daily_indicator);
                completed = (AppCompatCheckBox) itemView.findViewById(R.id.completed);
                title = (AppCompatTextView) itemView.findViewById(R.id.task_title);
                completionTime = (AppCompatTextView) itemView.findViewById(R.id.task_completion_time);
            }
        }
    }
}
