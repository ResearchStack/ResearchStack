package org.researchstack.backbone.task;

import org.junit.Ignore;
import org.junit.Test;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * NavigableOrderedTask is a class in RK that does many of the same things as
 * SmartSurveyTask. In the future it would be nice to implement this to replace SmartSurveyTask.
 */
public class NavigableOrderedTaskTest {
    @Test
    public void testNavigationWithSubtasks() {
        // Note: This test checks basic subtask navigation forward and backward

        OrderedTask baseTask = createOrderedTask("base", 5);
        SubtaskStep subtaskStepA = new SubtaskStep(createOrderedTask("A", 3));
        SubtaskStep subtaskStepB = new SubtaskStep(createOrderedTask("B", 2));

        List<Step> steps = new ArrayList<>(baseTask.getSteps());
        steps.add(2, subtaskStepA);
        steps.add(3, subtaskStepB);

        TaskResult taskResult = new TaskResult("base");

        NavigableOrderedTask task = new NavigableOrderedTask("base", steps);

        String[] expectedOrder = new String[]{
                "step1", "step2", "A.step1", "A.step2", "A.step3",
                "B.step1", "B.step2", "step3", "step4", "step5"};

        int idx = 0;
        Step step = null;
        String expectedIdentifier = null;

        // -- test stepAfterStep:withResult:

        do {
            // Add result for the given step
            if (step != null) {
                StepResult<String> stepResult = new StepResult(step);
                stepResult.setResult(step.getIdentifier());
                if (taskResult.getResults() != null) {
                    taskResult.getResults().put(stepResult.getIdentifier(), stepResult);
                } else {
                    Map<String, StepResult> resultMap = new LinkedHashMap<>();
                    resultMap.put(stepResult.getIdentifier(), stepResult);
                    taskResult.setResults(resultMap);
                }
            }

            // Get the next step
            expectedIdentifier = expectedOrder[idx];
            step = task.getStepAfterStep(step, taskResult);

            // Check expectations
            assertNotNull(step);
            assertEquals(step.getIdentifier(), expectedIdentifier);

        }
        while (step != null && step.getIdentifier().equals(expectedIdentifier) && ++idx < expectedOrder.length);

        // Check that exited while loop for expected reason
        assertNotNull(step);
        assertEquals(idx, expectedOrder.length);
        idx--;

        // -- test stepBeforeStep:withResult:

        while (step != null && step.getIdentifier().equals(expectedIdentifier) && (--idx >= 0)) {
            // Get the step before
            expectedIdentifier = expectedOrder[idx];
            String previousStepId = step.getIdentifier();
            step = task.getStepBeforeStep(step, taskResult);

            // Check expectations
            assertNotNull(step);
            assertEquals(step.getIdentifier(), expectedIdentifier);

            // Lop off the last result
            taskResult.getResults().remove(previousStepId);
        }
    }

    @Test
    public void testNavigationWithRules() {
        List<Step> steps = createSteps("step", 2);
        InstructionStep stepA1 = new InstructionStep("stepA.1", "", "");
        steps.add(stepA1);
        InstructionStep stepA2 = new InstructionStep("stepA.2", "", "");
        steps.add(stepA2);
        InstructionStep stepB1 = new InstructionStep("stepB.1", "", "");
        steps.add(stepB1);
        InstructionStep stepB2 = new InstructionStep("stepB.2", "", "");
        steps.add(stepB2);

        stepA1.setNextStepIdentifier("stepB.1");
        stepB2.setNextStepIdentifier("stepA.2");
        stepA2.setNextStepIdentifier("Exit");

        TaskResult taskResult = new TaskResult("base");

        NavigableOrderedTask task = new NavigableOrderedTask("base", steps);

        String[] expectedOrder = new String[]{
                "step1", "step2", "stepA.1", "stepB.1", "stepB.2", "stepA.2"};

        int idx = 0;
        Step step = null;
        String expectedIdentifier = null;

        // -- test stepAfterStep:withResult:

        do {
            // Add result for the given step
            if (step != null) {
                StepResult<String> stepResult = new StepResult(step);
                stepResult.setResult(step.getIdentifier());
                if (taskResult.getResults() != null) {
                    taskResult.getResults().put(stepResult.getIdentifier(), stepResult);
                } else {
                    Map<String, StepResult> resultMap = new LinkedHashMap<>();
                    resultMap.put(stepResult.getIdentifier(), stepResult);
                    taskResult.setResults(resultMap);
                }
            }

            // Get the next step
            expectedIdentifier = expectedOrder[idx];
            step = task.getStepAfterStep(step, taskResult);

            // ORKTaskViewController will look ahead to the next step and then look back to
            // see what navigation rules it should be using for buttons. Need to honor that flow.
            task.getStepAfterStep(step, taskResult);
            task.getStepBeforeStep(step, taskResult);

            // Check expectations
            assertNotNull(step);
            assertEquals(step.getIdentifier(), expectedIdentifier);

        }
        while (step != null && step.getIdentifier().equals(expectedIdentifier) && ++idx < expectedOrder.length);

        // Check that exited while loop for expected reason
        assertNotNull(step);
        assertEquals(idx, expectedOrder.length);
        idx--;

        // Check that the step after the last step is nil
        Step afterLast = task.getStepAfterStep(step, taskResult);
        assertTrue(afterLast == null);

        // -- test stepBeforeStep:withResult:

        while (step != null && step.getIdentifier().equals(expectedIdentifier) && (--idx >= 0)) {
            // Get the step before
            expectedIdentifier = expectedOrder[idx];
            String previousStepId = step.getIdentifier();
            step = task.getStepBeforeStep(step, taskResult);

            // Check expectations
            assertNotNull(step);
            assertEquals(step.getIdentifier(), expectedIdentifier);

            // Lop off the last result
            taskResult.getResults().remove(previousStepId);
        }
    }

    // Helper methods for the unit tests
    @Ignore
    OrderedTask createOrderedTask(String identifier, int numberOfSteps) {
        return new OrderedTask(identifier, createSteps("step", numberOfSteps));
    }

    @Ignore
    List<Step> createSteps(String idPrefix, int numberOfSteps) {
        List<Step> steps = new ArrayList<>();
        for (int i = 1; i <= numberOfSteps; i++) {
            Step step = new InstructionStep(idPrefix + i, "Step " + idPrefix + i, "");
            steps.add(step);
        }
        return steps;
    }
}