package org.researchstack.backbone.utils;

import org.junit.Test;
import org.researchstack.backbone.result.AudioResult;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.active.AudioStep;
import org.researchstack.backbone.step.active.CountdownStep;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by TheMDP on 1/19/17.
 */

public class StepResultHelperTests {

    @Test
    public void testFindStepWithIdSimple() {
        TaskResult taskResult = audioTaskResult();

        StepResult findCountdownStepResult = StepResultHelper.findStepResult(taskResult, "countdown");
        assertNotNull(findCountdownStepResult);
        assertEquals(findCountdownStepResult.getIdentifier(), "countdown");

        StepResult findAudioStep = StepResultHelper.findStepResult(taskResult, "audiostep");
        assertNotNull(findAudioStep);
        assertEquals(findAudioStep.getIdentifier(), "audiostep");
    }

    private TaskResult audioTaskResult() {
        // This test is based on the results of the audio task step result
        TaskResult taskResult = new TaskResult("taskresultid");

        CountdownStep countdownStep = new CountdownStep("countdown");
        StepResult<AudioResult> stepResult1 = new StepResult<>(countdownStep);
        AudioResult audio1 = new AudioResult("audio1", new File("a1.mp4"), "audio/mpeg");
        stepResult1.setResult(audio1);
        taskResult.setStepResultForStepIdentifier(stepResult1.getIdentifier(), stepResult1);

        AudioStep audioStep = new AudioStep("audiostep");
        StepResult<AudioResult> stepResult2 = new StepResult<>(audioStep);
        AudioResult audio2 = new AudioResult("audio2", new File("a2.mp4"), "audio/mpeg");
        stepResult2.setResult(audio2);
        taskResult.setStepResultForStepIdentifier(stepResult2.getIdentifier(), stepResult2);

        return taskResult;
    }
}
