package org.researchstack.backbone.model.taskitem.factory;

import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.model.survey.factory.ResourceParserHelper;
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

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 1/5/17.
 *
 * The TaskItemFactoryTests will test the deserialization of JSON into their corresponding Task objects
 */

public class TaskItemFactoryTests {

    SurveyFactoryHelper  helper;
    ResourceParserHelper resourceHelper;

    @Before
    public void setUp() throws Exception {

        helper = new SurveyFactoryHelper();
        resourceHelper = new ResourceParserHelper();
    }

    @Test
    public void testTappingTaskWithLocalizedSteps() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tapping-ABCD-1234\",\"schemaIdentifier\":\"Tapping Activity\",\"taskType\":\"tapping\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":12.0,\"handOptions\":\"right\"},\"localizedSteps\":[{\"identifier\":\"conclusion\",\"type\":\"instruction\",\"title\":\"Title 123\",\"text\":\"Text 123\",\"detailText\":\"Detail Text 123\"}]}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory(helper.mockContext, Collections.singletonList(taskItem));

        {
            Task task = factory.getTaskList().get(0);

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
                        if (step.getIdentifier().equals("Instruction0StepIdentifier")) {
                            assertEquals("intended Use Description Text", step.getTitle());
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
        TaskItemFactory factory = new TaskItemFactory(helper.mockContext, Collections.singletonList(taskItem));

        {
            Task task = factory.getTaskList().get(0);

            assertNotNull(task);
            assertEquals("Voice Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("Instruction0StepIdentifier")) {
                            assertEquals("intended Use Description Text", step.getTitle());
                        }
                        if (step.getIdentifier().equals("Instruction1StepIdentifier")) {
                            assertEquals("Speech Instruction", step.getTitle());
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
        TaskItemFactory factory = new TaskItemFactory(helper.mockContext, Collections.singletonList(taskItem));

        {
            Task task = factory.getTaskList().get(0);

            assertNotNull(task);
            assertEquals("Walking Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("Instruction0StepIdentifier")) {
                            assertEquals("intended Use Description Text", step.getTitle());
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
    public void testTremorTask() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tremor-ABCD-1234\",\"schemaIdentifier\":\"Tremor Activity\",\"taskType\":\"tremor\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":10.0,\"handOptions\":\"right\"}}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory(helper.mockContext, Collections.singletonList(taskItem));

        {
            Task task = factory.getTaskList().get(0);

            assertNotNull(task);
            assertEquals("Tremor Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("Instruction0StepIdentifier")) {
                            assertEquals("intended Use Description Text", step.getTitle());
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

/*
// Currently this json is INVALID and throws a MalformedJsonException
// Skip this test until we can fix it on the server or find a way to parse bad json

    @Test
    public void testTremorTaskBothHandsExcludeNoseAndElbowBent() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tremor-ABCD-1234\",\"schemaIdentifier\":\"Tremor Activity\",\"taskType\":\"tremor\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":10.0,\"handOptions\":\"both\",\"excludePostions”:[“elbowBent\",\"touchNose”]}}";
        TaskItem taskItem = helper.gson.fromJson(inputTaskString, TaskItem.class);
        TaskItemFactory factory = new TaskItemFactory(helper.mockContext, Collections.singletonList(taskItem));

        {
            Task task = factory.getTaskList().get(0);

            assertNotNull(task);
            assertEquals("Tremor Activity", task.getIdentifier());

            Step step = null;
            int stepCount = 0;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    if (step instanceof InstructionStep) {
                        if (step.getIdentifier().equals("Instruction0StepIdentifier")) {
                            assertEquals("intended Use Description Text", step.getTitle());
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
*/
}
