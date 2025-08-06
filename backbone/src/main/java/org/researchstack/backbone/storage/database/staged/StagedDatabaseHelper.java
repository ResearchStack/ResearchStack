package org.researchstack.backbone.storage.database.staged;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.researchstack.backbone.model.staged.StagedActivity;
import org.researchstack.backbone.model.staged.StagedActivityState;
import org.researchstack.backbone.model.staged.StagedEvent;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.storage.database.staged.records.StagedActivityRecord;
import org.researchstack.backbone.storage.database.staged.records.StagedEventRecord;
import org.researchstack.backbone.utils.LogExt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by spiria on 14/9/17.
 */

public class StagedDatabaseHelper extends SqlCipherDatabaseHelper {

    public StagedDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, UpdatablePassphraseProvider passphraseProvider) {
        super(context, name, factory, version, passphraseProvider);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase database, ConnectionSource connectionSource) {
        super.onCreate(database, connectionSource);
        try {
            TableUtils.createTableIfNotExists(connectionSource, StagedActivityRecord.class);
            TableUtils.createTableIfNotExists(connectionSource, StagedEventRecord.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        super.onUpgrade(database, connectionSource, oldVersion, newVersion);
        try {
            TableUtils.dropTable(connectionSource,StagedActivityRecord.class,true);
            TableUtils.dropTable(connectionSource,StagedEventRecord.class, true);
            onCreate(database, connectionSource );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addOrUpdateStagedActivity(StagedActivity activity) {
        LogExt.d(getClass(), "addOrUpdateStagedActivity() id: " + activity.getId());
        try {
            getDao(StagedActivityRecord.class).createOrUpdate(StagedActivityRecord.toRecord(activity));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStagedActivity(String activityId) {
        LogExt.d(getClass(), "removeStagedActivity() Activity id: " + activityId);
        try {
            Dao<StagedActivityRecord, String> stagedDao = getDao(StagedActivityRecord.class);
            stagedDao.deleteById(activityId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public StagedActivity getStagedActivity(String activityId) {
        LogExt.d(getClass(), "getStagedActivity() Activity id: " + activityId);
        try {
            Dao<StagedActivityRecord, String> stagedDao = getDao(StagedActivityRecord.class);
            StagedActivityRecord record = stagedDao.queryForId(activityId);
            return StagedActivityRecord.toStagedActivity(record);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StagedActivity> loadAllStagedActivities() {
        LogExt.d(getClass(), "loadAllStagedActivities()");
        try {
            List<StagedActivity> results = new ArrayList<>();
            List<StagedActivityRecord> activityRecords = getDao(StagedActivityRecord.class).queryForAll();

            for (StagedActivityRecord record : activityRecords) {
                StagedActivity result = StagedActivityRecord.toStagedActivity(record);
                results.add(result);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStagedActivities() {
        LogExt.d(getClass(), "deleteStagedActivities()");
        try {
            List<StagedActivity> results = new ArrayList<>();
            List<StagedActivityRecord> activityRecords = getDao(StagedActivityRecord.class).queryForAll();

            for (StagedActivityRecord record : activityRecords) {
                deleteStagedActivity(record.stagedActivityId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveStagedEvent(StagedEvent event) {
        LogExt.d(getClass(), "saveStagedEvent() Activity id: " + event.getActivityId());
        try {
            getDao(StagedEventRecord.class).create(StagedEventRecord.toRecord(event));
            if (event.getResult() != null) {
                this.saveTaskResult(event.getResult());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStagedEvent(StagedEvent event) {
        LogExt.d(getClass(), "updateStagedEvent() Activity id: " + event.getActivityId() + " Event Id: " + event.getId());
        try {
            getDao(StagedEventRecord.class).update(StagedEventRecord.toRecord(event));
            if (event.getResult() != null) {
                this.saveTaskResult(event.getResult());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStagedEvent(int eventId) {
        LogExt.d(getClass(), "deleteStagedEvent() Event id: " + eventId);
        try {
            Dao<StagedEventRecord, Integer> stagedDao = getDao(StagedEventRecord.class);
            stagedDao.deleteById(eventId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StagedEvent> loadAllStagedEvents() {
        LogExt.d(getClass(), "loadAllStagedEvents()");
        try {
            List<StagedEventRecord> eventRecords = getDao(StagedEventRecord.class).queryForAll();
            return eventsFromRecords(eventRecords, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StagedEvent> loadStagedEvents(Date date, String activityId, StagedActivityState status) {
        LogExt.d(getClass(), "loadStagedEvents()");
        try {
            Dao<StagedEventRecord, ?> dao = getDao(StagedEventRecord.class);
            Map<String, Object> where = new HashMap<>();
            where.put(StagedEventRecord.ACTIVITY_ID_COLUMN, activityId);
            where.put(StagedEventRecord.STATUS_COLUMN, status);

            List<StagedEventRecord> eventRecords = dao.queryForFieldValues(where);
            return eventsFromRecords(eventRecords, date);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StagedEvent> loadStagedEvents(Date date, String activityId) {
        LogExt.d(getClass(), "loadStagedEvents()");
        try {
            Dao<StagedEventRecord, ?> dao = getDao(StagedEventRecord.class);
            Map<String, Object> where = new HashMap<>();
            where.put(StagedEventRecord.ACTIVITY_ID_COLUMN, activityId);

            List<StagedEventRecord> eventRecords = dao.queryForFieldValues(where);
            return eventsFromRecords(eventRecords, date);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StagedEvent> loadLiveStagedEvents(Date date) {
        LogExt.d(getClass(), "loadLiveStagedEvents()");
        try {
            Dao<StagedEventRecord, ?> dao = getDao(StagedEventRecord.class);
            List<StagedEventRecord> eventRecords = dao.queryForAll();
            return eventsFromRecords(eventRecords, date);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFutureStagedEvents(Date date) {
        LogExt.d(getClass(), "deleteFutureStagedEvents()");
        try {
            Dao<StagedEventRecord, ?> dao = getDao(StagedEventRecord.class);
            List<StagedEventRecord> eventRecords = dao.queryForAll();
            for (StagedEventRecord record : eventRecords) {
                if (date == null || !record.eventStartDate.before(date)) {
                    // Future Event
                    if (record.status == StagedActivityState.New) {
                        // Keep started or completed events
                        deleteStagedEvent(record.id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStagedEvents() {
        LogExt.d(getClass(), "deleteStagedEvents()");
        try {
            Dao<StagedEventRecord, ?> dao = getDao(StagedEventRecord.class);
            List<StagedEventRecord> eventRecords = dao.queryForAll();
            for (StagedEventRecord record : eventRecords) {
                deleteStagedEvent(record.id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<StagedEvent> eventsFromRecords(List<StagedEventRecord> eventRecords, Date date) {
        List<StagedEvent> results = new ArrayList<>();
        for (StagedEventRecord record : eventRecords) {
            if ((date == null || (!record.eventStartDate.after(date) && !record.eventEndDate.before(date)))
                    || record.status != StagedActivityState.New) {
                TaskResult taskResult = null;
                if (record.taskResultId != null) {
                    taskResult = loadLatestTaskResult(record.taskResultId);
                }
                StagedEvent event = StagedEventRecord.toStagedEvent(record, taskResult);
                StagedActivity activity = this.getStagedActivity(event.getActivityId());
                event.setActivity(activity);
                results.add(event);
            }
        }
        return results;
    }

}
