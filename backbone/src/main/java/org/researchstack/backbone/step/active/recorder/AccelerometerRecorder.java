package org.researchstack.backbone.step.active.recorder;

import android.hardware.Sensor;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by TheMDP on 2/5/17.
 *
 * This class uses the JsonArrayDataRecorder class to save the Accelerometer sensor's data as
 * an array of accelerometer json objects with timestamp, ax, ay, and az
 */
public class AccelerometerRecorder extends DeviceMotionRecorder {
    AccelerometerRecorder(double frequency, String identifier, Step step, File outputDirectory) {
        super(frequency, identifier, step, outputDirectory);
    }

    @Override
    protected List<Integer> getSensorTypeList(List<Sensor> availableSensorList) {
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER)) {
            return Collections.singletonList(Sensor.TYPE_ACCELEROMETER);
        }
        return Collections.emptyList();
    }
}
