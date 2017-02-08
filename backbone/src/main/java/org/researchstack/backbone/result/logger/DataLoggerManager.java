package org.researchstack.backbone.result.logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by TheMDP on 2/7/17.
 */

public class DataLoggerManager {

    /**
     * Used to manage multiple DataLogger AsyncTasks at once
     * Will not consume any resources after about a minute of inactivity
     */
    private ExecutorService threadExecutor;

    /**
     * SharedPreferences are used to keep track of the DataLogger files' status
     * They can be dirty while being written, completed, and uploaded
     */
    private SharedPreferences sharedPrefs;
    private static final String SHARED_PREFS_KEY = "DataLoggerManagerSharedPrefs";
    private Gson gson; // used to serialize/deserialize DataLoggerFileStatus

    private static DataLoggerManager instance;

    @MainThread
    public static DataLoggerManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DataLoggerManager must be initialized with context before it can be accessed");
        }
        return instance;
    }

    @MainThread
    public static void initialize(Context context) {
        instance = new DataLoggerManager(context);
    }

    private DataLoggerManager(Context context) {
        // newCachedThreadPool will create new threads as needed, and it will
        // delete a Thread that hasn't been used for 60 seconds, that way,
        // if we accidentally leave one running because of a bug, it will destroy itself
        threadExecutor = Executors.newCachedThreadPool();
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @MainThread
    protected void startNewDataLoggerTask(DataLogger dataLogger, DataLoggerAsyncTask task) {
        createNewDataLoggerFileStatus(dataLogger);
        task.executeOnExecutor(threadExecutor);
    }

    @MainThread
    protected void dataLoggerTaskFinished(DataLogger dataLogger, Throwable error) {
        if (error != null) {
            deleteDataLoggerFileStatus(dataLogger);
        } else {
            completeDataLoggerFileStatus(dataLogger);
        }
    }

    /**
     * This creates a new status for the data logger that is is writing and considered "dirty"
     * @param dataLogger to operate upon
     */
    private void createNewDataLoggerFileStatus(DataLogger dataLogger) {
        DataLoggerFileStatus fileStatus = new DataLoggerFileStatus(
                dataLogger.getFile().getAbsolutePath(), true, false);
        String sharedPrefsKey = fileStatus.getSharedPrefsKey();
        String statusJson = gson.toJson(fileStatus);
        sharedPrefs.edit().putString(sharedPrefsKey, statusJson).apply();
    }

    /**
     * This updates the status of the file to not be dirty, but still is not uploaded
     * @param dataLogger to operate upon
     */
    private void completeDataLoggerFileStatus(DataLogger dataLogger) {
        DataLoggerFileStatus fileStatus = new DataLoggerFileStatus(
                dataLogger.getFile().getAbsolutePath(), false, false);
        String sharedPrefsKey = fileStatus.getSharedPrefsKey();
        String statusJson = gson.toJson(fileStatus);
        sharedPrefs.edit().putString(sharedPrefsKey, statusJson).apply();
    }

    /**
     * This method deletes the file and also deletes it's status
     * @param dataLogger to operate upon
     */
    private void deleteDataLoggerFileStatus(DataLogger dataLogger) {
        DataLoggerFileStatus fileStatus = new DataLoggerFileStatus(
                dataLogger.getFile().getAbsolutePath(), true, false);
        String sharedPrefsKey = fileStatus.getSharedPrefsKey();

        sharedPrefs.edit().remove(sharedPrefsKey).apply();
        boolean success = dataLogger.getFile().delete();

        if (!success) {
            Log.e(getClass().getCanonicalName(), "Failed to delete data logger file");
        }
    }

    /**
     * Used to keep track of all the DataLogger files and their status
     */
    protected class DataLoggerFileStatus {
        protected boolean isDirty;
        protected boolean isUploaded;
        protected String  filePath;

        DataLoggerFileStatus() {}

        DataLoggerFileStatus(String filePath, boolean isDirty, boolean isUploaded) {
            this.filePath = filePath;
            this.isDirty = isDirty;
            this.isUploaded = isUploaded;
        }

        /**
         * @return a unique value that can be used to safely store this class in SharedPreferences
         */
        protected String getSharedPrefsKey() {
            return String.valueOf(filePath.hashCode());
        }
    }
}
