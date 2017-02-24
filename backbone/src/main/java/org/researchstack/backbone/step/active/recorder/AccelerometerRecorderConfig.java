package org.researchstack.backbone.step.active.recorder;

import org.researchstack.backbone.step.Step;

import java.io.File;

/**
 * Created by TheMDP on 2/5/17.
 */

public class AccelerometerRecorderConfig extends RecorderConfig {

    /**
     * The frequency of accelerometer data collection in samples per second (Hz).
     */
    private double frequency;

    /** Default constructor used for serialization/deserialization */
    AccelerometerRecorderConfig() {
        super();
    }

    public AccelerometerRecorderConfig(String identifier, double frequency) {
        super(identifier);
        this.frequency = frequency;
    }

    @Override
    public Recorder recorderForStep(Step step, File outputDirectory) {
        return new AccelerometerRecorder(frequency, getIdentifier(), step, outputDirectory);
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
