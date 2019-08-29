package org.sagebionetworks.researchstack.backbone.step;

import org.sagebionetworks.researchstack.backbone.result.TaskResult;
import org.sagebionetworks.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.sagebionetworks.researchstack.backbone.task.NavigableOrderedTask;
import org.sagebionetworks.researchstack.backbone.utils.LogExt;

import java.util.List;

/**
 * Created by TheMDP on 2/26/17.
 *
 * The AudioTooLoudStep is designed to be placed after an ActiveStep with identifier audioStepResultIdentifier
 * The ActiveStep should also have an AudioRecorder that produces an AudioResult that can be
 * analyzed in this state to see if this should show to the user that their background noise
 * and the recording environment they are in, is too loud, and therefore unsatisfactory for
 * the proceeding ActiveStep with AudioRecorder to collect accurate data
 */

public class AudioTooLoudStep extends InstructionStep implements NavigableOrderedTask.NavigationSkipRule {

    /**
     * The step identifier where the AudioResult object will be
     */
    private String audioStepResultIdentifier;

    /**
     * Must be a value from 0.0 - 1.0
     * Step will be skipped unless the loudness threshold is over this threshold
     */
    private double loudnessThreshold;

    private boolean isSkippingStep = false;

    /* Default constructor needed for serialization/deserialization of object */
    AudioTooLoudStep() {
        super();
    }

    public AudioTooLoudStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public boolean shouldSkipStep(TaskResult result, List<TaskResult> additionalTaskResults) {
        boolean isResultTooLoud = AudioRecorder.getLastTotalSampleAvg() > loudnessThreshold;
        LogExt.i(getClass(), "Audio is " + (isResultTooLoud ? "" : "not") +
                " too loud with value of " + AudioRecorder.getLastTotalSampleAvg());
        isSkippingStep = !isResultTooLoud;
        return !isResultTooLoud;
    }

    @Override
    public String nextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        // Only use the next step id if we didn't skip this step, otherwise, proceed as normal
        if (!isSkippingStep) {
            return nextStepIdentifier;
        } else {
            isSkippingStep = false;  // revert back to original operation
            return null;
        }
    }

    public String getAudioStepResultIdentifier() {
        return audioStepResultIdentifier;
    }

    public void setAudioStepResultIdentifier(String audioStepResultIdentifier) {
        this.audioStepResultIdentifier = audioStepResultIdentifier;
    }

    public double getLoudnessThreshold() {
        return loudnessThreshold;
    }

    public void setLoudnessThreshold(double loudnessThreshold) {
        this.loudnessThreshold = loudnessThreshold;
    }
}
