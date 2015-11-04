package co.touchlab.touchkit.rk.ui.fragment;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.SchedulesAndTasksModel;

/**
 * Created by bradleymcdermott on 10/28/15.
 */
public class ActivitiesFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new TaskAdapter(loadTasksAndSchedules()));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        return view;
    }

    private ArrayList<SchedulesAndTasksModel.TaskModel> loadTasksAndSchedules()
    {
        Gson gson = new GsonBuilder().setDateFormat("MMM yyyy").create();
        InputStream stream = getResources().openRawResource(R.raw.tasks_and_schedules);
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(stream, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw  new RuntimeException(e);
        }

        SchedulesAndTasksModel schedulesAndTasksModel = gson.fromJson(reader,
                SchedulesAndTasksModel.class);
        ArrayList<SchedulesAndTasksModel.TaskModel> tasks = new ArrayList<>();
        for (SchedulesAndTasksModel.ScheduleModel schedule : schedulesAndTasksModel.schedules)
        {
            for (SchedulesAndTasksModel.TaskModel task : schedule.tasks)
            {
                tasks.add(task);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
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
