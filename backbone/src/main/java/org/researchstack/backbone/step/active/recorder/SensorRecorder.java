package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 2/7/17.
 *
 * The SensorRecorder is an abstract class that greatly reduces the amount of work required
 * to write sensor data to a DataLogger json file
 *
 * Any Android sensor is compatible with this class as long as you correctly implement
 * the two abstract methods, getSensorTypeList, and writeJsonData
 */

abstract class SensorRecorder extends JsonArrayDataRecorder implements SensorEventListener {

    public static final float MANUAL_JSON_FREQUENCY = -1.0f;

    private static final long MILLI_SECONDS_PER_SEC = 1000L;
    private static final long MICRO_SECONDS_PER_SEC = 1000000L;

    /**
     * The frequency of the sensor data collection in samples per second (Hz).
     * Android Sensors do not allow exact frequency specifications, per their documentation,
     * it is only a HINT, so we must manage it ourselves in a posted runnable with delay
     */
    private double frequency;

    private SensorManager sensorManager;
    private List<Sensor> sensorList;

    private Handler mainHandler;
    private Runnable jsonWriterRunnable;
    private int  writeCounter;
    private long writeDelayGoal;
    private long writeStartTime;

    SensorRecorder(double frequency, String identifier, Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
        this.frequency = frequency;
    }

    /**
     * @param  availableSensorList the list of available sensors for the user's device
     * @return a list of sensor types that should be listened to
     *         for example, if you only want accelerometer, you would return
     *         Collections.singletonList(Sensor.TYPE_ACCELEROMETER)
     */
    protected abstract List<Integer> getSensorTypeList(List<Sensor> availableSensorList);

    /**
     * This is called at the specified frequency so that we get an accurate frequency,
     * since Android sensors do not allow precise sensor frequency reporting
     *
     * Subclasses should call writeJsonObjectToFile() from within this method
     */
    protected abstract void writeJsonData();

    @Override
    public void start(Context context) {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        sensorList = new ArrayList<>();
        List<Sensor> availableSensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        boolean anySucceeded = false;
        for (int sensorType : getSensorTypeList(availableSensorList)) {
            Sensor sensor = sensorManager.getDefaultSensor(sensorType);
            if (sensor != null) {
                sensorList.add(sensor);
                boolean success;
                if (isManualFrequency()) {
                    success = sensorManager.registerListener(
                            this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                } else {
                    success = sensorManager.registerListener(this, sensor,
                            calculateDelayBetweenSamplesInMicroSeconds());
                }
                anySucceeded |= success;
            }
        }

        if (!anySucceeded) {
            super.onRecorderFailed("Failed to initialize sensor");
        } else {
            super.startJsonDataLogging();
        }

        startRunnableForWritingJson();
    }

    protected void startRunnableForWritingJson() {
        // These will be used to monitor periodic writes to get an accurate write frequency
        mainHandler = new Handler();
        writeCounter = 1;
        writeStartTime = System.currentTimeMillis();

        if (frequency < 0) {
            // Writing the JSON will be done manually, and not at a specific frequency
        } else {
            writeDelayGoal = calculateDelayBetweenSamplesInMilliSeconds();
            jsonWriterRunnable = new Runnable() {
                @Override
                public void run() {
                    writeJsonData();

                    writeCounter++;
                    // Offset delay from the writeJsonData call to get an accurate write frequency
                    long delayGoal = ((writeStartTime + (writeCounter * writeDelayGoal)) - System.currentTimeMillis());
                    // The device is not fast enough to keep up, so we will get a frequency only
                    // as fast as it can do, so just make the delay goal be the original delay
                    if (delayGoal <= 0) {
                        // minimal write delay to give the UI thread some time to catch up
                        // and hopefully get the frequency back up to the desired one
                        delayGoal = 1;
                    }

                    mainHandler.postDelayed(jsonWriterRunnable, delayGoal);
                }
            };
            mainHandler.postDelayed(jsonWriterRunnable, writeDelayGoal);
        }
    }

    @Override
    public void stop() {
        mainHandler.removeCallbacks(jsonWriterRunnable);
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
        stopJsonDataLogging();
    }

    @Override
    public void cancel() {
        super.cancel();
        mainHandler.removeCallbacks(jsonWriterRunnable);
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    /**
     * @param availableSensorList the list of available sensors
     * @param sensorType the sensor type to check if it is contained in the list
     * @return true if that sensor type is available, false if it is not
     */
    protected boolean hasAvailableType(List<Sensor> availableSensorList, int sensorType) {
        for (Sensor sensor : availableSensorList) {
            if (sensor.getType() == sensorType) {
                return true;
            }
        }
        return false;
    }

    protected long calculateDelayBetweenSamplesInMilliSeconds() {
        return (long)((float)MILLI_SECONDS_PER_SEC / frequency);
    }

    protected int calculateDelayBetweenSamplesInMicroSeconds() {
        return (int)((float)MICRO_SECONDS_PER_SEC / frequency);
    }

    /**
     * @return true if sensor frequency does not exist, and callbacks will be based on an event, like Step Detection
     *         false if the sensor frequency will come back at a desired frequency
     */
    protected boolean isManualFrequency() {
        return frequency < 0;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
