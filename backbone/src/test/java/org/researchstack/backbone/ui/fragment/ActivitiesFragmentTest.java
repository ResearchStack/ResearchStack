package org.researchstack.backbone.ui.fragment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import com.google.common.collect.ImmutableList;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.factory.ObservableTransformerFactory;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.adapter.TaskAdapter;
import rx.Single;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActivitiesFragmentTest {
    private ActivitiesFragment fragment;
    private DataProvider mockDataProvider;
    private IntentFactory mockIntentFactory;
    private ObservableTransformerFactory mockObservableTransformerFactory;
    private TaskAdapter mockAdapter;

    @Before
    public void setup() {
        // Mock Data Provider
        mockDataProvider = mock(DataProvider.class);
        DataProvider.init(mockDataProvider);

        // Mock fragment dependencies
        mockAdapter = mock(TaskAdapter.class);
        mockIntentFactory = mock(IntentFactory.class);
        mockObservableTransformerFactory = mock(ObservableTransformerFactory.class);

        // Spy fragment, so we can separate out non-test calls.
        fragment = spy(new TestActivitiesFragment());
        fragment.setAdapter(mockAdapter);
        fragment.setIntentFactory(mockIntentFactory);
        fragment.setObservableTransformerFactory(mockObservableTransformerFactory);
        fragment.setSwipeFreshLayout(mock(SwipeRefreshLayout.class));
    }

    public static class TestActivitiesFragment extends ActivitiesFragment {
        @Override
        protected void startCustomTask(SchedulesAndTasksModel.TaskScheduleModel task) {
            // do nothing
        }
        @Override
        protected boolean isFragmentValid() {
            return true;
        }
    }

    @AfterClass
    public static void teardown() {
        // de-init DataProvider, so that it doesn't interfere with other tests.
        DataProvider.init(null);
    }

    @Test
    public void fetchData() {
        // Mock dataProvider.loadTasksAndSchedules()
        SchedulesAndTasksModel model = new SchedulesAndTasksModel();
        when(mockDataProvider.loadTasksAndSchedules(any())).thenReturn(Single.just(model));

        // Mock observableTransformerFactory.defaultTransformer() to return the no-op transform.
        when(mockObservableTransformerFactory.defaultTransformer()).thenReturn(o -> o);

        // Spy fragment.processResults()
        List<Object> processResultsList = ImmutableList.of();
        doReturn(processResultsList).when(fragment).processResults(any());

        // Execute
        fragment.fetchData();

        // Verify data flow
        verify(fragment).processResults(same(model));
        verify(mockAdapter).addAll(same(processResultsList));
    }

    @Test
    public void taskSelected_NonSurvey() {
        // Mock dataProvider.loadTask()
        when(mockDataProvider.loadTask(any(), any())).thenReturn(Single.just(null));

        // Make test task
        SchedulesAndTasksModel.TaskScheduleModel taskScheduleModel =
                new SchedulesAndTasksModel.TaskScheduleModel();

        // Execute
        fragment.taskSelected(taskScheduleModel);

        // Verify data flow
        verify(mockDataProvider).loadTask(any(), same(taskScheduleModel));
        verify(fragment).startCustomTask(same(taskScheduleModel));
    }

    @Test
    public void taskSelected_Survey() {
        // Mock dataProvider.loadTask()
        Task mockTask = mock(Task.class);
        when(mockDataProvider.loadTask(any(), any())).thenReturn(Single.just(mockTask));

        // Mock intentFactory.newIntent()
        Intent intent = new Intent();
        when(mockIntentFactory.newTaskIntent(any(), any(), any())).thenReturn(intent);

        // Spy fragment.startActivityForResult()
        doNothing().when(fragment).startActivityForResult(any(), anyInt());

        // Make test task
        SchedulesAndTasksModel.TaskScheduleModel taskScheduleModel =
                new SchedulesAndTasksModel.TaskScheduleModel();

        // Execute
        fragment.taskSelected(taskScheduleModel);

        // Verify data flow
        verify(mockDataProvider).loadTask(any(), same(taskScheduleModel));
        verify(mockIntentFactory).newTaskIntent(any(), eq(ViewTaskActivity.class), same(mockTask));
        verify(fragment).startActivityForResult(same(intent), eq(ActivitiesFragment.REQUEST_TASK));
    }
}
