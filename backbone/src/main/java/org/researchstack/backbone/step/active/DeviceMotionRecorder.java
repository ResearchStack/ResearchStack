package org.researchstack.backbone.step.active;

import org.researchstack.backbone.step.Step;

import java.io.File;

/**
 * Created by TheMDP on 2/5/17.
 */

public class DeviceMotionRecorder extends Recorder {
    /**
     * The frequency of accelerometer data collection in samples per second (Hz).
     */
    private double frequency;

    /** Default constructor for serialization/deserialization */
    DeviceMotionRecorder() {
        super();
    }

    DeviceMotionRecorder(String identifier, Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
    }

    @Override
    public void start() {
        // TODO: implement
    }

    @Override
    public void stop() {
        // TODO: implement
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
