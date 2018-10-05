package org.researchstack.backbone.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.factory.ObservableTransformerFactory;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.model.taskitem.factory.TaskItemFactory;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.file.StorageAccessListener;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.factory.MoodSurveyFactory;
import org.researchstack.backbone.task.factory.MoodSurveyFrequency;
import org.researchstack.backbone.ui.ActiveTaskActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.adapter.TaskAdapter;
import org.researchstack.backbone.ui.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public abstract class ActivitiesFragment extends Fragment implements StorageAccessListener {
    private static final String LOG_TAG = ActivitiesFragment.class.getCanonicalName();
    public static final int REQUEST_TASK = 1492;

    private TaskAdapter adapter;
    private IntentFactory intentFactory = IntentFactory.INSTANCE;
    private ObservableTransformerFactory observableTransformerFactory =
            ObservableTransformerFactory.INSTANCE;
    protected RecyclerView recyclerView;
    private Subscription subscription;
    private SwipeRefreshLayout swipeContainer;

    public TaskAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TaskAdapter adapter) {
        this.adapter = adapter;
    }

    /** Intent factory, made available for subclasses to create Intent instances. */
    public final IntentFactory getIntentFactory() {
        return intentFactory;
    }

    /** Intent factory setter, made available if subclasses' unit tests need to mock. */
    public final void setIntentFactory(@NonNull IntentFactory intentFactory) {
        this.intentFactory = intentFactory;
    }

    // To allow unit tests to mock.
    @VisibleForTesting
    void setObservableTransformerFactory(
            @NonNull ObservableTransformerFactory observableTransformerFactory) {
        this.observableTransformerFactory = observableTransformerFactory;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public Subscription getRxSubscription() {
        return subscription;
    }

    public void setRxSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public SwipeRefreshLayout getSwipeFreshLayout() {
        return swipeContainer;
    }

    // To allow unit tests to mock.
    @VisibleForTesting
    void setSwipeFreshLayout(@NonNull SwipeRefreshLayout swipeContainer) {
        this.swipeContainer = swipeContainer;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rsb_fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeContainer = view.findViewById(R.id.swipe_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL_LIST, 0, false));

        createOrClearAdapter();

        // TODO: might need to add logic to prevent multiple requests
        swipeContainer.setOnRefreshListener(this::fetchData);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unsubscribe();
    }

    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    /**
     * Override this method to provide a customer adapter for your application.
     *
     * @return The adapter for displaying the list of tasks.
     */
    protected TaskAdapter createTaskAdapter() {
        return new TaskAdapter(getActivity());
    }

    public void fetchData() {
        DataProvider.getInstance().loadTasksAndSchedules(getActivity()).toObservable()
                .compose(observableTransformerFactory.defaultTransformer())
                .subscribe(new Action1<SchedulesAndTasksModel>() {
                    @Override
                    public void call(SchedulesAndTasksModel model) {
                        if (isFragmentValid()) {
                            refreshAdapterSuccess(model);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (isFragmentValid()) {
                            refreshAdapterFailure(throwable.getLocalizedMessage());
                        }
                    }
                });
    }

    protected boolean isFragmentValid() {
        return getActivity() != null && isAdded();
    }

    protected void refreshAdapterFailure(String errorMessage) {
        swipeContainer.setRefreshing(false);
        new AlertDialog.Builder(getContext())
                .setMessage(errorMessage)
                .setPositiveButton(R.string.rsb_BUTTON_OK, null)
                .create().show();
    }

    /**
     * @param model SchedulesAndTasksModel can be used to fill the adapter
     */
    protected void refreshAdapterSuccess(SchedulesAndTasksModel model) {
        swipeContainer.setRefreshing(false);
        createOrClearAdapter();
        adapter.addAll(processResults(model));
    }

    protected void createOrClearAdapter() {
        if (adapter == null) {
            unsubscribe();
            adapter = createTaskAdapter();
            recyclerView.setAdapter(adapter);

            subscription = adapter.getPublishSubject().subscribe(task -> {
                LogExt.d(LOG_TAG, "Publish subject subscribe clicked.");
                taskSelected(task);
            });
        } else {
            adapter.clear();
        }
    }

    public void taskSelected(SchedulesAndTasksModel.TaskScheduleModel task) {
        // Load task attempts to load a survey task based, based on the data provider.
        DataProvider.getInstance().loadTask(getContext(), task).subscribe(newTask -> {
            if (newTask == null) {
                // We were unable to load the survey task. This probably means it's one of these
                // custom tasks, keyed off task ID.
                startCustomTask(task);
            } else {
                // This is a survey task.
                startActivityForResult(intentFactory.newTaskIntent(getContext(),
                        getDefaultViewTaskActivityClass(), newTask), REQUEST_TASK);
            }
        });
    }

    public Class<? extends ViewTaskActivity> getDefaultViewTaskActivityClass() {
        return ViewTaskActivity.class;
    }

    /**
     * <p>
     * Start a custom task based on the given task schedule model. Generally used if the
     * DataProvider cannot load the task for whatever reason.
     * </p>
     * <p>
     * Apps should override this method to specify their own custom tasks.
     * </p>
     *
     * @param task
     *         task schedule model to trigger the custom task
     */
    protected abstract void startCustomTask(SchedulesAndTasksModel.TaskScheduleModel task);

    /**
     * Process the model to create section groups and section headers
     *
     * @param model SchedulesAndTasksModel object
     * @return a list of section groups and section headers
     */
    public List<Object> processResults(SchedulesAndTasksModel model) {
        if (model == null || model.schedules == null) {
            return Lists.newArrayList();
        }
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
                if (today && task.taskIsOptional) {
                    optionalTasks.add(task);
                } else if (today) {
                    todaysTasks.add(task);
                } else if (yesterday) {
                    yesterdayTasks.add(task);
                } else {
                    // skipping task
                    LogExt.d(LOG_TAG, "Skipping task: " + task.taskID);
                }

            }
        }

        // todays tasks
        tasks.add(new TaskAdapter.Header(getActivity().getString(R.string.rsb_activities_today_header_title,
                now.dayOfWeek().getAsText(),
                now.monthOfYear().getAsText(),
                now.dayOfMonth().getAsText()),
                getActivity().getString(R.string.rsb_activities_today_header_message)));
        tasks.addAll(todaysTasks);

        // todays optional tasks
        if (optionalTasks.size() > 0) {
            tasks.add(new TaskAdapter.Header(getActivity().getString(R.string.rsb_activities_optional_header_title),
                    getActivity().getString(R.string.rsb_activities_optional_header_message)));
            tasks.addAll(optionalTasks);
        }

        // yesterdays tasks
        if (yesterdayTasks.size() > 0) {
            tasks.add(new TaskAdapter.Header(getActivity().getString(R.string.rsb_activities_yesterday_header_title),
                    getActivity().getString(R.string.rsb_activities_yesterday_header_message)));
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

            fetchData();;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDataReady() {
        LogExt.i(LOG_TAG, "onDataReady()");

        fetchData();
    }

    @Override
    public void onDataFailed() {
        // Ignore
    }

    @Override
    public void onDataAuth() {
        // Ignore, activity handles auth
    }

    // TODO: remove the methods below once we finish task builder
    private Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
        builder.registerTypeAdapter(TaskItem.class, new TaskItemAdapter());
        return builder.create();
    }
  
    public void startCustomTappingTask() {
        String taskItemJson = "{\"taskIdentifier\":\"2-APHIntervalTapping-7259AC18-D711-47A6-ADBD-6CFCECDED1DF\",\"schemaIdentifier\":\"TappingActivity\",\"taskType\":\"tapping\",\"intendedUseDescription\":\"Speed of finger tapping can reflect severity of motor symptoms in Parkinson disease. This activity measures your tapping speed for each hand. Your medical provider may measure this differently.\",\"taskOptions\":{\"duration\":20.0,\"handOptions\":\"both\"},\"localizedSteps\":[{\"identifier\":\"conclusion\",\"type\":\"instruction\",\"text\":\"Thank You!\"}]}";
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(getContext(), createGson().fromJson(taskItemJson, TaskItem.class));
        startActivityForResult(ActiveTaskActivity.newIntent(getContext(), task), REQUEST_TASK);
    }

    public void startCustomTremorTask() {
        String taskItemJson = "{\"taskIdentifier\":\"1-APHTremor-108E189F-4B5B-48DC-BFD7-FA6796EEf439\",\"schemaIdentifier\":\"Tremor Activity\",\"taskType\":\"tremor\",\"taskOptions\":{\"duration\":10.0,\"handOptions\":\"both\",\"excludePositions\":[\"elbowBent\",\"handQueenWave\"]}}";
        startCustomTask(taskItemJson);
    }

    public void startCustomVoiceTask() {
        String taskItemJson = "{\"taskIdentifier\":\"3-APHPhonation-C614A231-A7B7-4173-BDC8-098309354292\",\"schemaIdentifier\":\"Voice Activity\",\"taskType\":\"voice\",\"intendedUseDescription\":\"This activitiy evaluates your voice by recording it with the microphone at the bottom of your phone.\",\"localizedSteps\":[{\"identifier\":\"instruction\",\"type\":\"instruction\",\"title\":\"Voice\"},{\"identifier\":\"instruction1\",\"type\":\"instruction\",\"title\":\"Voice\",\"text\":\"Take a deep breath and say “Aaaaah” into the microphone for as long as you can. Keep a steady volume so the audio bars remain blue.\",\"detailText\":\"Tap Get Started to begin the test.\"},{\"identifier\":\"countdown\",\"type\":\"instruction\",\"text\":\"Please wait while we check the ambient sound levels.\"}],\"taskOptions\":{\"duration\":10.0}}";
        startCustomTask(taskItemJson);
    }

    public void startCustomWalkingTask() {
        String taskItemJson = "{\"taskIdentifier\":\"4-APHTimedWalking-80F09109-265A-49C6-9C5D-765E49AAF5D9\",\"schemaIdentifier\":\"Walking Activity\",\"taskType\":\"shortWalk\",\"taskOptions\":{\"restDuration\":30.0,\"numberOfStepsPerLeg\":100.0},\"removeSteps\":[\"walking.return\"],\"localizedSteps\":[{\"identifier\":\"instruction\",\"type\":\"instruction\",\"text\":\"This activity measures your gait (walk) and balance, which can be affected by Parkinson disease.\",\"detailText\":\"Please do not continue if you cannot safely walk unassisted.\"},{\"identifier\":\"instruction1\",\"type\":\"instruction\",\"text\":\"\u2022 Please wear a comfortable pair of walking shoes and find a flat, smooth surface for walking.\n\n\u2022 Try to walk continuously by turning at the ends of your path, as if you are walking around a cone.\n\n\u2022 Importantly, walk at your normal pace. You do not need to walk faster than usual.\",\"detailText\":\"Put your phone in a pocket or bag and follow the audio instructions.\"},{\"identifier\":\"walking.outbound\",\"type\":\"active\",\"stepDuration\":30.0,\"title\":\"\",\"text\":\"Walk back and forth for 30 seconds.\",\"stepSpokenInstruction\":\"Walk back and forth for 30 seconds.\"},{\"identifier\":\"walking.rest\",\"type\":\"active\",\"stepDuration\":30.0,\"text\":\"Turn around 360 degrees, then stand still, with your feet about shoulder-width apart. Rest your arms at your side and try to avoid moving for 30 seconds.\",\"stepSpokenInstruction\":\"Turn around 360 degrees, then stand still, with your feet about shoulder-width apart. Rest your arms at your side and try to avoid moving for 30 seconds.\"}]}";
        startCustomTask(taskItemJson);
    }

    public void startCustomMoodSurveyTask() {
        Task task = MoodSurveyFactory.moodSurvey(
                getContext(),
                MoodSurveyFactory.MoodSurveyIdentifier,
                getContext().getString(R.string.rsb_activities_mood_survey_intended_use),
                MoodSurveyFrequency.DAILY,
                "Today, my thinking is:",
                new ArrayList<>());
        startCustomTask(task);
    }

    //endregion

    //region Start Custom Task Helpers

    private void startCustomTask(String taskItemJson) {
        TaskItem taskItem = createGson().fromJson(taskItemJson, TaskItem.class);
        startCustomTask(taskItem);
    }

    private void startCustomTask(TaskItem taskItem) {
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(getContext(), taskItem);
        startCustomTask(task);
    }

    private void startCustomTask(Task task) {
        startActivityForResult(ActiveTaskActivity.newIntent(getContext(), task), REQUEST_TASK);
    }

    //endregion
}
