package org.researchstack.backbone.result;

import java.io.File;

/**
 * Created by TheMDP on 2/26/17.
 */

public class AudioResult extends FileResult {

    /**
     * A value from 0.0 - 1.0
     * The rolling average of the audio data,
     * can be used to get an overall picture of the audio's background noise
     * For instance, you can not allow the user to do a step if this is > 0.45
     */
    private double rollingAverageOfVolume;

    /* Default identifier for serialization/deserialization */
    AudioResult() {
        super();
    }

    public AudioResult(String identifier) {
        super(identifier);
    }

    public AudioResult(String identifier, File file, String contentType) {
        super(identifier, file, contentType);
    }

    public double getRollingAverageOfVolume() {
        return rollingAverageOfVolume;
    }

    public void setRollingAverageOfVolume(double rollingAverageOfVolume) {
        this.rollingAverageOfVolume = rollingAverageOfVolume;
    }
}
