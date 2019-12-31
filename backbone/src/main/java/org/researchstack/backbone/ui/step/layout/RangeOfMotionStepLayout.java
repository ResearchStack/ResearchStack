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
 * The RangeOfMotionStepLayout is essentially the same as the TouchAnywhereStepLayout, except that
 * it calculates absolute device position Euler/Tait-Bryan angles in degrees: start, minimum, maximum,
 * finish and range, once the screen is tapped and the step finishes.
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    protected BroadcastReceiver deviceMotionReceiver;;
    protected RelativeLayout layout;
    private SensorEvent sensorEvent;

    private boolean isRecordingComplete = false;
    private boolean alreadyExecuted = false;
    public float[] currentDeviceAttitude = new float[4];
    public float[] startAttitude = new float[4];
    public float[] finishAttitude = new float[4];

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
        textTextview.setVisibility(View.VISIBLE);
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

                // This captures the quaternion representing the final (finish) position of the
                // device attitude once the recorder service has begun
                finishAttitude = getDeviceAttitudeAsQuaternion(getDeviceOrientationRelativeToStart());

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
                if (DeviceMotionRecorder.BROADCAST_DEVICE_MOTION_UPDATE_ACTION.equals(intent.getAction())) {
                    DeviceMotionRecorder.DeviceMotionUpdateHolder dataHolder =
                            DeviceMotionRecorder.getDeviceMotionUpdateHolder(intent);
                    if (dataHolder != null) {
                        float[] sensor_values;
                        //int sensorType = sensorEvent.sensor.getType();
                        //if (Sensor.TYPE_ROTATION_VECTOR == sensorType) {
                            if (dataHolder.getW() != 0) {
                                sensor_values = new float[] {
                                        dataHolder.getX(),
                                        dataHolder.getY(),
                                        dataHolder.getZ(),
                                        dataHolder.getW()
                                };
                            } else {
                                sensor_values = new float[] {
                                        dataHolder.getX(),
                                        dataHolder.getY(),
                                        dataHolder.getZ()
                                };
                            }
                            currentDeviceAttitude = getDeviceAttitudeAsQuaternion(sensor_values); // this should capture the current device attitude

                            if(!alreadyExecuted) { // getStartAttitude() should run once only
                                getStartAttitude();
                                alreadyExecuted = true;
                            }
                        //}
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(DeviceMotionRecorder.BROADCAST_DEVICE_MOTION_UPDATE_ACTION);
        intentFilter.addAction(DeviceMotionRecorder.BROADCAST_DEVICE_MOTION_UPDATE_KEY);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(deviceMotionReceiver, intentFilter);
    }

    @Override
    public void startBackgroundRecorderService() {
        super.startBackgroundRecorderService();

        // Commence recording via DeviceMotionRecorder
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);
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
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(deviceMotionReceiver);
    }

    public void onFinish() {
        isRecordingComplete = true;
        stepResultFinished();
        layout.setOnClickListener(null);
        stop();
    }

    
    /**
     * Method to obtain the initial (start) device attitude as a quaternion
     **/
    public void getStartAttitude() {
        startAttitude = getDeviceAttitudeAsQuaternion(currentDeviceAttitude);
    }
    
    
    /**
     * Method to obtain range-shifted angle of first (start) device attitude, relative to the zero position
     **/
    public double getShiftedStartAngle() {

        double absolute_start_angle;
        double raw_start_angle = getDeviceAngleInDegreesFromQuaternion(startAttitude);

        absolute_start_angle = shiftDeviceAngleRange(raw_start_angle);

        return absolute_start_angle;
    }


    /**
     * Method to obtain range-shifted angle of final (finish) device attitude, relative to the zero position
     **/
    public double getShiftedFinishAngle() {

        double absolute_finish_angle;
        double raw_finish_angle = getDeviceAngleInDegreesFromQuaternion(finishAttitude);

        absolute_finish_angle = shiftDeviceAngleRange(raw_finish_angle);

        return absolute_finish_angle;
    }


    /**
     * Method to calculate minimum range-shifted angles from the entire device recording session
     **/
    public double getShiftedMinimumAngle() {

        double adjusted_angle = getShiftedDeviceAngleUpdates();

        return MathUtils.getMinimum(adjusted_angle);
    }


    /**
     * Method to calculate maximum range-shifted angles from the entire device recording session
     **/
    public double getShiftedMaximumAngle() {

        double adjusted_angle = getShiftedDeviceAngleUpdates();

        return MathUtils.getMaximum(adjusted_angle);
    }


    /**
     * Method to obtain range-shifted angles for all attitude updates, relative to the start position
     **/
    public double getShiftedDeviceAngleUpdates() {

        double adjusted_angle;
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(getDeviceOrientationRelativeToStart());

        adjusted_angle = shiftDeviceAngleRange(unadjusted_angle);

        return adjusted_angle;
    }


    /**
     * Method to shift default range of calculated angles from +/-180 degrees to -90 to +270 degrees,
     * to cover all achievable ranges of motion (which can exceed 180 degrees but should not fall
     * short of 90 degrees)
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
    public float[] getDeviceOrientationRelativeToStart() {

        float[] inverseOfStart = getInverseOfStartAttitudeQuaternion();
        float[] currentRelativeDeviceAttitude;

        currentRelativeDeviceAttitude = MathUtils.multiplyQuaternions(currentDeviceAttitude, inverseOfStart);

        return currentRelativeDeviceAttitude;
    }


    /**
     * Method to obtain the inverse of the quaternion that represents the initial (start) device position
     **/
    public float[] getInverseOfStartAttitudeQuaternion() {

        float[] inverseOfStartAttitudeQuaternion;

        inverseOfStartAttitudeQuaternion = MathUtils.calculateInverseOfQuaternion(startAttitude);

        return inverseOfStartAttitudeQuaternion;
    }


    /**
     * Method to obtain the device's attitude as a quaternion from the rotation vector sensor
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

        /* In Android's zero orientation {0,0,0,0}, the device is in portrait mode (i.e. screen perpendicular
        to the ground), whereas in iOS's ResearchKit zero orientation is parallel with the ground
        (i.e. screen facing up). Hence, there will be a 90 degree reported difference between these
        configurations from the same task */

        start = getShiftedStartAngle(); // reports absolute an angle between +270 and -90 degrees
        rangeOfMotionResult.setStart(start);

        /* Because the knee and shoulder tasks both use pitch in the direction opposite to the original
        device axes (i.e. right hand rule), finish, maximum and minimum angles are reported the 'wrong'
        way around for these particular tasks. These calculations will need to be overriden in tasks
        where this is not the case */

        finish = getShiftedFinishAngle(); // absolute angle; direction related to start is opposite for knee and shoulder tasks
        rangeOfMotionResult.setFinish(finish);

        minimum = start - getShiftedMaximumAngle(); // captured minimum angle will be opposite for knee and shoulder tasks
        rangeOfMotionResult.setMinimum(minimum);

        maximum = start - getShiftedMinimumAngle(); // captured maximum angle will be opposite for knee and shoulder tasks
        rangeOfMotionResult.setMaximum(maximum);

        range = Math.abs(maximum - minimum); // largest range across all recorded angles
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
