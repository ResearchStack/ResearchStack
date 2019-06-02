package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Configuration;
import android.gesture.Gesture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.RecorderService;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.MathUtils;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
 *
 * The RangeOfMotionStepLayout is essentially the same as the ActiveStepLayout, except that it
 * calculates absolute device position in (Euler) angles: start, minimum, maximum, finish and range
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    private RelativeLayout layout;
    protected SensorEvent event;
    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    private BroadcastReceiver deviceMotionReceiver;
    public float[] startAttitude;
    public float[] finishAttitude;
    public double start;
    public double finish;
    public double minimum;
    public double maximum;
    public double range;

    public RangeOfMotionStepLayout(Context context) {
        super(context);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        setupRangeOfMotionViews();

        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setupRangeOfMotionViews() {

        layout = findViewById(R.id.rsb_active_step_layout_range_of_motion);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, null);
            }
        });
    }

    @Override
    protected void validateStep(Step step) {
        if (!(step instanceof RangeOfMotionStep)) {
            throw new IllegalStateException("RangeOfMotionStepLayout must have a RangeOfMotionStep");
        }
        rangeOfMotionStep = (RangeOfMotionStep) step;
        super.validateStep(step);
    }


    /* Not sure that we need any of this broadcast receiver section for device motion (attitude)

    @Override
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);
        deviceMotionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                if (DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION.equals(intent.getAction())) {
                    DeviceMotionRecorder.DeviceMotionUpdateHolder dataHolder =
                            DeviceMotionRecorder.getDeviceMotionUpdateHolder(intent);
                    if (dataHolder != null) {
                        if (RangeOfMotionStep.getNumberOfStepsPerLeg() > 0 &&
                                (dataHolder.getStepCount() >= RangeOfMotionStep.getNumberOfStepsPerLeg()))
                        {
                            // TODO: mdephillips 1/13/18
                            // we may want to move this functionality to the PedometerRecorder
                            // and having that signal to RecorderService to stop,
                            // since this StepLayout may be create/destroyed and miss this broadcast
                            RangeOfMotionStepLayout.super.stop();
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(AudioRecorder.BROADCAST_SAMPLE_ACTION);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(deviceMotionReceiver, intentFilter);
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(deviceMotionReceiver);
    }
     */


    // We need the following methods below:
    //1. Method for obtaining and holding the initial attitude as a quaternion (i.e. the first orientation when recording begins)
    //2. Method for obtaining and holding the final attitude as a quaternion (i.e. the last orientation when recording ends)
    //3. Method for obtaining and continually updating current attitude as quaternions
    //4. Method for calculating the inverse of a quaternion
    //5. Method for obtaining and holding the inverse of the start quaternion
    //6. Method for multiplying quaternions
    //7. Method to obtain the product of the inverse reference quaternion and the current quaternion to give current attitude relative to start position
    //8. Method to obtain the product of the inverse reference quaternion by the final quaternion to give current attitude relative to start position
    //9. Methods to calculate Euler angles from attitude quaternions
    //10. Method for obtaining relative quaternion to Euler angles, depending on device orientation (landscape or portrait)
    //11. Method to shift angle range from +/- 180 to +270 to -90 degrees
    //12. Methods to calculate minimum and maximum Euler angles from entire device recording
    //13. Methods to obtain final results of start, finish, minimum, maximum and range angles


    /**
     * Method to obtain range-shifted Euler angle of first (start) device attitude,
     * relative to the start position
     **/

    private double getShiftedStartAngle() {

        double raw_start_angle = getDeviceAngleInDegreesFromQuaternion(startAttitude);
        double absolute_start_angle;

        absolute_start_angle = shiftDeviceAngleRange(raw_start_angle);

        return absolute_start_angle;
    }


    /**
     * Method to obtain range-shifted Euler angle of final (finish) device attitude,
     * relative to the start position
     **/

    public double getShiftedFinishAngle() {

        float[] relativeFinishAttitude = multiplyFinishAttitudeByInverseOfStart();
        double raw_finish_angle = getDeviceAngleInDegreesFromQuaternion(relativeFinishAttitude);
        double absolute_finish_angle;

        absolute_finish_angle = shiftDeviceAngleRange(raw_finish_angle);

        return absolute_finish_angle;
    }


    /**
     * Methods to calculate minimum and maximum range-shifted Euler angles from the entire device
     * recording session
     **/


    public double getShiftedMinimumAngle() {

        double adjusted_angle = getShiftedDeviceAngleUpdates();

        return getMinimum(adjusted_angle);
    }

    public double getMinimum(double data) {

        double min = 0;

        if (data < min) {
            min = data;
        }
        return min;
    }


    public double getShiftedMaximumAngle() {

        double adjusted_angle = getShiftedDeviceAngleUpdates();

        return getMaximum(adjusted_angle);
    }

    public double getMaximum(double data) {

        double max = 0;

        if (data > max) {
            max = data;
        }
        return max;
    }


    /**
     * Method to obtain range-shifted Euler angle for all attitude updates, that are
     * relative to the start position
     **/

    public double getShiftedDeviceAngleUpdates() {

        float[] updatedAttitude = multiplyAllAttitudesByInverseOfStart();
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(updatedAttitude);
        double adjusted_angle;

        adjusted_angle = shiftDeviceAngleRange(unadjusted_angle);

        return adjusted_angle;
    }


    /**
     * Method to shift range of calculated angles from +/-180 degrees to -90 to +270 degrees,
     * to cover all achievable knee and shoulder ranges of motion
     **/

    public double shiftDeviceAngleRange(double original_angle) {

        double shifted_angle;
        boolean targetAngleRange = ((original_angle > 90) && (original_angle <= 180));

        if (targetAngleRange) {
            shifted_angle = Math.abs(original_angle) - 360;
            return shifted_angle;
        } else {
            shifted_angle = original_angle;
            return shifted_angle;
        }
    }


    /**
     * Method to calculate Euler angles from the device attitude quaternion, as a function of screen orientation
     **/

    private double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {

        int orientation = getResources().getConfiguration().orientation;
        double angle_in_degrees = 0;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            getDeviceAttitudeAsQuaternion();
            angle_in_degrees = Math.toDegrees(allOrientationsForRoll(quaternion[0], quaternion[1], quaternion[2], quaternion[3]));
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            getDeviceAttitudeAsQuaternion();
            angle_in_degrees = Math.toDegrees(allOrientationsForPitch(quaternion[0], quaternion[1], quaternion[2], quaternion[3]));
        }
        return angle_in_degrees;
    }


    /**
     * Methods to calculate Euler angles from device attitude quaternions
     **/
    
    public double allOrientationsForPitch(double w, double x, double y, double z) {

        double angle_in_rads;

        angle_in_rads = (Math.atan2(2.0 * (x * w + y * z), 1.0 - 2.0 * (x * x + z * z)));

        return angle_in_rads;
    }

    public double allOrientationsForRoll(double w, double x, double y, double z) {

        double angle_in_rads;

        angle_in_rads = (Math.atan2(2.0 * (y * w - x * z), 1.0 - 2.0 * (y * y + z * z)));

        return angle_in_rads;
    }

    //Yaw (azimuth) is not needed with the knee and shoulder tasks, but will be needed in other RoM tasks
    public double allOrientationsForYaw(double w, double x, double y, double z) {

        double angle_in_rads;

        angle_in_rads = (Math.asin(2.0 * (x * y - w * z)));

        return angle_in_rads;
    }


    /**
     * Method to multiply the final (finish) attitude quaternion by the inverse of the first (start)
     * quaternion to obtain the attitude of the finish position, relative to the start position
     **/

    public float[] multiplyFinishAttitudeByInverseOfStart() {

        float[] inverseOfStart = getInverseOfStartAttitudeQuaternion();
        float[] relativeFinishAttitudeQuaternion;

        relativeFinishAttitudeQuaternion = multiplyQuaternions(finishAttitude, inverseOfStart);

        return relativeFinishAttitudeQuaternion;
    }


    /**
     * Method to multiply every recorded attitude quaternion by the inverse of the quaternion
     * representing the start position to obtain the updated device attitude, relative to the
     * start position
     **/

    public float[] multiplyAllAttitudesByInverseOfStart() {

        float[] inverseOfStart = getInverseOfStartAttitudeQuaternion();
        float[] deviceAttitude = getDeviceAttitudeAsQuaternion(); // TODO: on every update
        float[] relativeAttitudeQuaternion;

        //if (deviceAttitude { //!= startAttitude) { // do we need to exclude startAttitude?
            relativeAttitudeQuaternion = multiplyQuaternions(deviceAttitude, inverseOfStart);
        //}
        return relativeAttitudeQuaternion;
    }


    /**
     * Method to multiply two quaternions (non-commutative)
     **/

    // for formula, see http://mathworld.wolfram.com/Quaternion.html
    public float[] multiplyQuaternions(float[] q1, float[] q2) {

        float[] productQuaternion = new float[4];

        productQuaternion[0] = (q1[0] * q2[0]) - (q1[1] * q2[1]) - (q1[2] * q2[2]) - (q1[3] * q2[3]);
        productQuaternion[1] = (q1[0] * q2[1]) + (q1[1] * q2[0]) + (q1[2] * q2[3]) - (q1[3] * q2[2]);
        productQuaternion[2] = (q1[0] * q2[2]) - (q1[1] * q2[3]) + (q1[2] * q2[0]) + (q1[3] * q2[1]);
        productQuaternion[3] = (q1[0] * q2[3]) + (q1[1] * q2[2]) - (q1[2] * q2[1]) + (q1[3] * q2[0]);

        return productQuaternion;
    }


    /**
     * Method to obtain the inverse of the start (initial position) quaternion
     **/

    public float[] getInverseOfStartAttitudeQuaternion() {

        float[] inverseOfStartAttitudeQuaternion;

        inverseOfStartAttitudeQuaternion = calculateInverseOfQuaternion(startAttitude);

        return inverseOfStartAttitudeQuaternion;
    }


    /**
     * Method to calculate the inverse (complex conjugate) of a quaternion
     **/

    // for formula, see http://mathworld.wolfram.com/Quaternion.html
    public float[] calculateInverseOfQuaternion(float[] originalQuaternion) {

        float[] inverseQuaternion = new float[4];

        inverseQuaternion[0] = originalQuaternion[0];
        inverseQuaternion[1] = (-1 * originalQuaternion[1]);
        inverseQuaternion[2] = (-1 * originalQuaternion[2]);
        inverseQuaternion[3] = (-1 * originalQuaternion[3]);

        return inverseQuaternion;
    }


    /**
     * Methods to obtain and hold the quaternion representing the initial (start) position of the
     * device attitude when the step initialises
     **/

    @Override
    public void createActiveStepLayout() {
        super.createActiveStepLayout();
        startAttitude = getDeviceAttitudeAsQuaternion(); // TODO: store this here using an instance variable?
    }


    /**
     * Methods to obtain and hold the quaternion representing the final (finish) position of the
     * device attitude when recording ends with a tap of the screen
     **/

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        super.onTouchEvent(motionEvent);
        finishAttitude = getDeviceAttitudeAsQuaternion(); // TODO: is this correct?
        return true;
    }


    /**
     * Method to obtain the device attitude's quaternion from the rotation vector
     **/

    public float[] getDeviceAttitudeAsQuaternion() {

        int type = event.sensor.getType();
        float[] attitudeQuaternion = new float[4];

        if (type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getQuaternionFromVector(attitudeQuaternion, event.values); // TODO: is this for every update?
        }
        return attitudeQuaternion;
    }


    @Override
    protected void stepResultFinished() {
        super.stepResultFinished();

        //now declared as instance variables
        //double start;
        //double finish;
        //double minimum;
        //double maximum;
        //double range;

        RangeOfMotionResult rangeOfMotionResult = new rangeOfMotionResult(rangeOfMotionStep.getIdentifier()); // based on TimedWalkStepLayout

        /* In Android's zero orientation, the device is in portrait mode (i.e. perpendicular to the
        ground); whereas in iOS ResearchKit zero is parallel with the ground */

        start = getShiftedStartAngle(); // reports absolute angle
        RangeOfMotionResult.getStart (getShiftedStartAngle()); // is this the appropriate format?

        /* Because the knee and shoulder tasks task uses pitch in the direction opposite to the
        original device axes (i.e. right hand rule), finish, maximum and minimum angles are
        reported the 'wrong' way around for the knee and shoulder tasks */

        finish = start - getShiftedFinishAngle(); // absolute angle; direction is opposite for knee and shoulder tasks
        minimum = start - getShiftedMaximumAngle(); // captured minimum angle will be opposite for knee and shoulder tasks
        maximum = start - getShiftedMinimumAngle(); // captured maximum angle will be opposite for knee and shoulder tasks
        range = Math.abs(maximum - minimum);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
    return stepResult
}
