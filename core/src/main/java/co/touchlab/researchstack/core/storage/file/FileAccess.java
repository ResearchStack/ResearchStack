package co.touchlab.researchstack.core.storage.file;
import android.content.Context;

/**
 * Generic file access contract for the app.  Depending on the type, there may need to be auth
 * input screens shown to the user.  Also, its possible to implement completely cloud based
 * storage, so the developer will need to be aware of the detail implementations to some degree.
 * <p>
 * Because some storage will need to show auth info to the user before interaction can start,
 * the normal Android lifecycle of Fractivities will need to be modified a bit.  In general, code
 * will hit 'onCreate', which will check for live file access, and if there's an issue, show
 * a different UI to the user to resolve.  A new part of the lifecycle, onDataReady (or similar)
 * will need to be added.  This intended to be run whenever auth is complete, but may happen outside
 * of the standard onCreate/onResume callbacks.
 * <p>
 * Its assumed that in most cases, once the app has authed, file access should be readily available.
 * If that is NOT the case, for example if a google auth expires, the developer will have to implement
 * an exception case flow.  If rare, simply letting the app crash and auth on a restart
 * might be sufficient, but planning for crashes isn't the most elegant of solutions ;)
 * <p>
 * This is highly volatile at the moment.  The interface may change dramatically.
 * <p>
 * Created by kgalligan on 11/24/15.
 */
public interface FileAccess
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
    void initFileAccess(Context context);

    /**
     * Registers a listener.  If you want to read/write data, you'll need to implement this to know when
     * file access is ready.
     *
     * @param fileAccessListener
     */
    void register(FileAccessListener fileAccessListener);

    /**
     * Guess what this does.  Yes, you'll need to call it if you called register or you'll have memory leaks
     * and possibly crashes on callbacks to dead clients.
     *
     * @param fileAccessListener
     */
    void unregister(FileAccessListener fileAccessListener);

    /**
     * Save data.  Do not call this before you init or you'll get an exception.  Also, networked
     * storage has the high probability of network based exceptions.  Use extra caution in those
     * situations.
     *
     * @param context Can be Application context, but we'll be careful not to store, so don't worry too much.
     * @param path    Path relative to the implementation's root store.  Must start with '/'.  No relative paths.
     * @param data    Byte array.  May implement streams in the future if all implementations support it.
     */
    void writeData(Context context, String path, byte[] data);

    /**
     * Read data.  Do not call this before you init or you'll get an exception.  Also, networked
     * storage has the high probability of network based exceptions.  Use extra caution in those
     * situations.  If the path does not exist, you'll get an exception, not a null.  Just FYI.  That
     * may change.
     *
     * @param context Can be Application context, but we'll be careful not to store, so don't worry too much.
     * @param path    Path relative to the implementation's root store.  Must start with '/'.  No relative paths.
     */
    byte[] readData(Context context, String path);

    /**
     * Write string.  Really just a convenience method that calls writeData.  Default encoding is UTF8.
     *
     * @param context
     * @param path
     * @param data
     */
    void writeString(Context context, String path, String data);

    /**
     * Read string.  Really just a convenience method that calls readData.  Default encoding is UTF8.
     *
     * @param context
     * @param path
     * @return
     */
    String readString(Context context, String path);

    /**
     * See if we have the data.
     *
     * @param context Can be Application context, but we'll be careful not to store, so don't worry too much.
     * @param path    Path relative to the implementation's root store.  Must start with '/'.  No relative paths.
     * @return Well...
     */
    boolean dataExists(Context context, String path);

    void clearData(Context context, String path);

    DataAccessAuthenticator getDataAccessAuthenticator();

    interface DataAccessAuthenticator
    {
       void logDataAccessTime();
       void runCheckForDataAccess(Context context);
    }
}
