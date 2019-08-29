package org.sagebionetworks.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.support.annotation.Nullable;

import org.sagebionetworks.researchstack.backbone.result.Result;

/**
 * Created by TheMDP on 2/5/17.
 *
 * The `RecorderListener` interface defines methods that the delegate of an `Recorder` object
 * should use to handle errors and log the completed results.
 *
 * This interface is implemented by `ActiveStepLayout`; your app should not need to implement it.
 */

public interface RecorderListener {
    /**
     * Tells the listener that the recorder has completed with the specified result.
     * Typically, this method is called once when recording is stopped.
     *
     * @param recorder        The generating recorder object.
     * @param result          The generated result.
     */
    void onComplete(Recorder recorder, Result result);

    /**
     * Tells the listener that recording failed.
     * Typically, this method is called once when the error occurred.
     *
     * @param recorder        The generating recorder object.
     * @param error           The error that occurred.
     */
    void onFail(Recorder recorder, Throwable error);

    /**
     * @return a valid Context for the recorder to broadcast status, null if not available
     */
    @Nullable
    Context getBroadcastContext();
}
