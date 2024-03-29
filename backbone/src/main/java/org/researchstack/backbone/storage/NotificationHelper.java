package org.researchstack.backbone.storage;

import android.content.Context;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.utils.LogExt;

import java.sql.SQLException;
import java.util.List;

public class NotificationHelper extends OrmLiteSqliteOpenHelper {
    public static final String DB_NAME = "db_notification";

    private static int DB_VERSION = 1;

    private static NotificationHelper sInstance;

    private NotificationHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static NotificationHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NotificationHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase database, ConnectionSource connectionSource)  {
        try {
            TableUtils.createTable(connectionSource, TaskNotification.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)  {
        try {
            TableUtils.dropTable(connectionSource,TaskNotification.class,true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TaskNotification> loadTaskNotifications() {
        LogExt.d(getClass(), "loadTaskNotifications()");
        try {
            Dao<TaskNotification, String> taskDao = getDao(TaskNotification.class);
            return taskDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTaskNotification(TaskNotification notification) {
        LogExt.d(getClass(), "saveTaskNotification() : " + notification.id);

        try {
            Dao<TaskNotification, Integer> taskDao = getDao(TaskNotification.class);
            taskDao.createOrUpdate(notification);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTaskNotification(int taskNotificationId) {
        LogExt.d(getClass(), "deleteTaskNotification() : " + taskNotificationId);

        try {
            Dao<TaskNotification, Integer> taskDao = getDao(TaskNotification.class);
            taskDao.deleteById(taskNotificationId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
