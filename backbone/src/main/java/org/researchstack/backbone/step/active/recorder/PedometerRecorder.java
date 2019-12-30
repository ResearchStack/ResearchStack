package org.researchstack.backbone.step.active.recorder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.model.UserHealth;
import org.researchstack.backbone.step.Step;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by TheMDP on 2/15/17.
 */

public class PedometerRecorder extends SensorRecorder
        implements AccelerometerStepDetector.OnStepTakenListener
{
    public static final String TIMESTAMP_KEY   = "timestamp";
    public static final String END_DATE        = "endDate";
    public static final String NUMBER_OF_STEPS = "numberOfSteps";
    public static final String DISTANCE        = "totalDistance";

    public static final String BROADCAST_PEDOMETER_UPDATE_ACTION  = "LocationRecorder_BroadcastPedometerUpdate";
    private static final String BROADCAST_PEDOMETER_UPDATE_KEY    = "PedometerUpdate";

    /**
     * This used to compute the totalDistance the user has traveled while recording the pedometer
     * The default value is about half a meter, it will only be used when a user's height is unavailable
     */
    public static final float DEFAULT_METERS_PER_STRIDE = 0.6f; // in meters

    /**
     * This factor, when multiplied by a user's height, will determine an average stride length
     */
    public static final float HEIGHT_FACTOR_FOR_STRIDE_LENGTH_MALE      = 0.413f;
    public static final float HEIGHT_FACTOR_FOR_STRIDE_LENGTH_FEMALE    = 0.415f;

    private boolean useAccelerometerDetector;
    private AccelerometerStepDetector accelerometerStepDetector;
    private float strideLength; // in meters

    private int stepCounter;
    private JsonObject jsonObject;
    private Context appContext;

    PedometerRecorder(String identifier, Step step, File outputDirectory) {
        super(MANUAL_JSON_FREQUENCY, identifier, step, outputDirectory);
    }

    @Override
    protected List<Integer> getSensorTypeList(List<Sensor> availableSensorList) {
        // Step detector is only available for OS kitkat and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Not all devices even have the pedometer sensor
            // I haven't found a full list yet but here are some that have it
            // HTC One M8, Nexus 5x, Nexus 6p, Samsung S6 and S7
            if (hasAvailableType(availableSensorList, Sensor.TYPE_STEP_DETECTOR)) {
                useAccelerometerDetector = false;
                return Collections.singletonList(Sensor.TYPE_STEP_DETECTOR);
            }
        }
        // do a custom pedometer algorithm
        useAccelerometerDetector = true;
        return Collections.singletonList(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void start(Context context) {
        super.start(context);
        stepCounter = 0;
        jsonObject = new JsonObject();
        if (useAccelerometerDetector) {
            accelerometerStepDetector = new AccelerometerStepDetector();
            accelerometerStepDetector.setOnStepTakenListener(this);
        }
        strideLength = computeStrideLength(context);
    }

    /**
     * @param context used to obtain the User's health data
     * @return attempts to use the user's height and gender to compute an accurate average stride length
     *         default stride length used if the health data is not available
     */
    private float computeStrideLength(Context context) {
        float computedStride = DEFAULT_METERS_PER_STRIDE;
        User user = DataProvider.getInstance().getUser(context);
        if (user != null) {
            UserHealth userHealth = user.getUserHealth();
            if (userHealth != null && userHealth.hasHeight()) {
                float heightFactor = HEIGHT_FACTOR_FOR_STRIDE_LENGTH_FEMALE;
                if (userHealth.getGender() == UserHealth.Gender.MALE) {
                    heightFactor = HEIGHT_FACTOR_FOR_STRIDE_LENGTH_MALE;
                }

                computedStride = userHealth.getHeight() * heightFactor;
            }
        }
        return computedStride;
    }

    @MainThread
    @Override
    public void onStepTaken() {
        stepCounter++;
        jsonObject.addProperty(TIMESTAMP_KEY, System.currentTimeMillis());
        jsonObject.addProperty(END_DATE, System.currentTimeMillis());
        jsonObject.addProperty(NUMBER_OF_STEPS, stepCounter);
        float distance = strideLength * stepCounter;
        jsonObject.addProperty(DISTANCE, distance);
        super.writeJsonObjectToFile(jsonObject);
        sendPedometerUpdateBroadcast(stepCounter, distance);
    }


    @Override
    public void recordSensorEvent(SensorEvent sensorEvent, JsonObject object) {
        // Step detector is only available for OS kitkat and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            onStepTaken();
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerStepDetector.processAccelerometerData(sensorEvent);
        }
        //TODO: fix accelerometer data
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // NO-OP
    }

    protected void sendPedometerUpdateBroadcast(int stepCount, float totalDistance) {
        PedometerUpdateHolder dataHolder = new PedometerUpdateHolder();
        dataHolder.setStepCount(stepCount);
        dataHolder.setTotalDistance(totalDistance);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BROADCAST_PEDOMETER_UPDATE_KEY, dataHolder);
        Intent intent = new Intent(BROADCAST_PEDOMETER_UPDATE_ACTION);
        intent.putExtras(bundle);
        intent.setAction(org.researchstack.backbone.step.active.recorder.PedometerRecorder.BROADCAST_PEDOMETER_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(appContext).
            sendBroadcast(intent);
    }

    /**
     * @param intent must have action of BROADCAST_PEDOMETER_UPDATE_ACTION
     * @return the PedometerUpdateHolder contained in the broadcast
     */
    public static PedometerUpdateHolder getPedometerUpdateHolder(Intent intent) {
        if (intent.getAction() == null ||
                !intent.getAction().equals(BROADCAST_PEDOMETER_UPDATE_ACTION) ||
                intent.getExtras() == null ||
                !intent.getExtras().containsKey(BROADCAST_PEDOMETER_UPDATE_KEY)) {
            return null;
        }
        return (PedometerUpdateHolder) intent.getExtras()
                .getSerializable(BROADCAST_PEDOMETER_UPDATE_KEY);
    }

    public static class PedometerUpdateHolder implements Serializable {
        private int stepCount;
        private float totalDistance;

        public int getStepCount() {
            return stepCount;
        }

        public void setStepCount(int stepCount) {
            this.stepCount = stepCount;
        }

        public float getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(float totalDistance) {
            this.totalDistance = totalDistance;
        }
    }
}
