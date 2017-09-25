package org.researchstack.backbone.storage.database.staged;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.model.staged.MedStagedActivity;
import org.researchstack.backbone.model.staged.MedStagedActivityState;
import org.researchstack.backbone.model.staged.MedStagedEvent;
import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.storage.database.staged.records.MedStagedActivityRecord;
import org.researchstack.backbone.storage.database.staged.records.MedStagedEventRecord;
import org.researchstack.backbone.utils.LogExt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.touchlab.squeaky.dao.Dao;
import co.touchlab.squeaky.db.sqlcipher.SQLiteDatabaseImpl;
import co.touchlab.squeaky.table.TableUtils;

/**
 * Created by spiria on 14/9/17.
 */

public class StagedDatabaseHelper extends SqlCipherDatabaseHelper {

    public StagedDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, UpdatablePassphraseProvider passphraseProvider) {
        super(context, name, factory, version, passphraseProvider);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            super.onCreate(sqLiteDatabase);
            TableUtils.createTables(new SQLiteDatabaseImpl(sqLiteDatabase),
                    MedStagedActivityRecord.class,
                    MedStagedEventRecord.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveMedStagedActivity(MedStagedActivity activity) {
        LogExt.d(getClass(), "saveMedStagedActivity() id: " + activity.getId());
        try {
            getDao(MedStagedActivityRecord.class).createOrUpdate(MedStagedActivityRecord.toRecord(activity));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeMedStagedActivity(String activityId) {
        LogExt.d(getClass(), "removeMedStagedActivity() Activity id: " + activityId);
        try {
            getDao(MedStagedActivityRecord.class).deleteById(activityId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public MedStagedActivity getMedStagedActivity(String activityId) {
        LogExt.d(getClass(), "getMedStagedActivity() Activity id: " + activityId);
        try {
            MedStagedActivityRecord record = getDao(MedStagedActivityRecord.class).queryForId(activityId);
            return MedStagedActivityRecord.toMedStagedActivity(record);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MedStagedActivity> loadAllMedStagedActivities() {
        LogExt.d(getClass(), "loadAllMedStagedActivities()");
        try {
            List<MedStagedActivity> results = new ArrayList<>();
            List<MedStagedActivityRecord> activityRecords = getDao(MedStagedActivityRecord.class).queryForAll().list();

            for (MedStagedActivityRecord record : activityRecords) {
                MedStagedActivity result = MedStagedActivityRecord.toMedStagedActivity(record);
                results.add(result);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveMedStagedEvent(MedStagedEvent event) {
        LogExt.d(getClass(), "saveMedStagedEvent() Activity id: " + event.getActivity());
        try {
            getDao(MedStagedEventRecord.class).create(MedStagedEventRecord.toRecord(event));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMedStagedEvent(int recordId, MedStagedEvent event) {
        LogExt.d(getClass(), "updateMedStagedEvent() Activity id: " + event.getActivity());
        try {
            MedStagedEventRecord record = MedStagedEventRecord.toRecord(event);
            record.id = recordId;
            getDao(MedStagedEventRecord.class).update(record);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MedStagedEvent> loadAllMedStagedEvents() {
        LogExt.d(getClass(), "loadAllMedStagedEvents()");
        try {
            List<MedStagedEvent> results = new ArrayList<>();
            List<MedStagedEventRecord> eventRecords = getDao(MedStagedEventRecord.class).queryForAll().list();

            for (MedStagedEventRecord record : eventRecords) {
                MedStagedEvent result = MedStagedEventRecord.toMedStagedEvent(record);
                results.add(result);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MedStagedEvent> loadMedStagedEvents(Date date, String activityId, MedStagedActivityState status) {
        LogExt.d(getClass(), "loadMedStagedEvents()");
        try {
            List<MedStagedEvent> results = new ArrayList<>();
            Dao dao = getDao(MedStagedEventRecord.class);

            Map<String, Object> where = new HashMap<>();
            where.put(MedStagedEventRecord.ACTIVITY_ID_COLUMN, activityId);
            where.put(MedStagedEventRecord.STATUS_COLUMN, status);

            List<MedStagedEventRecord> eventRecords = dao.queryForFieldValues(where).list();

            for (MedStagedEventRecord record : eventRecords) {
                if (record.eventStartDate.before(date) && record.eventEndDate.after(date)) {
                    MedStagedEvent result = MedStagedEventRecord.toMedStagedEvent(record);
                    results.add(result);
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MedStagedEvent> loadMedStagedEventsByDate(Date date) {
        LogExt.d(getClass(), "loadMedStagedEvents()");
        try {
            List<MedStagedEvent> results = new ArrayList<>();
            Dao dao = getDao(MedStagedEventRecord.class);

            List<MedStagedEventRecord> eventRecords = dao.queryForAll().list();

            for (MedStagedEventRecord record : eventRecords) {
                if (record.eventStartDate.before(date) && record.eventEndDate.after(date)) {
                    MedStagedEvent result = MedStagedEventRecord.toMedStagedEvent(record);
                    results.add(result);
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MedStagedEvent> loadLiveMedStagedEvents(Date date) {
        LogExt.d(getClass(), "loadLiveMedStagedEvents()");
        try {
            List<MedStagedEvent> results = new ArrayList<>();
            Dao dao = getDao(MedStagedEventRecord.class);

            List<MedStagedEventRecord> eventRecords = dao.queryForAll().list();

            for (MedStagedEventRecord record : eventRecords) {
                if (record.eventStartDate.before(date) && record.eventEndDate.after(date)) {
                    MedStagedEvent result = MedStagedEventRecord.toMedStagedEvent(record);
                    results.add(result);
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
