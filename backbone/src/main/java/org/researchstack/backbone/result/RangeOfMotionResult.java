package org.researchstack.backbone.result;

/**
 * Created by David Evans, 2019.
 */

public class RangeOfMotionResult extends Result {

    /**
     The angle (degrees) from the device reference position at the start position.
     */
    private double start;

    /**
     The angle (degrees) from the device reference position when the task finishes recording.
     */
    private double finish;

    /**
     The angle (degrees) from the device reference position at the minimum angle (e.g. when the knee is most bent, such as at the end of the task).
     */
    private double minimum;

    /**
     The angle (degrees) from the device reference position at the maximum angle (e.g. when the knee is most extended during the task).
     */
    private double maximum;

    /**
     The angle (degrees) passed through from the minimum angle to the maximum angle (e.g. from when the knee is most flexed to when it is most extended).
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

    public void setStart(double start) {
        this.start = start;
    }

    public double getFinish() { return finish; }

    public void setFinish(double finish) {
        this.finish = finish;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) { this.minimum = minimum; }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) { this.range = range; }

}
