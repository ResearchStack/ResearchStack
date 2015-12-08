package co.touchlab.researchstack.core;

import android.content.Context;

import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.file.FileAccess;

public abstract class StorageManager
{
    // TODO find a better place for this, maybe only use it for Bridge
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    protected static AppDatabase appDatabase;

    /**
     * File access interface.  Should either be clear, or used with standard encryption.  If you
     * need something funky, override.
     * @return
     */
    protected static FileAccess fileAccess;

    public static void init(FileAccess fileAccess, AppDatabase appDatabase)
    {
        StorageManager.fileAccess = fileAccess;
        StorageManager.appDatabase = appDatabase;
    }

    public static AppDatabase getAppDatabase()
    {
        return appDatabase;
    }
    public static FileAccess getFileAccess()
    {
        return fileAccess;
    }
}
