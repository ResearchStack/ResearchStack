package co.touchlab.researchstack.core.storage.database.sqlite;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.database.StepRecord;
import co.touchlab.researchstack.core.storage.database.TaskRecord;
import co.touchlab.researchstack.core.utils.FormatHelper;
import co.touchlab.squeaky.dao.Dao;
import co.touchlab.squeaky.db.sqlite.SQLiteDatabaseImpl;
import co.touchlab.squeaky.db.sqlite.SqueakyOpenHelper;
import co.touchlab.squeaky.stmt.Where;
import co.touchlab.squeaky.table.TableUtils;

/**
 * Created by kgalligan on 11/27/15.
 */
public class DatabaseHelper extends SqueakyOpenHelper implements AppDatabase
{
    // TODO Define in BuildConfig
    public static  String DB_NAME    = "appdb";

    // TODO Define in BuildConfig
    private static int    DB_VERSION = 1;

    private static DatabaseHelper sInstance;

    private DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION/*, new PassphraseProvider()
        {
            @Override
            public String getPassphrase()
            {
                return secret;
            }
        }*/);
    }

    public static DatabaseHelper getInstance(Context context)
    {
        if(sInstance == null)
        {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase)
    {
        try
        {
            TableUtils.createTables(new SQLiteDatabaseImpl(sqLiteDatabase), TaskRecord.class);

            TableUtils.createTables(new SQLiteDatabaseImpl(sqLiteDatabase), StepRecord.class);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        try
        {
            TableUtils.dropTables(new SQLiteDatabaseImpl(sqLiteDatabase), true, TaskRecord.class);

            TableUtils.dropTables(new SQLiteDatabaseImpl(sqLiteDatabase), true, StepRecord.class);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    // Task Record TODO Deprecated: remove when ready
    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

    @Override //TODO Deprecated
    public void saveTaskRecord(TaskRecord taskRecord)
    {
        try
        {
            getDao(TaskRecord.class).create(taskRecord);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override //TODO Deprecated
    public List<TaskRecord> findTaskRecordById(String taskId)
    {
        try
        {
            Where<TaskRecord> where = new Where<>(getDao(TaskRecord.class));
            List<TaskRecord> taskRecords = where.eq("taskResultId", taskId).query().list();
            return taskRecords;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override //TODO Deprecated
    public Map<String, TaskRecord> findLatestForAllTypes()
    {
        try
        {
            Map<String, TaskRecord> byTaskId = new HashMap<>();
            List<TaskRecord> taskRecords = getDao(TaskRecord.class).queryForAll().list();
            for(TaskRecord taskRecord : taskRecords)
            {
                TaskRecord check = byTaskId.get(taskRecord.taskResultId);
                if(check == null || check.completed.before(taskRecord.completed))
                {
                    byTaskId.put(taskRecord.taskResultId, taskRecord);
                }
            }
            return byTaskId;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
    // Task / Step Result
    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

    @Override
    public void saveTaskResult(TaskResult taskResult)
    {
        LogExt.d(getClass(), "saveTaskResult() id: " + taskResult.getIdentifier());

        try
        {
            TaskRecord taskRecord = new TaskRecord();
            taskRecord.taskResultId = taskResult.getIdentifier();
            taskRecord.started = taskResult.getStartDate();
            taskRecord.completed = taskResult.getEndDate();
            getDao(TaskRecord.class).create(taskRecord);

            Gson gson = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601).create();
            Dao<StepRecord> stepResultDao = getDao(StepRecord.class);

            for(StepResult stepResult : taskResult.getResults().values())
            {
                // TODO Step result could be null, which still indicates that it was answered. Figure
                // out how to handle this
                if (stepResult != null)
                {
                    StepRecord stepRecord = new StepRecord();
                    stepRecord.taskRecordId = taskRecord.id;
                    stepRecord.taskResultId = taskResult.getIdentifier();
                    stepRecord.stepResultId = stepResult.getIdentifier();
                    stepRecord.completed = stepResult.getEndDate();
                    if(! stepResult.getResults().isEmpty())
                    {
                        stepRecord.result = gson.toJson(stepResult.getResults());
                    }

                    stepResultDao.createOrUpdate(stepRecord);
                }
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskResult loadTaskResult(String taskRecordId)
    {
        LogExt.d(getClass(), "loadTaskResult() id: " + taskRecordId);

        try
        {
            TaskRecord taskRecord = getDao(TaskRecord.class).queryForId(taskRecordId);
            List<StepRecord> stepRecords = getDao(StepRecord.class).queryForEq(StepRecord.TASK_RECORD_ID,
                    taskRecordId).list();
            return TaskRecord.toTaskResult(taskRecord, stepRecords);
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TaskResult> loadTaskResults(String taskResultId)
    {
        LogExt.d(getClass(), "loadTaskResults() id: " + taskResultId);

        try
        {
            List<TaskResult> results = new ArrayList<>();
            List<TaskRecord> taskRecords = getDao(TaskRecord.class).queryForEq(TaskRecord.TASK_RESULT_ID,
                    taskResultId).list();

            for(TaskRecord record : taskRecords)
            {
                List<StepRecord> stepRecords = getDao(StepRecord.class).queryForEq(StepRecord.TASK_RECORD_ID,
                        record.id).list();
                TaskResult result = TaskRecord.toTaskResult(record, stepRecords);
                results.add(result);
            }

            return results;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StepResult> loadStepResults(String stepResultId)
    {
        LogExt.d(getClass(), "loadStepResults() id: " + stepResultId);

        try
        {
            List<StepResult> results = new ArrayList<>();
            List<StepRecord> stepRecords = getDao(StepRecord.class).queryForEq(StepRecord.STEP_RESULT_ID,
                    stepResultId).list();

            for(StepRecord stepRecord : stepRecords)
            {
                StepResult result = StepRecord.toStepResult(stepRecord);
                results.add(result);
            }

            return results;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

}
