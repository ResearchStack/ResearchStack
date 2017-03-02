package org.researchstack.backbone.step.active.recorder;

import org.researchstack.backbone.step.Step;

import java.io.File;
import java.io.Serializable;

/**
 * Created by TheMDP on 2/4/17.
 *
 * /**
 * The `RecorderConfig` class is the abstract base class for recorder configurations
 * that can be attached to an active step (`ActiveStep`).
 *
 * Recorder configurations provide an easy way to collect
 * sensor data into a serialized format during the duration of an active step.
 * If you want to filter or process the data in real time, it is better to
 * use the existing APIs directly.
 *
 * To use a recorder, include its configuration in the `recorderConfigurations` property
 * of an `ORKActiveStep` object, include that step in a task, and present it with
 * a 'ViewTaskActivity'.
 *
 * To add a new recorder, subclass both `RecorderConfig` and `Recorder`,
 * and add the new `RecorderConfig` subclass to an `ActiveStep` object.
 */

public abstract class RecorderConfig implements Serializable {

    /**
     * A short string that uniquely identifies the recorder configuration within the step.
     *
     * The identifier is reproduced in the results of a recorder created from this configuration.
     * In fact, the only way to link a result (an `FileResult` object) to the recorder
     * that generated it is to look at the value of `identifier`.
     * To accurately identify recorder results, you need to ensure that recorder identifiers
     * are unique within each step.
     *
     * In some cases, it can be useful to link the recorder identifier to a unique identifier in a
     * database; in other cases, it can make sense to make the identifier human
     * readable.
     */
    protected String identifier;

    /** Default constructor used for serialization/deserialization */
    RecorderConfig() {
        super();
    }

    RecorderConfig(String identifier) {
        super();
        this.identifier = identifier;
    }

    /**
     * Returns a recorder instance using this configuration.
     *
     * @param step                The step for which this recorder is being created.
     * @param outputDirectory     The directory in which all output file data should be written (if producing `FileResult` instances).
     *
     * @return A configured recorder instance.
     */
    public abstract Recorder recorderForStep(Step step, File outputDirectory);

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
