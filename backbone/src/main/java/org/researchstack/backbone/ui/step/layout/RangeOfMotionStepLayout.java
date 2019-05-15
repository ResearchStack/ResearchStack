package org.researchstack.backbone.ui.step.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import java.lang.Math;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RangeOfMotionStep;

/**
 * Created by David Evans, David Jimenez, Laurence Hurst, Simon Hartley, 2019.
 *
 * The RangeOfMotionStepLayout is essentially the same as the ActiveStepLayout, except that it
 * calculates the start, maximum, minimum and finish (Euler) angle results
 *
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

    protected SensorEvent event;
    protected RangeOfMotionStep rangeOfMotionStep;
    protected RangeOfMotionResult rangeOfMotionResult;
    private BroadcastReceiver deviceMotionReceiver;

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

        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void validateStep(Step step) {
        if (!(step instanceof RangeOfMotionStep)) {
            throw new IllegalStateException("RangeOfMotionStepLayout must have a RangeOfMotionStep");
        }
        rangeOfMotionStep = (RangeOfMotionStep) step;
        super.validateStep(step);
    }


    /* Not sure that we need this section for device motion


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
    //1. Method for obtaining and holding the reference attitude as quaternion (i.e. the first orientation when recording begins)
    //2. Method for obtaining and continually updating current attitude as quaternion
    //3. Method for inverting the reference quaternion and multiplying by current quaternion to give relative position
    //4. Method for converting relative quaternion to a Euler angle, depending on device orientation (landscape or portrait)
    //5. Methods to calculate minimum and maximum Euler angles

    /** Methods to calculate maximum and minimum angles recorded by the device **/

    public double getMinimumAngle() {

        double min_angle = 0;
        double new_angle = shiftAngleRange();

        if (new_angle < min_angle) {
            min_angle = new_angle;
        }
        return min_angle;
    }

    public double getMaximumAngle() {

        double max_angle = 0;
        double new_angle = shiftAngleRange();

        if (new_angle > max_angle) {
            max_angle = new_angle;
        }
        return max_angle
    }


    /* We need to shift the range of pitch angles reported by the device from +/-180 degrees
     to -90 to +270 degrees, which should be sufficient to cover all achievable knee and
     shoulder ranges of motion */

    /** Method to shift angle range**/

    private double shiftAngleRange() {

        double shifted_angle;
        double angle_in_degrees = getDeviceAngleInDegreesFromAttitude(event);
        boolean targetAngleRange = ((angle_in_degrees > 90) && (angle_in_degrees <= 180));

        if (targetAngleRange == true) {
        //if ((angle_in_degrees > 90) && (angle_in_degrees <= 180)) { //Not sure if this version will restrict the calculation only to relevant values
            shifted_angle = Math.abs(angle_in_degrees) - 360;
            return shifted_angle;
        }
        else {
            shifted_angle = angle_in_degrees;
            return shifted_angle;
        }
    }


    /** Method to calculate Euler angles from the device attitude quaternion **/

    private double getDeviceAngleInDegreesFromAttitude() {

        int orientation = getResources().getConfiguration().orientation;
        float[] Quaternion = getDeviceAttitudeAsQuaternion();
        double angle_in_degrees = 0;


        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            getDeviceAttitudeAsQuaternion();
            angle_in_degrees = Math.toDegrees(allOrientationsForRoll(Quaternion[0], Quaternion[1], Quaternion[2], Quaternion[3]));
            // To convert radians to degrees, we could instead use: double radiansToDegrees = rad * 180.0 / Math.PI;
        }
        else if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            getDeviceAttitudeAsQuaternion();
            angle_in_degrees = Math.toDegrees(allOrientationsForPitch(Quaternion[0], Quaternion[1], Quaternion[2], Quaternion[3]));
        }
        return angle_in_degrees;
    }


    /** Methods to convert quaternions to Euler angles **/

    public double allOrientationsForPitch (double w, double x, double y, double z){

        double angle_in_rads;

        angle_in_rads = (Math.atan2(2.0 * (x * w + y * z), 1.0 - 2.0 * (x * x + z * z)));

        return  angle_in_rads;
    }

    public double allOrientationsForRoll (double w, double x, double y, double z){

        double angle_in_rads;

        angle_in_rads = (Math.atan2(2.0 * (y * w - x * z), 1.0 - 2.0 * (y * y + z * z)));

        return  angle_in_rads;
    }

    //Yaw (azimuth) is not needed with the current knee and shoulder tasks, but will be for other RoM tasks
    public double allOrientationsForYaw (double w, double x, double y, double z){

        double angle_in_rads;

        angle_in_rads = (Math.asin(2.0 * (x * y - w * z)));

        return  angle_in_rads;
    }


    /** Method to multiply current attitude quaternion by the inverse of the reference quaternion **/

        /*  iOS code for calculating change in device orientation (attitude)
        - (void)deviceMotionRecorderDidUpdateWithMotion:(CMDeviceMotion *)motion {
        if (!_referenceAttitude) {
        _referenceAttitude = motion.attitude;
        }
        CMAttitude *currentAttitude = [motion.attitude copy];

        [currentAttitude multiplyByInverseOfAttitude:_referenceAttitude];

        double angle = [self getDeviceAngleInDegreesFromAttitude:currentAttitude];
        */

    public float multiplyReferenceQuaternionByCurrentAttitude() {

        int type = event.sensor.getType();
        float[] currentAttitudeQuaternion = getDeviceAttitudeAsQuaternion(); // on every update
        float[] inverseReferenceQuaternion = getInverseOfFirstAttitudeQuaternion();
        float[] changeAttitudeQuaternion;

        if (type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getQuaternionFromVector(currentAttitudeQuaternion, event.values);
            changeAttitudeQuaternion = currentAttitudeQuaternion * inverseReferenceQuaternion;
        }
        return changeAttitudeQuaternion;
    }


    /** Method to obtain and calculate the inverse (complex conjugate) of the reference quaternion**/

    public float[] getInverseOfFirstAttitudeQuaternion () {

        float [] firstAttitudeQuaternion = getDeviceAttitudeAsQuaternion(); // todo: need to limit this to first sensor event only
        float [] inverseOfFirstAttitudeQuaternion = new float [4];

        inverseOfFirstAttitudeQuaternion[0] = firstAttitudeQuaternion[0];
        inverseOfFirstAttitudeQuaternion[1] = -(firstAttitudeQuaternion[1]);
        inverseOfFirstAttitudeQuaternion[2] = -(firstAttitudeQuaternion[2]);
        inverseOfFirstAttitudeQuaternion[3] = -(firstAttitudeQuaternion[3]);

        return inverseOfFirstAttitudeQuaternion;
    }


    /** Method to obtain the device attitude's quaternion from the rotation vector **/

    public float[] getDeviceAttitudeAsQuaternion() {

        int type = event.sensor.getType();
        float[] q = {0, 0, 0, 0};

        if (type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getQuaternionFromVector(q, event.values);
        }
        return q;
}



@interface ORKRangeOfMotionStepViewController () <ORKDeviceMotionRecorderDelegate> {
ORKRangeOfMotionContentView *_contentView;
UITapGestureRecognizer *_gestureRecognizer;
CMAttitude *_referenceAttitude;
UIInterfaceOrientation _orientation;
}

@end


@implementation ORKRangeOfMotionStepViewController

- (void)viewDidLoad {
[super viewDidLoad];
_contentView = [ORKRangeOfMotionContentView new];
_contentView.translatesAutoresizingMaskIntoConstraints = NO;
self.activeStepView.activeCustomView = _contentView;
_gestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
[self.activeStepView addGestureRecognizer:_gestureRecognizer];
}


//This function records the angle of the device when the screen is tapped
- (void)handleTap:(UIGestureRecognizer *)sender {
[this calculateAndSetAngles];
[this finish];
}


public void calculateAndSetAngles {
_startAngle = ([this getDeviceAngleInDegreesFromAttitude:_referenceAttitude]);




//This calculates the current device orientation relative to the start orientation, by multiplying by the current orientation by inverse of the original orientation

public void deviceMotionRecorderDidUpdateWithMotion:
    onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
        if (!_referenceAttitude) {
        _referenceAttitude = motion.attitude;
    }
    CMAttitude *currentAttitude = [motion.attitude copy];

    [currentAttitude multiplyByInverseOfAttitude:_referenceAttitude];

double angle = [this getDeviceAngleInDegreesFromAttitude:currentAttitude];




//#pragma mark - ORKActiveTaskViewController

- (ORKResult *)result {
ORKStepResult *stepResult = [super result];

ORKRangeOfMotionResult *result = [[ORKRangeOfMotionResult alloc] initWithIdentifier:self.step.identifier];

//result.start = 90.0 - _startAngle;
result.start = _startAngle; // In Android's zero orientation, the device is in portrait (perpendicular to the ground); whereas in iOS it is parallel with the ground
result.finish = result.start - _newAngle;
//Because the task uses pitch in the direction opposite to the original device axes (i.e. right hand rule), maximum and minimum angles are reported the 'wrong' way around for the knee and shoulder tasks
result.minimum = result.start - _maxAngle;
result.maximum = result.start - _minAngle;
result.range = fabs(result.maximum - result.minimum);

stepResult.results = [self.addedResults arrayByAddingObject:result] ? : @[result];

return stepResult;
}

/*

 From iOS:
 
@implementation ORKRangeOfMotionStepViewController

        - (void)viewDidLoad {
        [super viewDidLoad];
        _contentView = [ORKRangeOfMotionContentView new];
        _contentView.translatesAutoresizingMaskIntoConstraints = NO;
        self.activeStepView.activeCustomView = _contentView;
        _gestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap:)];
        [self.activeStepView addGestureRecognizer:_gestureRecognizer];
        }
        //This function records the angle of the device when the screen is tapped
        - (void)handleTap:(UIGestureRecognizer *)sender {
        [self calculateAndSetAngles];
        [self finish];
        }

        - (void)calculateAndSetAngles {
        _startAngle = ([self getDeviceAngleInDegreesFromAttitude:_referenceAttitude]);

        //This function calculates maximum and minimum angles recorded by the device
        if (_newAngle > _maxAngle) {
        _maxAngle = _newAngle;
        }
        if (_minAngle == 0.0 || _newAngle < _minAngle) {
        _minAngle = _newAngle;
        }
        }

        #pragma mark - ORKDeviceMotionRecorderDelegate

        - (void)deviceMotionRecorderDidUpdateWithMotion:(CMDeviceMotion *)motion {
        if (!_referenceAttitude) {
        _referenceAttitude = motion.attitude;
        }
        CMAttitude *currentAttitude = [motion.attitude copy];

        [currentAttitude multiplyByInverseOfAttitude:_referenceAttitude];

        double angle = [self getDeviceAngleInDegreesFromAttitude:currentAttitude];

        //This function shifts the range of angles reported by the device from +/-180 degrees to -90 to +270 degrees, which should be sufficient to cover all ahievable knee and shoulder ranges of motion
        BOOL shiftAngleRange = angle > 90 && angle <= 180;
        if (shiftAngleRange) {
        _newAngle = fabs(angle) - 360;
        } else {
        _newAngle = angle;
        }

        [self calculateAndSetAngles];
        }

/*
 When the device is in Portrait mode, we need to get the attitude's pitch
 to determine the device's angle. attitude.pitch doesn't return all
 orientations, so we use the attitude's quaternion to calculate the
 angle.

        - (double)getDeviceAngleInDegreesFromAttitude:(CMAttitude *)attitude {
        if (!_orientation) {
        _orientation = [UIApplication sharedApplication].statusBarOrientation;
        }
        double angle;
        if (UIInterfaceOrientationIsLandscape(_orientation)) {
        double x = attitude.quaternion.x;
        double w = attitude.quaternion.w;
        double y = attitude.quaternion.y;
        double z = attitude.quaternion.z;
        angle = radiansToDegrees(allOrientationsForRoll(x, w, y, z));
        } else {
        double x = attitude.quaternion.x;
        double w = attitude.quaternion.w;
        double y = attitude.quaternion.y;
        double z = attitude.quaternion.z;
        angle = radiansToDegrees(allOrientationsForPitch(x, w, y, z));
        }
        return angle;
        }

 */

#pragma mark - ORKActiveTaskViewController

        - (ORKResult *)result {
        ORKStepResult *stepResult = [super result];

        ORKRangeOfMotionResult *result = [[ORKRangeOfMotionResult alloc] initWithIdentifier:self.step.identifier];

        result.start = 90.0 - _startAngle;
        result.finish = result.start - _newAngle;
        //Because the task uses pitch in the direction opposite to the original CoreMotion device axes (i.e. right hand rule), maximum and minimum angles are reported the 'wrong' way around for the knee and shoulder tasks
        result.minimum = result.start - _maxAngle;
        result.maximum = result.start - _minAngle;
        result.range = fabs(result.maximum - result.minimum);

        stepResult.results = [self.addedResults arrayByAddingObject:result] ? : @[result];

        return stepResult;
        }

