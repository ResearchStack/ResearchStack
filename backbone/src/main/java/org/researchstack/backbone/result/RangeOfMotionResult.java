package org.researchstack.backbone.result;

/**
 * Created by David EVans, 2019.
 */

public class RangeOfMotionResult extends Result {
    /**
    The angle (degrees) from the device reference position at the start position.
     */
    //private double distanceInMeters;
    private double start;

    /**
    The angle (degrees) from the device reference position when the task finishes recording.
     */
    //private int timeLimit;
    private double finish;

    /**
    The angle (degrees) from the device reference position at the minimum angle (e.g. when the knee is most bent, such as at the end of the task).
     */
    //private int duration;
    private double minimum;
    
    /**
    The angle (degrees) from the device reference position at the maximum angle (e.g. when the knee is extended).
     */
    private double maximum;
    
    /**
     The angle (degrees) passed through from the start position to the maximum angle (e.g. from when the knee is flexed to when it is extended).
     */
    private double range;

    /* Default identifier for serilization/deserialization */
    RangeOfMotionResult() {
        super();
    }

    public RangeOfMotionResult(String identifier) {
        super(identifier);
    }

    public double getStart() {
        return start;
    }

    //public void setDistanceInMeters(double distanceInMeters) {
    //    this.distanceInMeters = distanceInMeters;
    //}

    public int getFinish() {
        return finish;
    }

    //public void setTimeLimit(int timeLimit) {
    //    this.timeLimit = timeLimit;
    //}

    public int getMinimum() {
        return minimum;
    }

    //public void setDuration(int duration) {
    //    this.duration = duration;
    //}
    
    public int getMaximum() {
        return maximum;
    }
    
    public int getRange() {
        return range;
    }
}
