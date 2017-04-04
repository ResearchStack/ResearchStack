package org.researchstack.backbone.utils;

import org.junit.Test;
import org.researchstack.backbone.result.AudioResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.active.AudioStep;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.step.layout.StepLayout;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by TheMDP on 1/19/17.
 */

public class StepResultHelperTests {


    @Test
    public void testFindStepClass_Custom() {
        StepResult result0 = new StepResult(new InstructionStep("g", null, null));
        Result result1 = new Result("a");
        result0.getResults().put(result1.getIdentifier(), result1);
        Result result2 = new Result("b");
        result0.getResults().put(result2.getIdentifier(), result2);
        Result result3 = new Result("c");
        result0.getResults().put(result3.getIdentifier(), result3);
        StepResult result4 = new StepResult(new InstructionStep("d", null, null));
        CustomResult customResult = new CustomResult("e");
        result4.setResult(customResult);
        result0.getResults().put(result4.getIdentifier(), result4);

        CustomResult result = StepResultHelper.findResultOfClass(result0, new StepResultHelper.ResultClassComparator<CustomResult>() {
            public boolean isTypeOfClass(Object object) {
                return object instanceof CustomResult;
            }
        });

        assertNotNull(result);
    }

    public static class CustomResult extends Result {
        public CustomResult(String identifier) {
            super(identifier);
        }
    }

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
