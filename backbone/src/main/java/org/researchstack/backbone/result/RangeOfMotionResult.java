package org.researchstack.backbone.result;

/**
 * Created by David Evans, 2019.
 */

public class RangeOfMotionResult extends Result {
    /**
    The angle (degrees) from the device reference position at the start position.
     */
    public double start;

    /**
    The angle (degrees) from the device reference position when the task finishes recording.
     */
    public double finish;

    /**
    The angle (degrees) from the device reference position at the minimum angle (e.g. when the knee is most bent, such as at the end of the task).
     */
    public double minimum;
    
    /**
    The angle (degrees) from the device reference position at the maximum angle (e.g. when the knee is extended).
     */
    public double maximum;
    
    /**
     The angle (degrees) passed through from the start position to the maximum angle (e.g. from when the knee is flexed to when it is extended).
     */
    public double range;


    /* Default identifier for serilization/deserialization */
    RangeOfMotionResult() {
        super();
    }

    public RangeOfMotionResult(String identifier) {
        super(identifier);
    }

    public void getStart(double start) {
        this.start = start;
    }

    public void getFinish(double finish) {
        this.finish = finish;
    }


    public void getMinimum(double minimum) {
        this.minimum = minimum;
    }

    
    public void getMaximum(double maximum) {
        this.maximum = maximum;
    }
    
    public int getRange(double range) {
        this.range = range;
    }
}
