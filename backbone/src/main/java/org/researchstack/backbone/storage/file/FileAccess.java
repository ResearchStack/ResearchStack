package org.researchstack.backbone.storage.file;

import android.content.Context;

import org.researchstack.backbone.storage.file.aes.Encrypter;

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
public interface FileAccess {
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
     * @param context
     * @param fromPath
     * @param toPath
     */
    void moveData(Context context, String fromPath, String toPath);

    /**
     * See if we have the data.
     *
     * @param context Can be Application context, but we'll be careful not to store, so don't worry too much.
     * @param path    Path relative to the implementation's root store.  Must start with '/'.  No relative paths.
     * @return Well...
     */
    boolean dataExists(Context context, String path);

    void clearData(Context context, String path);

    /**
     * Set the encrypter. All data is passed through this object to be encrypted before save/decrypted
     * after loading. For no encryption, use an encrypter that passes the data back unchanged.
     *
     * @param encrypter Encrypter.
     */
    void setEncrypter(Encrypter encrypter);
}
