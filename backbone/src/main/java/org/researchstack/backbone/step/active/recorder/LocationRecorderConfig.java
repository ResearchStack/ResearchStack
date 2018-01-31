package org.researchstack.backbone.step.active.recorder;

import org.researchstack.backbone.step.Step;

import java.io.File;

public class LocationRecorderConfig extends RecorderConfig {
    public static final long DEFAULT_MIN_TIME = 100;  // 100 milliseconds minimal time change
    public static final long DEFAULT_LOCATION_DISTANCE = 0;    // no min distance
    public static final boolean DEFAULT_USES_RELATIVE_COORDINATE = false; // default to absolute coordinates

    private long minTime;
    private float minDistance;
    private boolean usesRelativeCoordinates;

    /** Default constructor used for serialization/deserialization */
    LocationRecorderConfig() {
        super();
    }

    public LocationRecorderConfig(String identifier) {
        this(identifier, DEFAULT_MIN_TIME, DEFAULT_LOCATION_DISTANCE);
    }

    /**
     * @param minTime per Android doc, minimum time interval between location updates, in milliseconds
     * @param minDistance per Android doc, minimum distance between location updates, in meters, no minimum if zero
     * @param identifier the recorder's identifier
     */
    public LocationRecorderConfig(String identifier, long minTime, float minDistance) {
        this(identifier, minTime, minDistance, DEFAULT_USES_RELATIVE_COORDINATE);
    }

    /** Private constructor, for use with builder. */
    private LocationRecorderConfig(
            String identifier, long minTime, float minDistance, boolean usesRelativeCoordinates) {
        super(identifier);
        this.minTime = minTime;
        this.minDistance = minDistance;
        this.usesRelativeCoordinates = usesRelativeCoordinates;
    }

    @Override
    public Recorder recorderForStep(Step step, File outputDirectory) {
        return new LocationRecorder(minTime, minDistance, usesRelativeCoordinates, getIdentifier(),
                step, outputDirectory);
    }

    /** LocationRecorderConfig builder. */
    public static class Builder {
        private String identifier;
        private long minTime = DEFAULT_MIN_TIME;
        private float minDistance = DEFAULT_LOCATION_DISTANCE;
        private boolean usesRelativeCoordinates = DEFAULT_USES_RELATIVE_COORDINATE;

        public Builder withIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withMinTime(long minTime) {
            this.minTime = minTime;
            return this;
        }

        public Builder withMinDistance(float minDistance) {
            this.minDistance = minDistance;
            return this;
        }

        /**
         * If this is set to true, the recorder will produce relative GPS coordinates, using the
         * user's initial position as zero in the relative coordinate system. If this is set to
         * false, the recorder will produce absolute GPS coordinates.
         */
        public Builder withUsesRelativeCoordinates(boolean usesRelativeCoordinates) {
            this.usesRelativeCoordinates = usesRelativeCoordinates;
            return this;
        }

        /** Builds the LocationRecorderConfig. */
        public LocationRecorderConfig build() {
            return new LocationRecorderConfig(identifier, minTime, minDistance,
                    usesRelativeCoordinates);
        }
    }
}
