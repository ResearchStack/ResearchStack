package org.researchstack.backbone.step.active;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 2/7/17.
 */

abstract class SensorRecorder extends JsonArrayDataRecorder implements SensorEventListener {
    private static final long MICRO_SECONDS_PER_SEC = 1000000L;

    /**
     * The frequency of accelerometer data collection in samples per second (Hz).
     */
    private double frequency;

    private SensorManager sensorManager;
    private List<Sensor> sensorList;

    /** Default constructor for serialization/deserialization */
    SensorRecorder() {
        super();
    }

    SensorRecorder(double frequency, String identifier, Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
        this.frequency = frequency;
    }

    /**
     * @return a list of sensor types that should be listened to
     *         for example, if you only want accelerometer, you would return
     *         Collections.singletonList(Sensor.TYPE_ACCELEROMETER)
     */
    protected abstract List<Integer> getSensorTypeList();

    @Override
    public void start(Context context) {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        sensorList = new ArrayList<>();
        boolean anySucceeded = false;
        for (int sensorType : getSensorTypeList()) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            if (sensor != null) {
                sensorList.add(sensor);
                boolean success = sensorManager.registerListener(
                        this, sensor, calculateDelayBetweenSamplesInMicroSeconds());
                anySucceeded |= success;
            }
        }

        if (!anySucceeded) {
            super.onRecorderFailed("Failed to initialize sensor");
        } else {
            super.startJsonDataLogging(frequency);
        }
    }

    @Override
    public void stop() {
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
        stopJsonDataLogging();
    }

    protected int calculateDelayBetweenSamplesInMicroSeconds() {
        return (int)(MICRO_SECONDS_PER_SEC / frequency);
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
