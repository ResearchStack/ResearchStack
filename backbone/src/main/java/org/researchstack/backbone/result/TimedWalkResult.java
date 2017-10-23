package org.researchstack.backbone.result;

/**
 * Created by TheMDP on 2/22/17.
 */

public class TimedWalkResult extends Result {
    /**
     The timed walk distance in meters.
     */
    private double distanceInMeters;

    /**
     The time limit to complete the trials.
     */
    private int timeLimit;

    /**
     The trial duration (that is, the time taken to do the walk).
     */
    private int duration;

    /* Default identifier for serilization/deserialization */
    TimedWalkResult() {
        super();
    }

    public TimedWalkResult(String identifier) {
        super(identifier);
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
