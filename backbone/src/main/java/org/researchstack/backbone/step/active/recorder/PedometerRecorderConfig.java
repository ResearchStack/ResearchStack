package org.researchstack.backbone.step.active.recorder;

import org.researchstack.backbone.step.Step;

import java.io.File;

/**
 * Created by TheMDP on 2/15/17.
 */

public class PedometerRecorderConfig extends RecorderConfig {

    /** Default constructor used for serialization/deserialization */
    PedometerRecorderConfig() {
        super();
    }

    public PedometerRecorderConfig(String identifier) {
        super(identifier);
    }

    @Override
    public Recorder recorderForStep(Step step, File outputDirectory) {
        return new PedometerRecorder(getIdentifier(), step, outputDirectory);
    }
}
