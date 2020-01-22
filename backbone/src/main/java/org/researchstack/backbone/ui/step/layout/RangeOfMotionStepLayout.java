package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.factory.TaskFactory;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder;
import org.researchstack.backbone.utils.MathUtils;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
 *
 * The RangeOfMotionStepLayout is essentially the same as the TouchAnywhereStepLayout, except that it captures
 * device position (attitude) and calculates absolute device position (Euler/Tait-Bryan angles) in
 * degrees, relative to device and screen orientation - start, minimum, maximum, finish and range - once 
 * the screen is tapped and the step finishes.
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    protected BroadcastReceiver deviceMotionReceiver;;
    protected RelativeLayout layout;

    private boolean firstAttitudeCaptured = false;
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
        textTextview.setVisibility(View.VISIBLE);
        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressBarHorizontal.setVisibility(View.GONE);
        submitBar.setVisibility(View.GONE);

        setupOnClickListener();
    }

    public void setupOnClickListener() {
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

                        double updatedAngle = calculateShiftedAngleRelativeToStart(updatedDeviceAttitudeAsQuaternion); // this converts the current device attitude into an angle (degrees)
                        if (!firstAttitudeCaptured) { // we want setStartAttitude() to run once only
                            setStartAttitude(updatedDeviceAttitudeAsQuaternion);
                            firstAttitudeCaptured = true; // this prevents setStartAttitude() from being re-set after the first pass
                            }
                        if (updatedAngle < min) { // this captures the minimum angle (relative to start) that is recorded during the task
                            min = updatedAngle;
                            setMinimumAngle(min);
                            }
                        if (updatedAngle > max) { // this captures the maximum angle (relative to start) that is recorded during the task
                            max = updatedAngle;
                            setMaximumAngle(max);
                            }
                        setFinishAttitude(updatedDeviceAttitudeAsQuaternion); // this captures the last (finish) angle to be recorded
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
    public float[] getStartAttitude() {
        return startAttitude;
    }

    private void setStartAttitude(float[] startAttitude) {
        this.startAttitude = startAttitude;
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
    private double calculateShiftedAngleRelativeToStart(float[] attitudeUpdates) {
        double shifted_angle;
        float[] attitudeUpdatesRelativeToStart = getDeviceOrientationRelativeToStart(attitudeUpdates);
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(attitudeUpdatesRelativeToStart);
        shifted_angle = shiftDeviceAngleRange(unadjusted_angle);
        return shifted_angle;
    }

    public double getMinimumAngle() {
        return minimumAngle;
    }

    private void setMinimumAngle (double minimumAngle) {
        this.minimumAngle = minimumAngle;
    }

    public double getMaximumAngle() {
        return maximumAngle;
    }

    private void setMaximumAngle (double maximumAngle) {
        this.maximumAngle = maximumAngle;
    }

    /**
     * Method to shift default range of calculated angles for specific device or screen orientations,
     * if required, before being evaluated for maximum and minimum values. Should be overridden in
     * sub-classes where necessary.
     **/
    public double shiftDeviceAngleRange(double original_angle) {
        double shifted_angle;
        int orientation = getScreenOrientation();
        boolean targetAngleRange = ((original_angle >= 0) && (original_angle <= 180));
        if (targetAngleRange && (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)) {
            shifted_angle = 360 - Math.abs(original_angle);
        } else {
            shifted_angle = original_angle;
        }
        return shifted_angle;
    }

    /**
     * Method to calculate angles in degrees from the device attitude quaternion, as a function of
     * device orientation (portrait or landscape) or screen orientation (portrait, landscape, reverse
     * portrait or reverse landscape).
     **/
    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {
        double angle_in_degrees = 0;
        int screen_orientation = getScreenOrientation();
        int device_orientation = getResources().getConfiguration().orientation;

        if ((device_orientation == Configuration.ORIENTATION_LANDSCAPE)
                || (screen_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                || (screen_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForRoll (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3]));
        }
        else if ((device_orientation == Configuration.ORIENTATION_PORTRAIT)
                || (screen_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                || (screen_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForPitch (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3]));
        }
        return angle_in_degrees;
    }

    /**
     * Method to get all possible screen orientations, not just portrait and landscape, to ensure that
     * angles (in degrees) are calculated correctly. Android only provides portrait or landscape in
     * the Configuration class, so we must use the Surface class to report the angle of the onscreen
     * rendered image relative to the 'natural' device orientation (portrait on most devices), which
     * should be locked for the duration of the task recording within ActiveTaskActivity.
     **/
    public int getScreenOrientation() {
        Context appContext = getContext().getApplicationContext();
        WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int rotation = windowManager.getDefaultDisplay().getRotation();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int screen_orientation;

        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && (height > width)
                || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && (width > height)) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(ContentValues.TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(ContentValues.TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    screen_orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return screen_orientation;
    }

    /**
     * Method to obtain the updated device attitude relative to the start position by multiplying updates
     * of the attitude quaternion by the inverse of the quaternion that represents the start position.
     * This relativity is necessary if the task is being performed in different start positions, which
     * could result in angles that exceed the already shifted range.
     **/
    public float[] getDeviceOrientationRelativeToStart(float[] originalDeviceAttitude) {
        float[] relativeDeviceAttitude;
        float[] inverseOfStart = MathUtils.calculateInverseOfQuaternion(getStartAttitude());
        relativeDeviceAttitude = MathUtils.multiplyQuaternions(originalDeviceAttitude, inverseOfStart);
        return relativeDeviceAttitude;
    }

    /**
     * Method to obtain the device's attitude as a unit quaternion from the rotation vector sensor,
     * when it is available
     **/
    public float[] getDeviceAttitudeAsQuaternion(float[] rotation_vector) {
        float[] attitudeQuaternion = new float[4];
        SensorManager.getQuaternionFromVector(attitudeQuaternion, rotation_vector);
        return attitudeQuaternion;
    }

    @Override
    protected void stepResultFinished() {

        int orientation = getScreenOrientation();
        double start;
        double finish;
        double minimum;
        double maximum;
        double range;

        rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());

        /* Like iOS, when using quaternions via the rotation vector sensor in Android, the zero attitude
        {0,0,0,0} position is parallel with the ground (i.e. screen facing up). Hence, tasks in which
        portrait or landscape is the start position (i.e. perpendicular to the ground) require a 90 degree
        adjustment. These are set to report an absolute an angle between +270 and -90 degrees. These
        calculations will need to be overridden in tasks where this range is not appropriate.*/

        // Capture absolute start angle relative to device/screen orientation
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            start = 90 + getShiftedStartAngle();
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            start = -90 - getShiftedStartAngle();
        } else {
            start = 90 - getShiftedStartAngle();
        }
        rangeOfMotionResult.setStart(start);

        // Capture absolute finish angle relative to device/screen orientation
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            finish = 90 + getShiftedFinishAngle();
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            finish = -90 - getShiftedStartAngle();
        } else {
            finish = 90 - getShiftedFinishAngle();
        }
        rangeOfMotionResult.setFinish(finish);

        /* Because both knee and shoulder tasks both use pitch in the direction opposite to the device
        axes (i.e. right hand rule), maximum and minimum angles are reported the 'wrong' way around
        for these particular tasks when the device is in portrait or landscape mode */

        // Capture minimum angle relative to device/screen orientation
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            minimum = start + getMinimumAngle(); // TODO: untested
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            minimum = start + getMinimumAngle(); // TODO: untested
        } else {
            minimum = start - getMaximumAngle();
        }
        rangeOfMotionResult.setMinimum(minimum);

        // Capture maximum angle relative to device/screen orientation
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            maximum = start + getMaximumAngle(); // TODO: untested
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            maximum = start + getMaximumAngle(); // TODO: untested
        } else {
            maximum = start - getMinimumAngle();
        }
        rangeOfMotionResult.setMaximum(maximum);

        // Capture range as largest difference across all recorded angles
        range = Math.abs(maximum - minimum);
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
