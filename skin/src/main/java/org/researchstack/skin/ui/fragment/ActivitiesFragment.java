package org.researchstack.skin.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.task.SmartSurveyTask;
import org.researchstack.skin.ui.views.DividerItemDecoration;
import org.researchstack.skin.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
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

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL_LIST,
                0,
                false));

        Observable.create(subscriber -> {
            SchedulesAndTasksModel model = JsonUtils.loadClass(getContext(),
                    SchedulesAndTasksModel.class,
                    "tasks_and_schedules");
            subscriber.onNext(model);
        })
                .compose(ObservableUtils.applyDefault())
                .map(o -> (SchedulesAndTasksModel) o)
                .subscribe(model -> {
                    // TODO relook at what object this adapter uses. Could be something simpler than TaskModel
                    adapter = new TaskAdapter(model);
                    recyclerView.setAdapter(adapter);

                    subscription = adapter.getPublishSubject().subscribe(task -> {

                        SmartSurveyTask newTask = DataProvider.getInstance().loadTask(getContext(), task);

                        startActivityForResult(ViewTaskActivity.newIntent(getContext(), newTask), REQUEST_TASK);
                    });
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK)
        {
            LogExt.d(getClass(), "Received task result from task activity");

            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
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
        HashMap<String, Boolean> taskScheduleType;

        PublishSubject<SchedulesAndTasksModel.TaskModel> publishSubject = PublishSubject.create();

        public TaskAdapter(SchedulesAndTasksModel model)
        {
            super();

            tasks = new ArrayList<>();
            taskScheduleType = new HashMap<>();

            //TODO refactor this, data should already be prepared / include all data we need
            for(SchedulesAndTasksModel.ScheduleModel schedule : model.schedules)
            {
                for(SchedulesAndTasksModel.TaskModel task : schedule.tasks)
                {
                    // TODO supporting tasks that define taskClassName instead of a file should be supported
                    if (!TextUtils.isEmpty(task.taskFileName))
                    {
                        taskScheduleType.put(task.taskID, schedule.scheduleType.equals("once"));
                        tasks.add(task);
                    }
                }
            }
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
            boolean isOneTime = taskScheduleType.get(task.taskID);

            Resources res = holder.itemView.getResources();
            int tintColor = res.getColor(isOneTime
                    ? R.color.rss_recurring_color
                    : R.color.rss_one_time_color);

            holder.title.setText(Html.fromHtml("<b>" + task.taskTitle + "</b>"));
            holder.title.append("\n" + task.taskCompletionTime);
            holder.title.setTextColor(tintColor);

            //TODO get current drawable or set "complete" drawable if complete
            Drawable drawable = holder.dailyIndicator.getDrawable();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, tintColor);
            holder.dailyIndicator.setImageDrawable(drawable);

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
            ImageView         dailyIndicator;
            AppCompatTextView title;

            public ViewHolder(View itemView)
            {
                super(itemView);
                dailyIndicator = (ImageView) itemView.findViewById(R.id.daily_indicator);
                title = (AppCompatTextView) itemView.findViewById(R.id.task_title);
            }
        }
    }
}
