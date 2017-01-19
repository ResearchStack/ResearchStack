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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.ui.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;


public class ActivitiesFragment extends Fragment implements StorageAccessListener {
    private static final int REQUEST_TASK = 1492;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rss_fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unsubscribe();
    }

    private void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void setUpAdapter() {
        unsubscribe();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL_LIST,
                0,
                false));

        Observable.create(subscriber -> {
            SchedulesAndTasksModel model = DataProvider.getInstance()
                    .loadTasksAndSchedules(getActivity());
            subscriber.onNext(model);
        })
                .compose(ObservableUtils.applyDefault())
                .map(o -> (SchedulesAndTasksModel) o)
                .subscribe(model -> {
                    adapter = new TaskAdapter(model);
                    recyclerView.setAdapter(adapter);

                    subscription = adapter.getPublishSubject().subscribe(task -> {

                        Task newTask = DataProvider.getInstance().loadTask(getContext(), task);

                        if (newTask == null) {
                            Toast.makeText(getActivity(),
                                    R.string.rss_local_error_load_task,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startActivityForResult(ViewTaskActivity.newIntent(getContext(), newTask),
                                REQUEST_TASK);
                    });
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK) {
            LogExt.d(getClass(), "Received task result from task activity");

            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            StorageAccess.getInstance().getAppDatabase().saveTaskResult(taskResult);
            DataProvider.getInstance().uploadTaskResult(getActivity(), taskResult);

            setUpAdapter();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDataReady() {
        LogExt.i(getClass(), "onDataReady()");

        setUpAdapter();
    }

    @Override
    public void onDataFailed() {
        // Ignore
    }

    @Override
    public void onDataAuth() {
        // Ignore, activity handles auth
    }

    public static class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
        List<SchedulesAndTasksModel.TaskScheduleModel> tasks;
        HashMap<String, Boolean> taskScheduleType;

        PublishSubject<SchedulesAndTasksModel.TaskScheduleModel> publishSubject = PublishSubject.create();

        public TaskAdapter(SchedulesAndTasksModel model) {
            super();

            tasks = new ArrayList<>();
            taskScheduleType = new HashMap<>();

            for (SchedulesAndTasksModel.ScheduleModel schedule : model.schedules) {
                for (SchedulesAndTasksModel.TaskScheduleModel task : schedule.tasks) {
                    taskScheduleType.put(task.taskID, schedule.scheduleType.equals("once"));
                    tasks.add(task);
                }
            }
        }

        public PublishSubject<SchedulesAndTasksModel.TaskScheduleModel> getPublishSubject() {
            return publishSubject;
        }

        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rss_item_schedule, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
            SchedulesAndTasksModel.TaskScheduleModel task = tasks.get(position);
            boolean isOneTime = taskScheduleType.get(task.taskID);

            Resources res = holder.itemView.getResources();
            int tintColor = res.getColor(isOneTime
                    ? R.color.rss_recurring_color
                    : R.color.rss_one_time_color);

            holder.title.setText(Html.fromHtml("<b>" + task.taskTitle + "</b>"));
            holder.title.append("\n" + task.taskCompletionTime);
            holder.title.setTextColor(tintColor);

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
        public int getItemCount() {
            return tasks.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView dailyIndicator;
            AppCompatTextView title;

            public ViewHolder(View itemView) {
                super(itemView);
                dailyIndicator = (ImageView) itemView.findViewById(R.id.daily_indicator);
                title = (AppCompatTextView) itemView.findViewById(R.id.task_title);
            }
        }
    }
}
