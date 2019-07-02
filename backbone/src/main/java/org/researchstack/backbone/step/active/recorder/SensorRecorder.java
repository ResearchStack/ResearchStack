package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.SystemClock;

import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.LogExt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private static final long MICRO_SECONDS_PER_SEC = 1000000L;

    public static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    public static final String UPTIME_IN_SECONDS_KEY = "uptime";
    public static final String TIMESTAMP_DATE_KEY = "timestampDate";

    /**
     * The frequency of the sensor data collection in samples per second (Hz).
     * Android Sensors do not allow exact frequency specifications, per their documentation,
     * it is only a HINT, so we must manage it ourselves in a posted runnable with delay
     */
    private double frequency;

    private SensorManager sensorManager;
    private List<Sensor> sensorList;

    private long timestampZeroReferenceNanos = 0;

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

                if (!success) {
                    LogExt.i(SensorRecorder.class, "Failed to register sensor: " + sensor);
                }
            }
        }

        if (!anySucceeded) {
            super.onRecorderFailed("Failed to initialize any sensor");
        } else {
            super.startJsonDataLogging();
        }
    }

    @Override
    public final void onSensorChanged(SensorEvent sensorEvent) {

        JsonObject jsonObject = new JsonObject();

        if (timestampZeroReferenceNanos <= 0) {
            // set timestamp reference, which timestamps are measured relative to
            timestampZeroReferenceNanos = sensorEvent.timestamp;

            // record date equivalent of timestamp reference
            long uptimeNanos;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                uptimeNanos = SystemClock.elapsedRealtimeNanos();
            } else {
                uptimeNanos = (long) (SystemClock.elapsedRealtime() * 1e6); // millis to nanos
            }

            long timestampReferenceMillis = System.currentTimeMillis()
                    + (long) ((timestampZeroReferenceNanos - uptimeNanos) * 1e-6d);
            Date timestampReferenceDate = new Date(timestampReferenceMillis);
            jsonObject.addProperty(TIMESTAMP_DATE_KEY,
                    new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601, Locale.getDefault())
                            .format(timestampReferenceDate));
        }

        // these values are doubles
        jsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY,
                (sensorEvent.timestamp - timestampZeroReferenceNanos) * 1e-9);
        jsonObject.addProperty(UPTIME_IN_SECONDS_KEY, sensorEvent.timestamp * 1e-9);

        recordSensorEvent(sensorEvent, jsonObject);

        writeJsonObjectToFile(jsonObject);
    }

    /***
     * This method receives a SensorEvent and a JsonObject and is expected to update the
     * JsonObject with data to be written.
     * @param sensorEvent
     * @param object json object pre-populated with uptime and timestamp
     */
    public abstract void recordSensorEvent(final SensorEvent sensorEvent,
                                           final JsonObject object);

    @Override
    public void stop() {
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
        stopJsonDataLogging();
    }

    @Override
    public void cancel() {
        super.cancel();
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
