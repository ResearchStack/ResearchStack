package org.researchstack.backbone.step.active.recorder;

import android.Manifest;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by TheMDP on 2/20/17.
 */

public class LocationRecorderConfig extends RecorderConfig {

    public static final long DEFAULT_MIN_TIME = 100;  // 100 milliseconds minimal time change
    public static final long DEFAULT_LOCATION_DISTANCE = 0;    // no min distance

    private long minTime;
    private float minDistance;

    /** Default constructor used for serialization/deserialization */
    LocationRecorderConfig() {
        super();
    }

    public LocationRecorderConfig(String identifier) {
        super(identifier);
        minTime     = DEFAULT_MIN_TIME;
        minDistance = DEFAULT_LOCATION_DISTANCE;
    }

    /**
     * @param minTime per Android doc, minimum time interval between location updates, in milliseconds
     * @param minDistance per Android doc, minimum distance between location updates, in meters, no minimum if zero
     * @param identifier the recorder's identifier
     */
    public LocationRecorderConfig(String identifier, long minTime, float minDistance) {
        super(identifier);
        this.minTime = minTime;
        this.minDistance = minDistance;
    }

    @Override
    public Recorder recorderForStep(Step step, File outputDirectory) {
        return new LocationRecorder(minTime, minDistance, getIdentifier(), step, outputDirectory);
    }
}
