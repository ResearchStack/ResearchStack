package co.touchlab.researchstack.skin.ui.fragment;

import android.app.Activity;
import android.content.Intent;
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

import java.util.Date;
import java.util.List;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.skin.DataProvider;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.model.SchedulesAndTasksModel;
import co.touchlab.researchstack.skin.task.SmartSurveyTask;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by bradleymcdermott on 10/28/15.
 */
public class ActivitiesFragment extends Fragment
{
    private static final int REQUEST_TASK = 1492;
    private TaskAdapter  adapter;
    private RecyclerView recyclerView;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        setUpAdapter();

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        unsubscribe();
    }

    private void unsubscribe()
    {
        if(subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    private void setUpAdapter()
    {
        // TODO make updating the list better, make sure not leaking memory with the rx subject
        unsubscribe();

        // TODO relook at what object this adapter uses. Could be something simpler than TaskModel
        List<SchedulesAndTasksModel.TaskModel> tasks = DataProvider.getInstance()
                .loadTasksAndSchedules(getContext());
        adapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        subscription = adapter.getPublishSubject().subscribe(task -> {

            SmartSurveyTask newTask = DataProvider.getInstance().loadTask(getContext(), task);

            startActivityForResult(ViewTaskActivity.newIntent(getContext(), newTask), REQUEST_TASK);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK)
        {
            LogExt.d(getClass(), "Received task result from task activity");

            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            taskResult.setEndDate(new Date());
            DataProvider.getInstance().uploadTaskResult(getActivity(), taskResult);

            setUpAdapter();
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
    {
        List<SchedulesAndTasksModel.TaskModel> tasks;

        PublishSubject<SchedulesAndTasksModel.TaskModel> publishSubject = PublishSubject.create();

        public TaskAdapter(List<SchedulesAndTasksModel.TaskModel> tasks)
        {
            super();
            this.tasks = tasks;
        }

        public PublishSubject<SchedulesAndTasksModel.TaskModel> getPublishSubject()
        {
            return publishSubject;
        }

        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position)
        {
            SchedulesAndTasksModel.TaskModel task = tasks.get(position);
            holder.title.setText(task.taskTitle);
            holder.completionTime.setText(task.taskCompletionTime);

            // TODO fix this, just for looks atm
            holder.dailyIndicator.setBackgroundResource(task.taskTitle.equals("Daily Survey")
                    ? R.color.recurring_color
                    : R.color.one_time_color);

            holder.itemView.setOnClickListener(v -> {
                LogExt.d(getClass(), "Item clicked: " + task.taskID);
                publishSubject.onNext(task);
            });
        }

        @Override
        public int getItemCount()
        {
            return tasks.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
        {
            View              dailyIndicator;
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
