package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;

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
import android.view.View;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.MathUtils;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
 *
 * The RangeOfMotionStepLayout is essentially the same as the ActiveStepLayout, except that it
 * calculates absolute device position angles in degrees: start, minimum, maximum, finish and range
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected SensorEvent sensorEvent;
    protected RelativeLayout layout;
    protected RangeOfMotionStep rangeOfMotionStep;
    private BroadcastReceiver deviceMotionReceiver;

    public float[] startAttitude = new float[4];
    public float[] finishAttitude = new float[4];
    public float[] updatedAttitude = new float[4];

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
                        float[] rotation_vector;

                        if (dataHolder.getW() != 0) {
                            rotation_vector = new float[] {dataHolder.getX(), dataHolder.getY(), dataHolder.getZ(), dataHolder.getW()};
                        } else {
                            rotation_vector = new float[] {dataHolder.getX(), dataHolder.getY(), dataHolder.getZ()};
                        }
                        getDeviceOrientationRelativeToStart(rotation_vector);
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


    /**
     * Method to obtain range-shifted Euler angle of first (start) device attitude,
     * relative to the zero position
     **/

    public double getShiftedStartAngle() {

        double absolute_start_angle;
        double raw_start_angle = getDeviceAngleInDegreesFromQuaternion(startAttitude);

        absolute_start_angle = shiftDeviceAngleRange(raw_start_angle);

        return absolute_start_angle;
    }


    /**
     * Method to obtain range-shifted Euler angle of final (finish) device attitude,
     * relative to the zero position
     **/

    public double getShiftedFinishAngle() {

        double absolute_finish_angle;
        double raw_finish_angle = getDeviceAngleInDegreesFromQuaternion(finishAttitude);

        absolute_finish_angle = shiftDeviceAngleRange(raw_finish_angle);

        return absolute_finish_angle;
    }


    /**
     * Methods to calculate minimum and maximum range-shifted Euler angles from the entire device
     * recording session
     **/

    public double getShiftedMinimumAngle() {

        double adjusted_angle = getShiftedDeviceAngleUpdates();

        return MathUtils.getMinimum(adjusted_angle);
    }


    public double getShiftedMaximumAngle() {

        double adjusted_angle = getShiftedDeviceAngleUpdates();

        return MathUtils.getMaximum(adjusted_angle);
    }


    /**
     * Method to obtain range-shifted Euler angles for all attitude updates, relative to the
     * start position
     **/

    public double getShiftedDeviceAngleUpdates() {

        double adjusted_angle;
        
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(updatedAttitude);

        adjusted_angle = shiftDeviceAngleRange(unadjusted_angle);

        return adjusted_angle;
    }


    /**
     * Method to shift default range of calculated angles from +/-180 degrees to -90 to +270 degrees,
     * to cover all achievable ranges of motion (which can exceed 180 degrees)
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
     * Method to calculate Euler angles from the device attitude quaternion, as a function of
     * screen orientation
     **/

    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {

        double angle_in_degrees = 0;
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getDeviceAttitudeAsQuaternion(sensorEvent.values);
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForRoll (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3])
            );
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getDeviceAttitudeAsQuaternion(sensorEvent.values);
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
     **/

    public void getDeviceOrientationRelativeToStart(float[] rotation_vector) {

        float[] deviceAttitude = getDeviceAttitudeAsQuaternion(rotation_vector);
        float[] inverseOfStart = getInverseOfStartAttitudeQuaternion();

        updatedAttitude = MathUtils.multiplyQuaternions(deviceAttitude, inverseOfStart);  // this holds the relative device attitude as a quaternion
    }


    /**
     * Method to obtain the inverse of the start (initial position) quaternion
     **/

    public float[] getInverseOfStartAttitudeQuaternion() {

        float[] inverseOfStartAttitudeQuaternion;

        inverseOfStartAttitudeQuaternion = MathUtils.calculateInverseOfQuaternion(startAttitude);

        return inverseOfStartAttitudeQuaternion;
    }


    /**
     * Methods to obtain and hold the quaternion representing the initial (start) position of the
     * device attitude when the step first initialises
     **/

    @Override
    public void createActiveStepLayout() {
        super.createActiveStepLayout();
        startAttitude = getDeviceAttitudeAsQuaternion(sensorEvent.values); // this holds the start attitude quaternion
    }


    /**
     * Methods to obtain and hold the quaternion representing the final (finish) position of the
     * device attitude when recording ends with the downward action during a tap of the screen
     **/

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        super.onTouchEvent(motionEvent);

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
            return true;
        }
        return false;
    }

    /*
    * Because we call this from onTouchEvent, this code will be executed for both normal touch
    * events and when the system calls this using Accessibility via performClick
    */

    @Override
    public boolean performClick() {
        super.performClick();

        finishAttitude = getDeviceAttitudeAsQuaternion(sensorEvent.values); // this holds the finish attitude quaternion

        return true;
    }


    /**
     * Method to obtain the device's attitude as a quaternion from the rotation vector sensor, when
     * it is available
     **/

    public float[] getDeviceAttitudeAsQuaternion(float[] rotation_vector) {

        float[] attitudeQuaternion = new float[4];
        int sensorType = sensorEvent.sensor.getType();

        if (sensorType == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getQuaternionFromVector(attitudeQuaternion, rotation_vector);
        }
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

        RangeOfMotionResult rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());
        
        /* In Android's zero orientation, the device is in portrait mode (i.e. perpendicular to the
        ground), whereas in iOS ResearchKit zero is parallel with the ground. Hence, there will be
        a 90 degree reported difference between these configurations from the same task */

        start = getShiftedStartAngle(); // reports absolute an angle between +270 and -90 degrees
        rangeOfMotionResult.setStart(start);

        /* Because the knee and shoulder tasks task uses pitch in the direction opposite to the
        original device axes (i.e. right hand rule), finish, maximum and minimum angles are
        reported the 'wrong' way around for the knee and shoulder tasks */

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
