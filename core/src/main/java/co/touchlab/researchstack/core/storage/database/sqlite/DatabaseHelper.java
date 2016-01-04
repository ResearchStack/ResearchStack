package co.touchlab.researchstack.core.storage.database.sqlite;
import android.content.Context;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.database.TaskRecord;
import co.touchlab.squeaky.db.sqlite.SQLiteDatabaseImpl;
import co.touchlab.squeaky.db.sqlite.SqueakyOpenHelper;
import co.touchlab.squeaky.stmt.Where;
import co.touchlab.squeaky.table.TableUtils;

/**
 * Created by kgalligan on 11/27/15.
 */
public class DatabaseHelper extends SqueakyOpenHelper implements AppDatabase
{
    public static  String DB_NAME    = "appdb";
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
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
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

    @Override
    public List<TaskRecord> findTaskRecordById(String taskId)
    {
        try
        {
            Where<TaskRecord> where = new Where<>(getDao(TaskRecord.class));
            List<TaskRecord> taskRecords = where.eq("taskId", taskId)
                                                .query()
                                                .list();
            return taskRecords;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, TaskRecord> findLatestForAllTypes()
    {
        try
        {
            Map<String, TaskRecord> byTaskId = new HashMap<>();
            List<TaskRecord> taskRecords = getDao(TaskRecord.class).queryForAll()
                                                                   .list();
            for(TaskRecord taskRecord : taskRecords)
            {
                TaskRecord check = byTaskId.get(taskRecord.taskId);
                if(check == null || check.completed.before(taskRecord.completed))
                {
                    byTaskId.put(taskRecord.taskId, taskRecord);
                }
            }
            return byTaskId;
        }
        catch(SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
