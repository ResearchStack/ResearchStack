package org.researchstack.backbone.step.active.recorder;

import org.researchstack.backbone.step.Step;

import java.io.File;

/**
 * Created by TheMDP on 2/26/17.
 *
 * The `AudioRecorderConfig` class represents a configuration that records
 * audio data during an active step.
 *
 * An `AudioRecorderConfig` generates an `AudioRecorder` object.
 *
 * To use a recorder, include its configuration in the `recorderConfigurationList` property
 * of an `ActiveStep` object, include that step in a task, and present it with
 * an 'ActiveTaskActivity'.
 *
 */

public class AudioRecorderConfig extends RecorderConfig {

    private AudioRecorderSettings settings;

    /** Default constructor used for serialization/deserialization */
    AudioRecorderConfig() {
        super();
    }

    public AudioRecorderConfig(AudioRecorderSettings settings, String identifier) {
        super(identifier);
        this.settings = settings;
    }

    @Override
    public Recorder recorderForStep(Step step, File outputDirectory) {
        return new AudioRecorder(settings, identifier, step, outputDirectory);
    }

    public AudioRecorderSettings getSettings() {
        return settings;
    }

    public void setSettings(AudioRecorderSettings settings) {
        this.settings = settings;
    }
}
