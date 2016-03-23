package org.researchstack.backbone.storage.file.auth;
import android.content.Context;

import org.researchstack.backbone.storage.file.StorageAccessListener;

public interface DataAccess
{
    /**
     * Check and init file access.  If there is an auth flow, the FileAccess implementation is
     * required to handle it.  Once file access is ready, registered listeners will be notified
     * via the FileAccessListener.  This is true whether there is an auth flow or if the system
     * is already prepared.  The callback will happen on the main thread looper, after all scheduled
     * events, which will include (in the standard case) onCreate/onStart/onResume, and the equivalent
     * Fragment callbacks.  This is to ensure one path, and reduce edge case race conditions.
     *
     * @param context Calling context.  If you are calling from an Activity, its best to send it and
     *                not the application context, because we may want to start new activities, and
     *                the application context can only do so with a new task, which will possibly complicate
     *                the back stack.  We won't do anything weird that causes memory leaks.  Promise ;)
     */
    void requestStorageAccess(Context context);

    /**
     * Registers a listener.  If you want to read/write data, you'll need to implement this to know when
     * file access is ready.
     *
     * @param storageAccessListener
     */
    void register(StorageAccessListener storageAccessListener);

    /**
     * Guess what this does.  Yes, you'll need to call it if you called register or you'll have memory leaks
     * and possibly crashes on callbacks to dead clients.
     *
     * @param storageAccessListener
     */
    void unregister(StorageAccessListener storageAccessListener);
}
