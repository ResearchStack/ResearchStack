package org.researchstack.backbone.step;

import org.researchstack.backbone.result.AudioResult;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.StepResultHelper;

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
        // Check if audio is too loud by using a rolling average in AudioResult object
        StepResult stepResult = StepResultHelper.findStepResult(result, audioStepResultIdentifier);
        if (stepResult != null && !stepResult.getResults().keySet().isEmpty()) {
            for (Object key : stepResult.getResults().keySet()) {
                Object value = stepResult.getResults().get(key);
                if (value instanceof AudioResult) {
                    AudioResult audioResult = (AudioResult)value;
                    boolean isResultTooLoud = audioResult.getRollingAverageOfVolume() > loudnessThreshold;

                    LogExt.i(getClass(), "Audio is " + (isResultTooLoud ? "" : "not") +
                            " too loud with value of " + audioResult.getRollingAverageOfVolume());

                    isSkippingStep = !isResultTooLoud;
                    return isSkippingStep;
                }
            }
        }
        isSkippingStep = true;
        return true;
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
