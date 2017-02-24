package org.researchstack.skin.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.skin.ui.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;


public class ActivitiesFragment extends Fragment implements StorageAccessListener {
    private static final String LOG_TAG = ActivitiesFragment.class.getCanonicalName();
    private static final int REQUEST_TASK = 1492;
    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private Subscription subscription;
    private SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rss_fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: might need to add logic to prevent multiple requests
                fetchData();
            }
        });

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

        fetchData();

    }

    private void fetchData() {
        Observable.create(subscriber -> {
            SchedulesAndTasksModel model = DataProvider.getInstance()
                    .loadTasksAndSchedules(getActivity());
            subscriber.onNext(model);
        })
                .compose(ObservableUtils.applyDefault())
                .map(o -> (SchedulesAndTasksModel) o)
                .subscribe(model -> {
                    swipeContainer.setRefreshing(false);
                    if(adapter == null) {
                        adapter = new TaskAdapter(getActivity());
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
                    } else {
                        adapter.clear();
                    }

                    adapter.addAll(processResults(model));

                });
    }

    /**
     * Process the model to create section groups and section headers
     * @param model
     * @return
     */
    private List<Object> processResults(SchedulesAndTasksModel model) {
        List<Object> tasks = new ArrayList<>();

        DateTime now = new DateTime();
        DateTime startOfDay = new DateTime().withTimeAtStartOfDay().minusSeconds(1);
        DateTime startOfYesterday = new DateTime().minusDays(1).withTimeAtStartOfDay().minusSeconds(1);
        DateTime startOfTomorrow = new DateTime().plusDays(1).withTimeAtStartOfDay().minusSeconds(1);

        List<SchedulesAndTasksModel.TaskScheduleModel> yesterdayTasks = new ArrayList<>();
        List<SchedulesAndTasksModel.TaskScheduleModel> todaysTasks = new ArrayList<>();
        List<SchedulesAndTasksModel.TaskScheduleModel> optionalTasks = new ArrayList<>();

        for (SchedulesAndTasksModel.ScheduleModel schedule : model.schedules) {
            DateTime scheduled = (schedule.scheduledOn != null) ? new DateTime(schedule.scheduledOn) : new DateTime();
            boolean today = (scheduled.isAfter(startOfDay) && scheduled.isBefore(startOfTomorrow));
            boolean yesterday = (scheduled.isAfter(startOfYesterday) && scheduled.isBefore(startOfDay));
            for (SchedulesAndTasksModel.TaskScheduleModel task : schedule.tasks) {
                if(today && task.taskIsOptional) {
                    optionalTasks.add(task);
                } else if(today) {
                    todaysTasks.add(task);
                } else if(yesterday) {
                    yesterdayTasks.add(task);
                } else {
                    // skipping task
                    LogExt.d(LOG_TAG, "Skipping task: " + task.taskID);
                }

            }
        }

        // todays tasks
        tasks.add(new TaskAdapter.Header(getActivity().getString(R.string.rss_activities_today_header_title,
                now.dayOfWeek().getAsText(),
                now.monthOfYear().getAsText(),
                now.dayOfMonth().getAsText()),
                getActivity().getString(R.string.rss_activities_today_header_message)));
        tasks.addAll(todaysTasks);

        // todays optional tasks
        if(optionalTasks.size() > 0) {
            tasks.add(new TaskAdapter.Header(getActivity().getString(R.string.rss_activities_optional_header_title),
                    getActivity().getString(R.string.rss_activities_optional_header_message)));
            tasks.addAll(optionalTasks);
        }

        // yesterdays tasks
        if(yesterdayTasks.size() > 0) {
            tasks.add(new TaskAdapter.Header(getActivity().getString(R.string.rss_activities_yesterday_header_title),
                    getActivity().getString(R.string.rss_activities_yesterday_header_message)));
            tasks.addAll(yesterdayTasks);
        }

        return tasks;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK) {
            LogExt.d(LOG_TAG, "Received task result from task activity");

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
        LogExt.i(LOG_TAG, "onDataReady()");

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

    public static class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_HEADER = 0;
        private static final int VIEW_TYPE_ITEM = 1;

        List<Object> tasks;
        private LayoutInflater inflater;

        PublishSubject<SchedulesAndTasksModel.TaskScheduleModel> publishSubject = PublishSubject.create();

        public TaskAdapter(Context context) {
            super();

            tasks = new ArrayList<>();
            this.inflater = LayoutInflater.from(context);
        }

        public PublishSubject<SchedulesAndTasksModel.TaskScheduleModel> getPublishSubject() {
            return publishSubject;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == VIEW_TYPE_HEADER) {
                View view = inflater.inflate(R.layout.rss_item_schedule_header, parent, false);
                return new ActivitiesFragment.TaskAdapter.HeaderViewHolder(view);
            } else {
                View view = inflater.inflate(R.layout.rss_item_schedule, parent, false);
                return new ActivitiesFragment.TaskAdapter.ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position) {
            Object obj = tasks.get(position);
            if(hldr instanceof ViewHolder) {
                ViewHolder holder = (ViewHolder) hldr;
                SchedulesAndTasksModel.TaskScheduleModel task = (SchedulesAndTasksModel.TaskScheduleModel)obj;

                Resources res = holder.itemView.getResources();
                int tintColor = getColorForTask(res, task.taskID);
                holder.colorBar.setBackgroundColor(tintColor);

                holder.title.setText(task.taskTitle);
                holder.subtitle.setText(task.taskCompletionTime);

                holder.itemView.setOnClickListener(v -> {
                    LogExt.d(LOG_TAG, "Item clicked: " + task.taskID + ", " + task.taskType);
                    publishSubject.onNext(task);
                });
            } else {
                HeaderViewHolder holder = (HeaderViewHolder) hldr;
                Header header = (Header)obj;
                holder.title.setText(header.title);
                holder.message.setText(header.message);
            }
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        @Override
        public int getItemViewType(int position) {
            Object item = tasks.get(position);
            return item instanceof Header ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
        }

        // Clean all elements of the recycler
        public void clear() {
            tasks.clear();
            notifyDataSetChanged();
        }

        // Add a list of items
        public void addAll(List<Object> list) {
            tasks.addAll(list);
            notifyDataSetChanged();
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {

            TextView title;
            TextView message;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.activity_header_title);
                message = (TextView) itemView.findViewById(R.id.activity_header_message);
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            View colorBar;
            ImageView dailyIndicator;
            TextView title;
            TextView subtitle;

            public ViewHolder(View itemView) {
                super(itemView);
                colorBar = itemView.findViewById(R.id.color_bar);
                dailyIndicator = (ImageView) itemView.findViewById(R.id.daily_indicator);
                title = (TextView) itemView.findViewById(R.id.task_title);
                subtitle = (TextView) itemView.findViewById(R.id.task_subtitle);
            }
        }

        public static class Header {
            String title;
            String message;

            public Header(String t, String m) {
                title = t;
                message = m;
            }
        }

        // TODO: this should live somewhere else like ResourceManager?  Cause it will be app specific
        //       and the constants defined somewhere?
        private int getColorForTask(Resources resources, String taskId) {
            int colorId = 0;
            if(taskId != null) {
                if (taskId.contains("APHTimedWalking")) {
                    colorId = resources.getColor(R.color.rss_activity_yellow);
                } else if (taskId.contains("APHPhonation")) {
                    colorId = resources.getColor(R.color.rss_activity_blue);
                } else if (taskId.contains("APHIntervalTapping")) {
                    colorId = resources.getColor(R.color.rss_activity_purple);
                } else if (taskId.contains("APHMedicationTracker")) {
                    colorId = resources.getColor(R.color.rss_activity_red);
                } else if (taskId.contains("APHTremor")) {
                    colorId = resources.getColor(R.color.rsb_colorPrimary);
                } else {
                    colorId = resources.getColor(R.color.rss_activity_default);
                }
            } else {
                colorId = resources.getColor(R.color.rss_activity_default);
            }

            return colorId;
        }
    }
}
