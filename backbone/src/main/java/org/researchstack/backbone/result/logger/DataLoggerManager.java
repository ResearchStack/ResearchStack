package org.researchstack.backbone.result.logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by TheMDP on 2/7/17.
 *
 * The DataLoggerManager class is central station for the DataLogger file's life cycle
 *
 * It works by associating a "FileStatus" with each DataLogger file that is created
 * The FileStatus can be "dirty", which means it is either being created or has not reached
 * the stage of attempted upload.  Once the FileStatus is attempted upload, it will exist
 * until it is uploaded.  After it is uploaded, the necessary parts of the file needed by
 * the app can be stored, but the underlying data will be removed.
 */

public class DataLoggerManager {

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

    /**
     * @return true if DataLoggerManager is initialized with context, false if not and it needs to be
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    @MainThread
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new DataLoggerManager(context);
        }
    }

    private DataLoggerManager(Context context) {
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @MainThread
    protected void startNewDataLoggerTask(DataLogger dataLogger) {
        createNewDataLoggerFileStatus(dataLogger.getFile());
    }

    @MainThread
    protected void dataLoggerTaskFinished(DataLogger dataLogger, Throwable error) {
        if (error != null) {
            deleteFileStatus(dataLogger.getFile());
        }
    }

    /**
     * This creates a new status for the data logger that is is writing and considered "dirty"
     * @param file to operate upon
     */
    private void createNewDataLoggerFileStatus(File file) {
        DataLoggerFileStatus fileStatus = new DataLoggerFileStatus(
                fullFilePathAndName(file), true, false);
        String sharedPrefsKey = fileStatus.getSharedPrefsKey();
        String statusJson = gson.toJson(fileStatus);
        sharedPrefs.edit().putString(sharedPrefsKey, statusJson).apply();
    }

    /**
     * This method deletes the file and also deletes it's status
     * @param file to operate upon
     */
    public void deleteFileStatus(File file) {
        DataLoggerFileStatus fileStatus = new DataLoggerFileStatus(
                fullFilePathAndName(file), true, false);
        String sharedPrefsKey = fileStatus.getSharedPrefsKey();

        sharedPrefs.edit().remove(sharedPrefsKey).apply();
        boolean success = file.delete();

        if (!success) {
            Log.e(getClass().getCanonicalName(), "Failed to delete data logger file");
        }
    }

    /**
     * This will loop through all the data logger files that exist in storage and
     * remove any that are "dirty".  A "dirty" file is any file that has not been zipped
     * up and attempted to be uploaded yet.
     *
     * Examples of dirty data logger files are ones that were written before and app crashed
     * or was force closed, or files that were written and then the user cancelled the active task
     * that was responsible for creating them
     */
    public void deleteAllDirtyFiles() {
        Map<String, ?> fileStatusMap = sharedPrefs.getAll();

        List<String> prefKeysToDelete  = new ArrayList<>();
        List<String> filePathsToDelete = new ArrayList<>();

        // Loop through all the potential file status and find ones that are dirty
        for (String key : fileStatusMap.keySet()) {
            Object fileStatusObj = fileStatusMap.get(key);
            if (fileStatusObj instanceof String) {
                DataLoggerFileStatus fileStatus = gson.fromJson((String)fileStatusObj, DataLoggerFileStatus.class);
                if (fileStatus.isDirty) {
                    prefKeysToDelete.add(key);
                    filePathsToDelete.add(fileStatus.filePath);
                }
            }
        }

        // remove all dirty files
        for (String filePath : filePathsToDelete) {
            File file = new File(filePath);
            if (file.exists()) {
                boolean success = file.delete();
                if (!success) {
                    Log.e(getClass().getCanonicalName(), "Failed to delete dirty file " + filePath);
                }
            }
        }

        // remove dirty shared prefs
        SharedPreferences.Editor editor = sharedPrefs.edit();
        for (String prefsKey : prefKeysToDelete) {
            editor.remove(prefsKey);
        }
        editor.apply();
    }

    /**
     * Removes the "dirty" status from the files, as they are now complete and should
     * be kept around until they are successfully uploaded
     * @param fileList list of files to change status of
     */
    public void updateFileListToAttemptedUploadStatus(List<File> fileList) {
        Map<String, ?> fileStatusMap = sharedPrefs.getAll();
        Map<String, String> updateMap = new HashMap<>();
        // Loop through all the potential file status and find ones that are dirty
        for (String key : fileStatusMap.keySet()) {
            Object fileStatusObj = fileStatusMap.get(key);
            if (fileStatusObj instanceof String) {
                DataLoggerFileStatus fileStatus = gson.fromJson((String)fileStatusObj, DataLoggerFileStatus.class);
                for (File file : fileList) {
                    String filePath = fullFilePathAndName(file);
                    if (fileStatus.filePath.equals(filePath)) {
                        DataLoggerFileStatus updateStatus = new DataLoggerFileStatus(filePath, false, false);
                        updateMap.put(updateStatus.getSharedPrefsKey(), gson.toJson(updateStatus));
                    }
                }
            }
        }

        // Update the shared pref refs
        SharedPreferences.Editor editor = sharedPrefs.edit();
        for (String prefsKey : updateMap.keySet()) {
            editor.putString(prefsKey, updateMap.get(prefsKey));
        }
        editor.apply();
    }

    private String fullFilePathAndName(File file) {
        return file.getAbsolutePath() + file.getName();
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
