package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.SystemClock;

import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * This class uses the JsonArrayDataRecorder class to save the Accelerometer sensor's data as
 * an array of accelerometer json objects with timestamp, ax, ay, and az
 */

public class AccelerometerRecorder extends SensorRecorder {

    public static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    public static final String UPTIME_IN_SECONDS_KEY = "uptime";
    // TODO: uptime
    public static final String ACCELERATION_X_KEY   = "x";
    public static final String ACCELERATION_Y_KEY   = "y";
    public static final String ACCELERATION_Z_KEY   = "z";

    private JsonObject jsonObject;

    AccelerometerRecorder(double frequency, String identifier, Step step, File outputDirectory) {
        super(frequency, identifier, step, outputDirectory);
    }

    @Override
    protected List<Integer> getSensorTypeList(List<Sensor> availableSensorList) {
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER)) {
            return Collections.singletonList(Sensor.TYPE_ACCELEROMETER);
        }
        return new ArrayList<>();
    }

    @Override
    public void start(Context context) {
        super.start(context);
        if (isRecording()) {
            jsonObject = new JsonObject();
        }
    }

    @Override
    public void recordSensorEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                jsonObject.addProperty(ACCELERATION_X_KEY, sensorEvent.values[0]);
                jsonObject.addProperty(ACCELERATION_Y_KEY, sensorEvent.values[1]);
                jsonObject.addProperty(ACCELERATION_Z_KEY, sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // NO-OP
    }
}
