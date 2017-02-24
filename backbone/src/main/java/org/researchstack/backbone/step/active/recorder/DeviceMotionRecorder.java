package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * The DeviceMotionRecorder incorporates a bunch of sensor fusion sensor readings
 * together to paint a broad picture of the device's orientation and movement over time
 *
 * This class is an attempt at mimicing iOS' device motion class which has all of these
 * sensor values updated at the same time.  However, on Android, we need to collect
 * them all in parrallel and write the group at a frequency separate of onSensorValueChanged
 */

public class DeviceMotionRecorder extends SensorRecorder {

    public static final String TIMESTAMP_KEY            = "timestamp";
    public static final String ACCURACY_KEY             = "accuracy";

    public static final String ROTATION_VECTOR_KEY      = "attitude";
    public static final String GYROSCOPE_KEY            = "rotationRate";
    public static final String ACCELEROMETER_KEY        = "gravity";
    public static final String LINEAR_ACCELEROMETER_KEY = "userAcceleration";
    public static final String MAGNETIC_FIELD_KEY       = "magneticField";

    public static final String X_KEY    = "x";
    public static final String Y_KEY    = "y";
    public static final String Z_KEY    = "z";
    public static final String W_KEY    = "w";

    private JsonObject jsonObject;
    private JsonObject attitudeJsonObject;
    private JsonObject gyroscopeJsonObject;
    private JsonObject accelJsonObject;
    private JsonObject linAccelJsonObject;
    private JsonObject magneticJsonObject;

    DeviceMotionRecorder(double frequency, String identifier, Step step, File outputDirectory) {
        super(frequency, identifier, step, outputDirectory);
    }

    @Override
    public void start(Context context) {
        super.start(context);
        if (isRecording()) {

            jsonObject = new JsonObject();
            attitudeJsonObject = new JsonObject();
            gyroscopeJsonObject = new JsonObject();
            accelJsonObject = new JsonObject();
            linAccelJsonObject = new JsonObject();
            magneticJsonObject = new JsonObject();
        }
    }

    /**
     * Called by the base class at the Recorder's frequency
     */
    @Override
    protected void writeJsonData() {
        // Update the main json object
        jsonObject.addProperty(TIMESTAMP_KEY, System.currentTimeMillis());
        jsonObject.add(ACCELEROMETER_KEY, accelJsonObject);
        jsonObject.add(LINEAR_ACCELEROMETER_KEY, linAccelJsonObject);
        jsonObject.add(GYROSCOPE_KEY, gyroscopeJsonObject);
        jsonObject.add(MAGNETIC_FIELD_KEY, magneticJsonObject);
        jsonObject.add(ROTATION_VECTOR_KEY, attitudeJsonObject);

        // Write the main json object
        writeJsonObjectToFile(jsonObject);
    }

    @Override
    protected List<Integer> getSensorTypeList(List<Sensor> availableSensorList) {
        List<Integer> sensorTypeList = new ArrayList<>();

        // Only add these sensors if the device has them
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER)) {
            sensorTypeList.add(Sensor.TYPE_ACCELEROMETER);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_LINEAR_ACCELERATION)) {
            sensorTypeList.add(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_GYROSCOPE)) {
            sensorTypeList.add(Sensor.TYPE_GYROSCOPE);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_MAGNETIC_FIELD)) {
            sensorTypeList.add(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ROTATION_VECTOR)) {
            sensorTypeList.add(Sensor.TYPE_ROTATION_VECTOR);
        }

        return sensorTypeList;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelJsonObject.addProperty(X_KEY, sensorEvent.values[0]);
                accelJsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
                accelJsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linAccelJsonObject.addProperty(X_KEY, sensorEvent.values[0]);
                linAccelJsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
                linAccelJsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeJsonObject.addProperty(X_KEY, sensorEvent.values[0]);
                gyroscopeJsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
                gyroscopeJsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticJsonObject.addProperty(X_KEY, sensorEvent.values[0]);
                magneticJsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
                magneticJsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                attitudeJsonObject.addProperty(X_KEY, sensorEvent.values[0]);
                attitudeJsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
                attitudeJsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
                // I read about a bug on some devices where the 4th element doesn't exist
                // so this is just a precaution so this does not crash
                if (sensorEvent.values.length > 3) {
                    attitudeJsonObject.addProperty(W_KEY, sensorEvent.values[3]);
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticJsonObject.addProperty(ACCURACY_KEY, i);
        }
    }
}
