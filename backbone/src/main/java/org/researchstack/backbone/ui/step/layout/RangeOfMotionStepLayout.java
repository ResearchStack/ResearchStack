package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.MathUtils;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
 *
 * The RangeOfMotionStepLayout is essentially the same as the ActiveStepLayout, except that it captures
 * device position (attitude) and calculates absolute device position (Euler/Tait-Bryan angles) in
 * degrees - start, minimum, maximum, finish and range - once the screen is tapped and the step finishes.
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    protected BroadcastReceiver deviceMotionReceiver;;
    protected RelativeLayout layout;

    private boolean startAttitudeCaptured = false;
    private float[] updatedDeviceAttitudeAsQuaternion = new float[4];
    private float[] startAttitude = new float[4];
    private float[] finishAttitude = new float[4];
    private double min;
    private double minimumAngle;
    private double max;
    private double maximumAngle;
    public double sensorFreq = 100.0; // Sensor frequency for device motion recorder

    public RangeOfMotionStepLayout(Context context) {
        super(context);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public RangeOfMotionStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    protected void validateStep(Step step) {
        if (!(step instanceof RangeOfMotionStep)) {
            throw new IllegalStateException("RangeOfMotionStepLayout must have a RangeOfMotionStep");
        }
        rangeOfMotionStep = (RangeOfMotionStep) step;
        super.validateStep(step);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        LayoutInflater.from(getContext())
                .inflate(R.layout.rsb_step_layout_range_of_motion, this, true);

        titleTextview.setVisibility(View.VISIBLE);
        textTextview.setVisibility(View.GONE); // This will need to be set to VISIBLE for RS framework
        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressBarHorizontal.setVisibility(View.GONE);
        submitBar.setVisibility(View.GONE);

        setupOnClickListener();
    }

    private void setupOnClickListener() {
        layout = findViewById(R.id.rsb_step_layout_range_of_motion);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onFinish();
            }
        });
    }

    @Override
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);

        deviceMotionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                // This obtains values from the rotation vector sensor via a broadcast from DeviceMotionRecorder
                if (DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION.equals(intent.getAction())) {
                    DeviceMotionRecorder.RotationVectorUpdateHolder dataHolder =
                            DeviceMotionRecorder.getRotationVectorUpdateHolder(intent);
                    if (dataHolder != null) {
                        float[] sensor_values;
                        if (dataHolder.getW() != 0.0f) {
                            sensor_values = new float[]{
                                    dataHolder.getX(),
                                    dataHolder.getY(),
                                    dataHolder.getZ(),
                                    dataHolder.getW()
                            };
                            updatedDeviceAttitudeAsQuaternion = getDeviceAttitudeAsQuaternion(sensor_values);
                        } else {
                            sensor_values = new float[]{
                                    dataHolder.getX(),
                                    dataHolder.getY(),
                                    dataHolder.getZ()
                            };
                            updatedDeviceAttitudeAsQuaternion = getDeviceAttitudeAsQuaternion(sensor_values);
                        }
                        double updatedAngle = calculateShiftedRelativeAngle(updatedDeviceAttitudeAsQuaternion); // this converts the current device attitude into an angle (degrees)
                        if (!startAttitudeCaptured) { // we want setStartAttitude() to run once only
                            setStartAttitude(updatedDeviceAttitudeAsQuaternion);
                            startAttitudeCaptured = true;
                            }
                        if (updatedAngle < min) { // this captures the minimum angle recorded during the task
                            min = updatedAngle;
                            setMinimumAngle(min);
                            }
                        if (updatedAngle > max) { // this captures the maximum angle recorded during the task
                            max = updatedAngle;
                            setMaximumAngle(max);
                            }
                        setFinishAttitude(updatedDeviceAttitudeAsQuaternion); // this captures the final (finish) angle of the task
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION);
        intentFilter.addAction(DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_KEY);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(deviceMotionReceiver, intentFilter);
    }

    @Override
    public void startBackgroundRecorderService() {
        super.startBackgroundRecorderService();

        // Commence recording via DeviceMotionRecorder
        Context appContext = getContext().getApplicationContext();
        DeviceMotionRecorder deviceMotionRecorder = new DeviceMotionRecorder (
                sensorFreq,
                TaskFactory.Constants.DeviceMotionRecorderIdentifier,
                rangeOfMotionStep,
                getOutputDirectory(appContext));
        deviceMotionRecorder.start(appContext);
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).
                unregisterReceiver(deviceMotionReceiver);
    }

    public void onFinish() {
        stepResultFinished();
        layout.setOnClickListener(null);
        stop(); // this should stop device motion recording and the broadcast
    }

    /**
     * Methods to obtain the initial (start) device attitude as a quaternion
     **/
    private void setStartAttitude(float[] startAttitude) {
        this.startAttitude = startAttitude;
    }

    public float[] getStartAttitude() {
        return startAttitude;
    }

    /**
     * Method to obtain range-shifted angle (degrees) of first (start) device attitude, relative to the zero position
     **/
    public double getShiftedStartAngle() {
        double shifted_start_angle;
        double raw_start_angle = getDeviceAngleInDegreesFromQuaternion(getStartAttitude());
        shifted_start_angle = shiftDeviceAngleRange(raw_start_angle);
        return shifted_start_angle;
    }

    /**
     * Methods to obtain the final (finish) device attitude as a quaternion
     **/
    public float[] getFinishAttitude() {
        return finishAttitude;
    }

    private void setFinishAttitude(float[] finishAttitude) {
        this.finishAttitude = finishAttitude;
    }

    /**
     * Method to obtain range-shifted angle (degrees) of final (finish) device attitude, relative to the zero position
     **/
    public double getShiftedFinishAngle() {
        double shifted_finish_angle;
        double raw_finish_angle = getDeviceAngleInDegreesFromQuaternion(getFinishAttitude());
        shifted_finish_angle = shiftDeviceAngleRange(raw_finish_angle);
        return shifted_finish_angle;
    }

    /**
     * Methods to obtain and calculate the minimum and maximum range-shifted angle (degrees), calculated
     * for all device motion updates during recording, relative to the start position
     **/
    private double calculateShiftedRelativeAngle(float[] attitudeUpdates) {
        double shifted_angle;
        float[] attitudeUpdatesRelativeToStart = getDeviceOrientationRelativeToStart(attitudeUpdates);
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(attitudeUpdatesRelativeToStart);
        shifted_angle = shiftDeviceAngleRange(unadjusted_angle);
        return shifted_angle;
    }

    private void setMinimumAngle (double minimumAngle) {
        this.minimumAngle = minimumAngle;
    }

    public double getMinimumAngle() {
        return minimumAngle;
    }

    private void setMaximumAngle (double maximumAngle) {
        this.maximumAngle = maximumAngle;
    }

    public double getMaximumAngle() {
        return maximumAngle;
    }

    /**
     * Method to shift default range of calculated angles, if required, before being evaluated for maximum
     * and minimum values. Default setting is to return the original angle value, which creates an available
     * range of +270 and -90, allowing for the current 90 degree adjustment in the stepResultFinished() method
     * for tasks that negin in a vertical device orientation. This range is suitable for the knee and shoulder
     * tasks but may not be suitable for other device motion tasks (e.g. if a range of +/-180 degrees is required).
     * If so, the method can be overridden and a version of the following conditional statement can be used:
     *
     *     boolean targetAngleRange = ((original_angle < -90) && (original_angle >= -180));
     *     double shifted_angle;
     *     if (targetAngleRange) {
     *         shifted_angle = Math.abs(original_angle) - 360;
     *         } else {
     *         shifted_angle = original_angle;
     *         }
     *         return shifted_angle;
     **/
    public double shiftDeviceAngleRange(double original_angle) {
        return original_angle;
    }

    /**
     * Method to calculate angles in degrees from the device attitude quaternion, as a function of
     * screen orientation
     **/
    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {
        double angle_in_degrees = 0;
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForRoll (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3])
            );
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForPitch (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3])
            );
        }
        return angle_in_degrees;
    }

    /**
     * Method to multiply updates of the attitude quaternion by the inverse of the quaternion
     * that represents the start position, to obtain the updated device attitude relative to the
     * start position (this relativity is necessary if the task is being performed in different
     * start positions, which could result in angles that exceed the already shifted range)
     *
     **/
    public float[] getDeviceOrientationRelativeToStart(float[] originalDeviceAttitude) {
        float[] relativeDeviceAttitude;
        float[] inverseOfStart = MathUtils.calculateInverseOfQuaternion(getStartAttitude());
        relativeDeviceAttitude = MathUtils.multiplyQuaternions(originalDeviceAttitude, inverseOfStart);
        return relativeDeviceAttitude;
    }

    /**
     * Method to obtain the device's attitude as a quaternion from the rotation vector sensor, when
     * it is available
     **/
    public float[] getDeviceAttitudeAsQuaternion(float[] rotation_vector) {
        float[] attitudeQuaternion = new float[4];
        SensorManager.getQuaternionFromVector(attitudeQuaternion, rotation_vector);
        return attitudeQuaternion;
    }

    @Override
    protected void stepResultFinished() {
        super.stepResultFinished();

        double start;
        double finish;
        double minimum;
        double maximum;
        double range;

        rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());

        /* Like iOS's ResearchKit, when using quaternions via the rotation vector sensor in Android,
        the zero orientation {0,0,0,0} position is parallel with the ground (i.e. screen facing up).
        Hence, tasks in which portrait is the start position (i.e. perpendicular to the ground) require
        a 90 degree adjustment. In addition, the sign of angles calculated from the quaternion need to
        be reversed for the knee and shoulder tasks, in which the device will be rotated in the direction
        opposite to that of the device axes (i.e. right hand rule). */

        start = 90 - getShiftedStartAngle(); // reports absolute an angle between +270 and -90 degrees
        rangeOfMotionResult.setStart(start);

        finish = 90 - getShiftedFinishAngle(); // absolute angle; direction related to start is opposite for knee and shoulder tasks
        rangeOfMotionResult.setFinish(finish);

        /* Because both knee and shoulder tasks both use pitch in the direction opposite to the device
        axes (i.e. right hand rule), maximum and minimum angles are reported the 'wrong' way around
        for these particular tasks. These calculations will need to be overriden in tasks where this
        is not the case */

        minimum = start - getMaximumAngle(); // captured minimum angle will be opposite for knee and shoulder tasks
        rangeOfMotionResult.setMinimum(minimum);

        maximum = start - getMinimumAngle(); // captured maximum angle will be opposite for knee and shoulder tasks
        rangeOfMotionResult.setMaximum(maximum);

        range = Math.abs(maximum - minimum); // largest range across all recorded angles
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}

