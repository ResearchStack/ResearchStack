package org.researchstack.backbone.storage.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.StepRecord;
import org.researchstack.backbone.storage.database.TaskRecord;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.LogExt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple database implementation of {@link AppDatabase} the has no encryption and only has tables
 * for saving TaskResults and StepResults. You can extend this class and override onCreate and
 * onUpgrade to add your own tables to those provided by this implementation.
 * <p>
 * Uses Squeaky, a simple ORM, which uses annotation processing on the model classes to create the
 * tables and add methods for database operations. <b>If you create a subclass of this database
 * class and add more models, you will need to add the annotation processor to your app's
 * build.gradle file:</b> (See the Sample App for details, but add `apt
 * 'co.touchlab.squeaky:squeaky-processor:0.4.0'` to your dependencies and add android-apt:
 * https://bitbucket.org/hvisser/android-apt)
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper implements AppDatabase {
    public static final String DEFAULT_NAME = "appdb";
    public static final int DEFAULT_VERSION = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,TaskRecord.class);
            TableUtils.createTable(connectionSource,StepRecord.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // handle future db upgrades here
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    // Task / Step Result
    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

    @Override
    public void saveTaskResult(TaskResult taskResult) {
        LogExt.d(getClass(), "saveTaskResult() id: " + taskResult.getIdentifier());

        try {
            TaskRecord taskRecord = new TaskRecord();
            taskRecord.taskId = taskResult.getIdentifier();
            taskRecord.started = taskResult.getStartDate();
            taskRecord.completed = taskResult.getEndDate();
            getDao(TaskRecord.class).create(taskRecord);

            Gson gson = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601).create();
            Dao<StepRecord, ?> stepResultDao = getDao(StepRecord.class);

            for (StepResult stepResult : taskResult.getResults().values()) {
                if (stepResult != null) {
                    StepRecord stepRecord = new StepRecord();
                    stepRecord.taskRecordId = taskRecord.id;
                    stepRecord.taskId = taskResult.getIdentifier();
                    stepRecord.stepId = stepResult.getIdentifier();
                    stepRecord.completed = stepResult.getEndDate();
                    stepRecord.answerFormatClass = stepResult.getAnswerFormat().getClass().getName();
                    stepRecord.answerFormat = gson.toJson(stepResult.getAnswerFormat());
                    if (!stepResult.getResults().isEmpty()) {
                        stepRecord.result = gson.toJson(stepResult.getResults());
                    }

                    stepResultDao.createOrUpdate(stepRecord);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskResult loadLatestTaskResult(String taskIdentifier) {
        LogExt.d(getClass(), "loadTaskResults() id: " + taskIdentifier);

        try {
            List<TaskRecord> taskRecords = getDao(TaskRecord.class).queryForEq(TaskRecord.TASK_ID, taskIdentifier);
            Collections.sort(taskRecords, new TaskRecord.SortByCompleted());

            if (taskRecords.isEmpty()) {
                return null;
            }

            TaskRecord record = taskRecords.get(0);
            List<StepRecord> stepRecords = getDao(StepRecord.class).queryForEq(StepRecord.TASK_RECORD_ID, record.id);
            return TaskRecord.toTaskResult(record, stepRecords);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TaskResult> loadTaskResults(String taskIdentifier) {
        LogExt.d(getClass(), "loadTaskResults() id: " + taskIdentifier);

        try {
            List<TaskResult> results = new ArrayList<>();
            List<TaskRecord> taskRecords = getDao(TaskRecord.class).queryForEq(TaskRecord.TASK_ID, taskIdentifier);

            for (TaskRecord record : taskRecords) {
                List<StepRecord> stepRecords = getDao(StepRecord.class).queryForEq(StepRecord.TASK_RECORD_ID,
                        record.id);
                TaskResult result = TaskRecord.toTaskResult(record, stepRecords);
                results.add(result);
            }

            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StepResult> loadStepResults(String stepIdentifier) {
        LogExt.d(getClass(), "loadStepResults() id: " + stepIdentifier);

        try {
            List<StepResult> results = new ArrayList<>();
            List<StepRecord> stepRecords = getDao(StepRecord.class).queryForEq(StepRecord.STEP_ID, stepIdentifier);

            for (StepRecord stepRecord : stepRecords) {
                StepResult result = StepRecord.toStepResult(stepRecord);
                results.add(result);
            }

            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setEncryptionKey(String key) {
        LogExt.w(getClass(), "No-op, this db implementation is not encrypted");
    }

}
