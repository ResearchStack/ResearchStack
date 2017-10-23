package org.researchstack.backbone.step.active.recorder;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by TheMDP on 2/15/17.
 *
 * https://github.com/bagilevi/android-pedometer
 * Accelerometer to Step Algorithm from link is distributed under a No restrictions license
 * TODO: develop a better step detector method, this one has too many unknown constants
 * TODO: it also seems to have a variance of about +-15% in my experiments
 */

public class AccelerometerStepDetector {

    private final static String TAG = "StepDetector";

    private OnStepTakenListener onStepTakenListener;

    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;

    public AccelerometerStepDetector() {
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    }

    public void processAccelerometerData(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float vSum = 0;
            for (int i = 0; i < 3; i++) {
                final float v = mYOffset + sensorEvent.values[i] * mScale[1];
                vSum += v;
            }
            int k = 0;
            float v = vSum / 3;

            float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
            if (direction == -mLastDirections[k]) {
                // Direction changed
                int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                mLastExtremes[extType][k] = mLastValues[k];
                float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                if (diff > mLimit) {

                    boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                    boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                    boolean isNotContra = (mLastMatch != 1 - extType);

                    if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                        Log.i(TAG, "step");
                        onStepTaken();
                        mLastMatch = extType;
                    } else {
                        mLastMatch = -1;
                    }
                }
                mLastDiff[k] = diff;
            }
            mLastDirections[k] = direction;
            mLastValues[k] = v;
        }
    }

    private void onStepTaken() {
        if (onStepTakenListener != null) {
            onStepTakenListener.onStepTaken();
        }
    }

    public void setOnStepTakenListener(OnStepTakenListener onStepTakenListener) {
        this.onStepTakenListener = onStepTakenListener;
    }

    public interface OnStepTakenListener {
        void onStepTaken();
    }
}
