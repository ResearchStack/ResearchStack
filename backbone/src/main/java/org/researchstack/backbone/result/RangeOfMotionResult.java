package org.researchstack.backbone.result;

/**
 * Created by David Evans, 2019.
 **/

public class RangeOfMotionResult extends Result {

    /**
     * Boolean variable to return true if the rotation vector sensor is available on the device.
     */
    private boolean hasRotationVector;

    /**
     * Boolean variable to return true if the accelerometer sensor is available on the device.
     */
    private boolean hasAccelerometer;

    /**
     The orientation of the device at the start position:
     ORIENTATION_UNDETECTABLE = -2;
     ORIENTATION_UNSPECIFIED = -1
     ORIENTATION_LANDSCAPE = 0
     ORIENTATION_PORTRAIT = 1
     ORIENTATION_REVERSE_LANDSCAPE = 2
     ORIENTATION_REVERSE_PORTRAIT = 3
     **/
    private int orientation;

    /**
     * Time duration (seconds) of the task
     */
    private double duration;

    /**
     The maximum acceleration (ms^-2) recorded along the x-axis during the task.
     */
    private double maximumAx;

    /**
     The maximum acceleration (ms^-2) recorded along the y-axis during the task.
     */
    private double maximumAy;

    /**
     The maximum acceleration (ms^-2) recorded along the z-axis during the task.
     */
    private double maximumAz;

    /**
     The maximum resultant acceleration (ms^-2) recorded during the task.
     */
    private double maximumAr;

    /**
     * Mean resultant acceleration
     */
    private double meanAr;

    /**
     * Standard deviation of resultant acceleration
     */
    private double SDAr;

    /**
     * The maximum jerk (ms^-3) recorded along the x-axis during the task.
     */
    private double maximumJx;

    /**
     * The maximum jerk (ms^-3) recorded along the y-axis during the task.
     */
    private double maximumJy;

    /**
     * The maximum jerk (ms^-3) recorded along the z-axis during the task.
     */
    private double maximumJz;

    /**
     * The maximum resultant jerk (ms^-3) recorded during the task.
     */
    private double maximumJr;

    /**
     * Mean resultant jerk; the time derivative of acceleration (ms^-3)
     */
    private double meanJerk;

    /**
     * Standard deviation of resultant jerk; the time derivative of acceleration (ms^-3)
     */
    private double SDJerk;

    /**
     * The time integral of resultant jerk, normalized by the total time of the task (ms^-1)
     */
    private double timeNormIntegratedJerk;

    /**
     * The angle (degrees) from the device reference position at the start position.
     */
    private double start;

    /**
     * The angle (degrees) from the device reference position when the task finishes recording.
     */
    private double finish;

    /**
     * The angle (degrees) from the device reference position at the minimum angle (e.g. when the knee is most bent, such as at the end of the task).
     */
    private double minimum;
    
    /**
     * The angle (degrees) from the device reference position at the maximum angle (e.g. when the knee is most extended during the task).
     */
    private double maximum;
    
    /**
     * The angle (degrees) passed through from the minimum angle to the maximum angle (e.g. from when the knee is most flexed to when it is most extended).
     */
    private double range;


    /* Default identifier for serilization/deserialization */
    RangeOfMotionResult() {
        super();
    }

    public RangeOfMotionResult(String identifier) {
        super(identifier);
    }

    public boolean getHasRotationVector() {
        return hasRotationVector;
    }

    public void setHasRotationVector(boolean hasRotationVector) {
        this.hasRotationVector = hasRotationVector;
    }

    public boolean getHasAccelerometer() {
        return hasAccelerometer;
    }

    public void setHasAccelerometer(boolean hasAccelerometer) {
        this.hasAccelerometer = hasAccelerometer;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getMaximumAx() {
        return maximumAx;
    }

    public void setMaximumAx(double maximumAx) {
        this.maximumAx = maximumAx;
    }

    public double getMaximumAy() {
        return maximumAy;
    }

    public void setMaximumAy(double maximumAy) {
        this.maximumAy = maximumAy;
    }

    public double getMaximumAz() {
        return maximumAz;
    }

    public void setMaximumAz(double maximumAz) {
        this.maximumAz = maximumAz;
    }

    public double getMaximumAr() {
        return maximumAr;
    }

    public void setMaximumAr(double maximumAr) {
        this.maximumAr = maximumAr;
    }

    public double getMeanAr() {
        return meanAr;
    }

    public void setMeanAr(double meanAr) {
        this.meanAr = meanAr;
    }

    public double getSDAr() {
        return SDAr;
    }

    public void setSDAr(double SDAr) {
        this.SDAr = SDAr;
    }

    public double getMaximumJx() {
        return maximumJx;
    }

    public void setMaximumJx(double maximumJx) {
        this.maximumJx = maximumJx;
    }

    public double getMaximumJy() {
        return maximumJy;
    }

    public void setMaximumJy(double maximumJy) {
        this.maximumJy = maximumJy;
    }

    public double getMaximumJz() {
        return maximumJz;
    }

    public void setMaximumJz(double maximumJz) {
        this.maximumJz = maximumJz;
    }

    public double getMaximumJr() {
        return maximumJr;
    }

    public void setMaximumJr(double maximumJr) {
        this.maximumJr = maximumJr;
    }
    public double getMeanJerk() {
        return meanJerk;
    }

    public void setMeanJerk(double meanJerk) {
        this.meanJerk = meanJerk;
    }

    public double getSDJerk() {
        return SDJerk;
    }

    public void setSDJerk(double SDJerk) {
        this.SDJerk = SDJerk;
    }

    public double getTimeNormIntegratedJerk() {
        return timeNormIntegratedJerk;
    }

    public void setTimeNormIntegratedJerk(double timeNormIntegratedJerk) {
        this.timeNormIntegratedJerk = timeNormIntegratedJerk;
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
