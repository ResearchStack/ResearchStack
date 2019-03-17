package org.researchstack.backbone.ui.step.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.lang.Math;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.RangeOfMotionResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder;

/**
 * Created by David Evans, 2019.
 *
 * The RangeOfMotionStepLayout is essentially the same as the ActiveStepLayout, except that it
 * calculates the start, maximum, minimum and finish (Euler) angle results
 *
 *
 */

public class RangeOfMotionStepLayout extends ActiveStepLayout {

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
}



/*
  Conversion equations below (based on https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles )

or https://docs.oracle.com/javase/6/docs/api/java/lang/Math.html#toDegrees(double)
 */


//public static void allOrientationsForPitch(double quaternion_w, double quaternion_x, double quaternion_y, double quaternion_z)

public double allOrientationsForPitch(quaternion_w, quaternion_x, quaternion_y, quaternion_z) = (Math.atan2(2.0 * (quaternion_x * quaternion_w + quaternion_y * quaternion_z), 1.0 - 2.0 * (quaternion_x * quaternion_x + quaternion_z * quaternion_z)))

public double allOrientationsForRoll(quaternion_w, quaternion_x, quaternion_y, quaternion_z) = (Math.atan2(2.0 * (quaternion_y * quaternion_w - quaternion_x * quaternion_z), 1.0 - 2.0 * (quaternion_y * quaternion_y + quaternion_z * quaternion_z)))

//Yaw is not needed with the current knee and shoulder tasks, but will be for other RoM tasks
public double allOrientationsForYaw(quaternion_w, quaternion_x, quaternion_y, quaternion_z) = (Math.asin(2.0 * (quaternion_x * quaternion_y - quaternion_w * quaternion_z)))



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


//This function calculates maximum and minimum angles recorded by the device
if (_newAngle > _maxAngle) {
    _maxAngle = _newAngle;
    }
if (_minAngle == 0.0 || _newAngle < _minAngle) {
    _minAngle = _newAngle;
    }
}


//This calculates the current device orientation relative to the start orientation, by multiplying by the inverse of the current orientation
public void deviceMotionRecorderDidUpdateWithMotion:
    onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
        if (!_referenceAttitude) {
        _referenceAttitude = motion.attitude;
    }
    CMAttitude *currentAttitude = [motion.attitude copy];

    [currentAttitude multiplyByInverseOfAttitude:_referenceAttitude];

double angle = [this getDeviceAngleInDegreesFromAttitude:currentAttitude];


//This function shifts the range of angles reported by the device from +/-180 degrees to
//-90 to +270 degrees, which should be sufficient to cover all achievable knee and shoulder ranges of motion
boolean shiftAngleRange = angle > 90 && angle <= 180;
    if (shiftAngleRange) {
        double _newAngle = fabs(angle) - 360;
        }
    else {
        double _newAngle = angle;
        }

    [this calculateAndSetAngles];
}


/*
 When the device is in Portrait mode, we need to get the attitude's pitch
 to determine the device's angle. attitude.pitch doesn't return all
 orientations, so we use the attitude's quaternion to calculate the
 Euler angle.
 */

public static getDeviceAngleInDegreesFromAttitude:
    onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
        double angle {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                SensorManager.getQuaternionFromVector(double[] Quarternion, event.values);
                double quaternion_w = Quaternion[0];
                double quaternion_x = Quaternion[1];
                double quaternion_y = Quaternion[2];
                double quaternion_z = Quaternion[3];
            }
            angle = Math.toDegrees(allOrientationsForRoll(quaternion_w, quaternion_x, quaternion_y, quaternion_z)); // To convert radians to degrees, we could instead use: double radiansToDegrees = rad * 180.0 / Math.PI;
            }
            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                SensorManager.getQuaternionFromVector(double[] Quarternion, event.values);
                double quaternion_w = Quaternion[0];
                double quaternion_x = Quaternion[1];
                double quaternion_y = Quaternion[2];
                double quaternion_z = Quaternion[3];
            }
            angle = Math.toDegrees(allOrientationsForPitch(quaternion_w, quaternion_x, quaternion_y, quaternion_z));
        }
    return angle;
}


#pragma mark - ORKActiveTaskViewController

- (ORKResult *)result {
ORKStepResult *stepResult = [super result];

ORKRangeOfMotionResult *result = [[ORKRangeOfMotionResult alloc] initWithIdentifier:self.step.identifier];

//result.start = 90.0 - _startAngle;
result.start = _startAngle; // Android's zero orientation is in portrait (perpendicular to the ground); whereas iOs is paralell with the ground
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

