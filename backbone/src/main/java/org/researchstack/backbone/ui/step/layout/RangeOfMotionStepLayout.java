package org.researchstack.backbone.ui.step.layout;

import java.lang.Math;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.factory.TaskFactory;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder;
import org.researchstack.backbone.utils.MathUtils;

import static java.lang.Double.NaN;

/**
 * Created by David Evans, Simon Hartley, Laurence Hurst, David Jimenez, 2019.
 *
 * The behaviour of the RangeOfMotionStepLayout is essentially the same as the TouchAnywhereStepLayout, 
 * except that it captures device position (attitude) and calculates absolute device position (Euler/Tait-Bryan angles) 
 * in degrees, relative to device orientation - start, minimum, maximum, finish and range - once the 
 * screen is tapped and the step finishes.
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    protected BroadcastReceiver deviceMotionReceiver;;
    protected RelativeLayout layout;
    protected SensorManager sensorManager;

    private boolean firstOrientationCaptured = false;
    private boolean firstAttitudeCaptured = false;
    private int orientation;
    private int initialOrientation;
    private float[] updatedDeviceAttitudeAsQuaternion = new float[4];
    private float[] startAttitude = new float[4];
    private float[] finishAttitude = new float[4];
    private double min;
    private double minimumAngle;
    private double max;
    private double maximumAngle;

    public static final int ORIENTATION_UNDETECTABLE = -2;
    public static final int ORIENTATION_UNSPECIFIED = -1;
    public static final int ORIENTATION_LANDSCAPE = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_REVERSE_LANDSCAPE = 2;
    public static final int ORIENTATION_REVERSE_PORTRAIT = 3;


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
                        if (!firstAttitudeCaptured) {
                            setStartAttitude(updatedDeviceAttitudeAsQuaternion);
                            firstAttitudeCaptured = true; // prevents setStartAttitude() from being re-set after the first pass
                            }
                        if (updatedAngle < min) { // captures the minimum angle (relative to start) that is recorded during the task
                            min = updatedAngle;
                            setMinimumAngle(min);
                            }
                        if (updatedAngle > max) { // captures the maximum angle (relative to start) that is recorded during the task
                            max = updatedAngle;
                            setMaximumAngle(max);
                            }
                        setFinishAttitude(updatedDeviceAttitudeAsQuaternion); // continually reset until the last value
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(deviceMotionReceiver, intentFilter);
    }

    @Override
    public void startBackgroundRecorderService() {
        super.startBackgroundRecorderService();

        Context appContext = getContext().getApplicationContext();
        enableOrientationEventListener(appContext); // initiates capture of the initial device orientation
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
        stop(); // this should stop both device motion recording and the broadcast
    }

    /**
     * Method to get all possible physical orientations of the device (portrait and landscape, reverse
     * portrait and reverse landscape) to ensure that angles (in degrees) are calculated correctly
     * irrespective of the start orientation of the device. Capturing device orientation using Android's
     * Configuration and Display classes is unreliable, as these actually report the rotation of the
     * onscreen rendered image relative to the 'natural' device orientation (portrait on most devices),
     * especially for active tasks when the user may not be looking at the screen, as in the Range of
     * Motion task, and may not realise that auto-rotate is not enabled. We therefore want to use the
     * physical orientation of the device itself, which can be captured using the onOrientationChanged()
     * method from the OrientationEventListener class.
     */
    public void enableOrientationEventListener(Context appContext) {
        OrientationEventListener orientationEventListener = new OrientationEventListener(
                appContext, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int degrees) {
                if (degrees > 315 || degrees <= 45) { // 0 degrees
                    orientation = ORIENTATION_PORTRAIT;
                } else if (degrees > 45 && degrees <= 135) { // 90 degrees
                    orientation = ORIENTATION_REVERSE_LANDSCAPE;
                } else if (degrees > 135 && degrees <= 225) { // 180 degrees
                    orientation = ORIENTATION_REVERSE_PORTRAIT;
                } else if (degrees > 225 && degrees <= 315) { //270 degrees
                    orientation = ORIENTATION_LANDSCAPE;
                } else if (degrees < 0) { // flipped screen
                    orientation = ORIENTATION_UNSPECIFIED;
                    Log.i(ContentValues.TAG, "The device orientation is unspecified: value = "
                            + orientation );
                }
                if (!firstDeviceOrientationCaptured && orientation != ORIENTATION_UNSPECIFIED) {
                    setInitialOrientation(orientation);
                    firstDeviceOrientationCaptured = true; // prevents setFirstOrientation from being re-set
                }
            }
        };
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            orientation = ORIENTATION_UNDETECTABLE;
            Log.i(ContentValues.TAG, "The device orientation is undetectable: value = "
                            + orientation );
        }
    }

    /**
     * Methods to obtain the device's physical orientation at the beginning of the task
     **/
    public int getInitialOrientation() {
        return initialOrientation;
    }

    private void setInitialOrientation(int initialOrientation) {
        this.initialOrientation = initialOrientation;
    }

    /**
     * Methods to obtain the initial (start) device attitude as a unit quaternion
     */
    public float[] getStartAttitude() {
        return startAttitude;
    }

    private void setStartAttitude(float[] startAttitude) {
        this.startAttitude = startAttitude;
    }

    /**
     * Method to obtain range-shifted angle (degrees) of first (start) device attitude, relative to the zero position
     */
    public double getShiftedStartAngle() {
        double shifted_start_angle;
        double raw_start_angle = getDeviceAngleInDegreesFromQuaternion(getStartAttitude());
        shifted_start_angle = shiftStartAndFinishAngleRanges(raw_start_angle);
        return shifted_start_angle;
    }

    /**
     * Methods to obtain the final (finish) device attitude as a unit quaternion
     */
    public float[] getFinishAttitude() {
        return finishAttitude;
    }

    private void setFinishAttitude(float[] finishAttitude) {
        this.finishAttitude = finishAttitude;
    }

    /**
     * Method to obtain range-shifted angle (degrees) of final (finish) device attitude, relative to the zero position
     */
    public double getShiftedFinishAngle() {
        double shifted_finish_angle;
        double raw_finish_angle = getDeviceAngleInDegreesFromQuaternion(getFinishAttitude());
        shifted_finish_angle = shiftStartAndFinishAngleRanges(raw_finish_angle);
        return shifted_finish_angle;
    }

    /**
     * Method to shift default range of calculated angles for specific devices or screen orientations,
     * when required, for start and finish angles. Should be overridden in sub-classes where necessary.
     */
    public double shiftStartAndFinishAngleRanges(double original_angle) {
        double shifted_angle;
        int initial_orientation = getInitialOrientation();
        if (initial_orientation == ORIENTATION_REVERSE_PORTRAIT
                && (original_angle >= 0 && original_angle < 180)) {
            shifted_angle = Math.abs(original_angle) - 360;
        } else {
            shifted_angle = original_angle;
        }
        return shifted_angle;
    }
    
    /**
     * Methods to obtain and calculate the minimum and maximum range-shifted angle (degrees), calculated
     * for all device motion updates during recording, relative to the start position
     */
    private double calculateShiftedAngleRelativeToStart(float[] attitudeUpdates) {
        double shifted_angle;
        float[] attitudeUpdatesRelativeToStart = getDeviceAttitudeRelativeToStart(attitudeUpdates);
        double unadjusted_angle = getDeviceAngleInDegreesFromQuaternion(attitudeUpdatesRelativeToStart);
        shifted_angle = shiftMinAndMaxAngleRange(unadjusted_angle);
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
     * Method to extend the available range for maximum angle from 180 degrees to 270 degrees, relative
     * to the start position. Range for minimum angle will be reduced to 90 degrees. Should be overridden
     * in sub-classes where necessary.
     */
    public double shiftMinAndMaxAngleRange(double original_angle) {
        double shifted_angle;
        int initial_orientation = getInitialOrientation();
        if ((initial_orientation == ORIENTATION_PORTRAIT || initial_orientation == ORIENTATION_REVERSE_PORTRAIT)
                && (original_angle > 90 && original_angle <= 180)) {
            shifted_angle = Math.abs(original_angle) - 360;
        } else {
            shifted_angle = original_angle;
        }
        return shifted_angle;
    }

    /**
     * Method to calculate angles in degrees from the device attitude quaternion, as a function of
     * device orientation (portrait or landscape) or screen orientation (portrait, landscape, reverse
     * portrait or reverse landscape).
     */
    public double getDeviceAngleInDegreesFromQuaternion(float[] quaternion) {
        double angle_in_degrees = 0;
        int initial_orientation = getInitialOrientation();

        if (initial_orientation == ORIENTATION_LANDSCAPE || initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForRoll (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3]));
        }
        else if (initial_orientation == ORIENTATION_PORTRAIT || initial_orientation == ORIENTATION_REVERSE_PORTRAIT) {
            angle_in_degrees = Math.toDegrees(MathUtils.allOrientationsForPitch (
                    quaternion[0],
                    quaternion[1],
                    quaternion[2],
                    quaternion[3]));
        }
        return angle_in_degrees;
    }

    /**
     * Method to obtain the updated device attitude relative to the start position by multiplying updates
     * of the attitude quaternion by the inverse of the quaternion that represents the start position.
     * This relativity is necessary if the task is being performed in different start positions, which
     * could result in angles that exceed the already shifted range.
     */
    public float[] getDeviceAttitudeRelativeToStart(float[] originalDeviceAttitude) {
        float[] relativeDeviceAttitude;
        float[] inverseOfStart = MathUtils.calculateInverseOfQuaternion(getStartAttitude());
        relativeDeviceAttitude = MathUtils.multiplyQuaternions(originalDeviceAttitude, inverseOfStart);
        return relativeDeviceAttitude;
    }

    /**
     * Method to obtain the device's attitude as a unit quaternion from the rotation vector sensor,
     * when it is available
     */
    public float[] getDeviceAttitudeAsQuaternion(float[] rotation_vector) {
        float[] attitudeQuaternion = new float[4];
        SensorManager.getQuaternionFromVector(attitudeQuaternion, rotation_vector);
        return attitudeQuaternion;
    }

    @Override
    protected void stepResultFinished() {

        Context appContext = getContext().getApplicationContext();
        sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
        
        // task
        boolean has_rotation_vector;
        boolean has_accelerometer;
        int initial_orientation;
        //angles
        double start;
        double finish;
        double minimum;
        double maximum;
        double range;

        rangeOfMotionResult = new RangeOfMotionResult(rangeOfMotionStep.getIdentifier());
        
        // Rotation vector sensor is available on the device
        has_rotation_vector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null;
        rangeOfMotionResult.setHasRotationVector(has_rotation_vector);

        // Accelerometer sensor is available on the device
        has_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
        rangeOfMotionResult.setHasAccelerometer(has_accelerometer);
        
        // Initial device orientation
        initial_orientation = getInitialOrientation();
        rangeOfMotionResult.setOrientation(initial_orientation);

        /* Like iOS, when using quaternions via the rotation vector sensor in Android, the zero attitude
        {0,0,0,0} position is parallel with the ground (i.e. screen facing up). Hence, tasks in which
        portrait or landscape is the start position (i.e. perpendicular to the ground) require a 90 degree
        adjustment. These are set to report an absolute an angle between +270 and -90 degrees. These
        calculations will need to be overridden in tasks where this range is not appropriate.*/

        // Capture absolute start angle relative to device orientation
        if (!has_rotation_vector) {
            start = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                start = 90 + getShiftedStartAngle();
            } else if (initial_orientation == ORIENTATION_REVERSE_PORTRAIT) {
                start = -90 - getShiftedStartAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED) {
                start = NaN;
            } else {
                start = 90 - getShiftedStartAngle();
            }
        }
        rangeOfMotionResult.setStart(start);

        // Capture absolute finish angle relative to device orientation
        if (!has_rotation_vector) {
            finish = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                finish = 90 + getShiftedFinishAngle();
            } else if (initial_orientation == ORIENTATION_REVERSE_PORTRAIT) {
                finish = -90 - getShiftedFinishAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED) {
                finish = NaN;
            } else {
                finish = 90 - getShiftedFinishAngle();
            }
        }
        rangeOfMotionResult.setFinish(finish);

        /* Because both knee and shoulder tasks both use pitch in the direction opposite to the device
        axes (i.e. right hand rule), maximum and minimum angles are reported the 'wrong' way around
        for these particular tasks when the device is in portrait or landscape mode */

        // Capture minimum angle relative to device orientation
        if (!has_rotation_vector) {
            minimum = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                minimum = start + getMinimumAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED) {
                minimum = NaN;
            } else {
                minimum = start - getMaximumAngle(); // landscape, portrait and reverse portrait
            }
        }
        rangeOfMotionResult.setMinimum(minimum);

        // Capture maximum angle relative to device orientation
        if (!has_rotation_vector) {
            maximum = NaN;
        } else {
            if (initial_orientation == ORIENTATION_REVERSE_LANDSCAPE) {
                maximum = start + getMaximumAngle();
            } else if (initial_orientation == ORIENTATION_UNSPECIFIED) {
                maximum = NaN;
            } else {
                maximum = start - getMinimumAngle(); // landscape, portrait and reverse portrait
            }
        }
        rangeOfMotionResult.setMaximum(maximum);

        // Capture range as largest difference across all recorded angles
        range = Math.abs(maximum - minimum);
        rangeOfMotionResult.setRange(range);

        stepResult.setResultForIdentifier(rangeOfMotionResult.getIdentifier(), rangeOfMotionResult);
    }
}
