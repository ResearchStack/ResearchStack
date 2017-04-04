package org.researchstack.backbone.model.taskitem.factory;

import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.researchstack.backbone.model.survey.factory.SurveyFactoryHelper;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.step.CompletionStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.AudioStep;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.FitnessStep;
import org.researchstack.backbone.step.active.TappingIntervalStep;
import org.researchstack.backbone.step.active.WalkingTaskStep;
import org.researchstack.backbone.task.Task;

import static junit.framework.Assert.*;
import static org.researchstack.backbone.task.factory.TremorTaskFactory.*;

/**
 * Created by TheMDP on 1/5/17.
 *
 * The TaskItemFactoryTests will test the deserialization of JSON into their corresponding Task objects
 */

public class TaskItemFactoryTests {

    private SurveyFactoryHelper helper;

    @Before
    public void setUp() throws Exception {

        helper = new SurveyFactoryHelper();

        PackageManager packageManager = Mockito.mock(PackageManager.class);
        Mockito.when(helper.mockContext.getPackageManager()).thenReturn(packageManager);
    }

    @Test
    public void testTappingTaskWithLocalizedSteps() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tapping-ABCD-1234\",\"schemaIdentifier\":\"Tapping Activity\",\"taskType\":\"tapping\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":12.0,\"handOptions\":\"right\"},\"localizedSteps\":[{\"identifier\":\"conclusion\",\"type\":\"instruction\",\"title\":\"Title 123\",\"text\":\"Text 123\",\"detailText\":\"Detail Text 123\"}]}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(helper.mockContext, taskItem);

        {
            assertNotNull(task);
            assertEquals("Tapping Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof CompletionStep) {
                        assertEquals("conclusion", step.getIdentifier());
                        assertEquals("Title 123", step.getTitle());
                        assertEquals("Text 123", step.getText());
                        assertEquals("Detail Text 123", ((CompletionStep) step).getMoreDetailText());
                    }
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("instruction")) {
                            assertEquals("intended Use Description Text", step.getText());
                        }
                    }
                    if (step instanceof TappingIntervalStep) {
                        // The inputTaskString specifies that it should ONLY be right hand with 12 seconds duration
                        TappingIntervalStep substep = (TappingIntervalStep)step;
                        assertEquals(12, substep.getStepDuration());
                        assertEquals("tapping.right", substep.getIdentifier());
                    }
                    stepCount++;
                }
            } while (step != null);

            // intro step, a right hand instruction and a right hand tapping step, and a conclusion
            assertEquals(4, stepCount);
        }

        String s = "{“taskIdentifier\":\"1-Voice-ABCD-1234\",\"schemaIdentifier\":\"VoiceActivity\",\"taskType\":\"voice\",\"intendedUseDescription\":\"intendedUseDescriptionText\",\"taskOptions\":[{\"duration\":10.0,\"speechInstruction\":\"SpeechInstruction\",\"shortSpeechInstruction\":\"ShortSpeechInstruction\"}]}";
    }

    @Test
    public void testVoiceTask() {
        String inputTaskString = "{“taskIdentifier\":\"1-Voice-ABCD-1234\",\"schemaIdentifier\":\"Voice Activity\",\"taskType\":\"voice\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":10.0,\"speechInstruction\":\"Speech Instruction\",\"shortSpeechInstruction\":\"Short Speech Instruction\"}}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(helper.mockContext, taskItem);

        {
            assertNotNull(task);
            assertEquals("Voice Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("instruction")) {
                            assertEquals("intended Use Description Text", step.getText());
                        }
                        if (step.getIdentifier().equals("instruction1")) {
                            assertEquals("Speech Instruction", step.getText());
                        }
                    }
                    if (step instanceof AudioStep) {
                        AudioStep substep = (AudioStep)step;
                        assertEquals(10, substep.getStepDuration());
                        assertEquals("Short Speech Instruction", step.getTitle());
                    }
                    stepCount++;
                }
            } while (step != null);

            // 2 intro steps, a countdown, an audio step, and a conclusion, audio too loud is skipped
            assertEquals(5, stepCount);
        }
    }

    @Test
    public void testWalkingTask() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Walking-ABCD-1234\",\"schemaIdentifier\":\"Walking Activity\",\"taskType\":\"walking\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"walkDuration\":45.0,\"restDuration\":20.0}}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(helper.mockContext, taskItem);

        {
            assertNotNull(task);
            assertEquals("Walking Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("instruction")) {
                            assertEquals("intended Use Description Text", step.getText());
                        }
                    }
                    if (step instanceof WalkingTaskStep) {
                        WalkingTaskStep substep = (WalkingTaskStep)step;
                        assertEquals(45, substep.getStepDuration());
                    }
                    if (step instanceof FitnessStep) {
                        FitnessStep substep = (FitnessStep)step;
                        assertEquals(20, substep.getStepDuration());
                    }
                    stepCount++;
                }
            } while (step != null);

            // 2 intro steps, a countdown, an walking step, a rest step, and a conclusion
            assertEquals(6, stepCount);
        }
    }

    @Test
    public void testShortWalkTask() {
        String inputTaskString = "{\"taskIdentifier\":\"4-APHTimedWalking-80F09109-265A-49C6-9C5D-765E49AAF5D9\",\"schemaIdentifier\":\"Walking Activity\",\"taskType\":\"shortWalk\",\"taskOptions\":{\"restDuration\":30.0,\"numberOfStepsPerLeg\":100.0},\"removeSteps\":[\"walking.return\"],\"localizedSteps\":[{\"identifier\":\"instruction\",\"type\":\"instruction\",\"text\":\"This activity measures your gait (walk) and balance, which can be affected by Parkinson disease.\",\"detailText\":\"Please do not continue if you cannot safely walk unassisted.\"},{\"identifier\":\"instruction1\",\"type\":\"instruction\",\"text\":\"\\u2022 Please wear a comfortable pair of walking shoes and find a flat, smooth surface for walking.\\n\\n\\u2022 Try to walk continuously by turning at the ends of your path, as if you are walking around a cone.\\n\\n\\u2022 Importantly, walk at your normal pace. You do not need to walk faster than usual.\",\"detailText\":\"Put your phone in a pocket or bag and follow the audio instructions.\"},{\"identifier\":\"walking.outbound\",\"type\":\"active\",\"stepDuration\":30.0,\"text\":\"Walk back and forth for 30 seconds.\",\"stepSpokenInstruction\":\"Walk back and forth for 30 seconds.\"},{\"identifier\":\"walking.rest\",\"type\":\"active\",\"stepDuration\":30.0,\"text\":\"Turn around 360 degrees, then stand still, with your feet about shoulder-width apart. Rest your arms at your side and try to avoid moving for 30 seconds.\",\"stepSpokenInstruction\":\"Turn around 360 degrees, then stand still, with your feet about shoulder-width apart. Rest your arms at your side and try to avoid moving for 30 seconds.\"}]}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(helper.mockContext, taskItem);

        {
            assertNotNull(task);
            assertEquals("Walking Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("instruction")) {
                            assertEquals("This activity measures your gait (walk) and balance, which can be affected by Parkinson disease.", step.getText());
                            assertEquals("Please do not continue if you cannot safely walk unassisted.", ((InstructionStep) step).getMoreDetailText());
                        }
                        if (step.getIdentifier().equals("instruction1")) {
                            assertEquals("\u2022 Please wear a comfortable pair of walking shoes and find a flat, smooth surface for walking.\n\n\u2022 Try to walk continuously by turning at the ends of your path, as if you are walking around a cone.\n\n\u2022 Importantly, walk at your normal pace. You do not need to walk faster than usual.", step.getText());
                            assertEquals("Put your phone in a pocket or bag and follow the audio instructions.", ((InstructionStep) step).getMoreDetailText());
                        }
                    }
                    if (step instanceof WalkingTaskStep) {
                        assertEquals("walking.outbound", step.getIdentifier());
                        WalkingTaskStep substep = (WalkingTaskStep)step;
                        assertEquals(30, substep.getStepDuration());
                        assertEquals(100, substep.getNumberOfStepsPerLeg());
                        assertEquals("Walk back and forth for 30 seconds.", substep.getText());
                        assertEquals("Walk back and forth for 30 seconds.", substep.getSpokenInstruction());
                    }
                    if (step instanceof FitnessStep) {
                        assertEquals("walking.rest", step.getIdentifier());
                        FitnessStep substep = (FitnessStep)step;
                        assertEquals(30, substep.getStepDuration());
                        assertEquals("Turn around 360 degrees, then stand still, with your feet about shoulder-width apart. Rest your arms at your side and try to avoid moving for 30 seconds.", substep.getText());
                        assertEquals("Turn around 360 degrees, then stand still, with your feet about shoulder-width apart. Rest your arms at your side and try to avoid moving for 30 seconds.", substep.getSpokenInstruction());
                    }
                    stepCount++;
                }
            } while (step != null);

            // 2 intro steps, a countdown, an walking step (only one way), a rest step, and a conclusion
            assertEquals(6, stepCount);
        }
    }

    @Test
    public void testTremorTask() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tremor-ABCD-1234\",\"schemaIdentifier\":\"Tremor Activity\",\"taskType\":\"tremor\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":10.0,\"handOptions\":\"right\"}}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(helper.mockContext, taskItem);

        {
            assertNotNull(task);
            assertEquals("Tremor Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("instruction")) {
                            assertEquals("intended Use Description Text", step.getText());
                        }
                    }
                    if (step instanceof ActiveStep && !(step instanceof CountdownStep)) {
                        // The inputTaskString specifies that it should ONLY be right hand with 12 seconds duration
                        ActiveStep substep = (ActiveStep)step;
                        assertEquals(10, substep.getStepDuration());
                        assertTrue(substep.getIdentifier().contains("right"));
                    }
                    stepCount++;
                }
            } while (step != null);

            // 7 intro steps, 5 countdown, 5 active step, a rest step, and a conclusion
            assertEquals(18, stepCount);
        }
    }

    @Test
    public void testTremorTaskBothHandsExcludeNoseAndElbowBent() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tremor-ABCD-1234\",\"schemaIdentifier\":\"Tremor Activity\",\"taskType\":\"tremor\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":10.0,\"handOptions\":\"both\",\"excludePositions\":[\"handAtShoulderLength\", \"elbowBent\"]}}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory();
        Task task = factory.createTask(helper.mockContext, taskItem);

        {
            assertNotNull(task);
            assertEquals("Tremor Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("instruction")) {
                            assertEquals("intended Use Description Text", step.getText());
                        }
                    }
                    if (step instanceof ActiveStep && !(step instanceof CountdownStep)) {
                        // The inputTaskString specifies that it should ONLY be right hand with 12 seconds duration
                        ActiveStep substep = (ActiveStep)step;
                        assertEquals(10, substep.getStepDuration());
                        // Check that
                        assertFalse(substep.getIdentifier().contains(TremorTestExtendArmStepIdentifier));
                        assertFalse(substep.getIdentifier().contains(TremorTestBendArmStepIdentifier));
                    }
                    stepCount++;
                }
            } while (step != null);

            // 23 represents both hands' instructions, countdowns, and active steps
            assertEquals(23, stepCount);
        }
    }
}
