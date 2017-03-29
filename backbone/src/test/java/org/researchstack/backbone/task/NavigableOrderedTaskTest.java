package org.researchstack.backbone.task;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.model.survey.factory.SurveyFactoryHelper;
import org.researchstack.backbone.onboarding.MockResourceManager;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.step.ToggleFormStep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * NavigableOrderedTask is a class in RK that does many of the same things as
 * SmartSurveyTask. In the future it would be nice to implement this to replace SmartSurveyTask.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourcePathManager.class, ResourceManager.class})
public class NavigableOrderedTaskTest
{
    SurveyFactoryHelper mSurveyFactoryHelper;

    @Before
    public void setUp() throws Exception
    {
        mSurveyFactoryHelper = new SurveyFactoryHelper();

        // All of this, along with the @PrepareForTest and @RunWith above, is needed
        // to mock the resource manager to load resources from the directory src/test/resources
        PowerMockito.mockStatic(ResourcePathManager.class);
        PowerMockito.mockStatic(ResourceManager.class);
        MockResourceManager resourceManager = new MockResourceManager();
        PowerMockito.when(ResourceManager.getInstance()).thenReturn(resourceManager);
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "eligibilityrequirements");
    }

    private String getJsonResource(String resourceName) {
        ResourcePathManager.Resource resource = ResourceManager.getInstance().getResource(resourceName);
        return ResourceManager.getResourceAsString(mSurveyFactoryHelper.mockContext, resourceName);
    }

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

        String[] expectedOrder = new String[] {
                "step1", "step2", "A.step1", "A.step2", "A.step3",
                "B.step1", "B.step2", "step3", "step4", "step5" };

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

        } while (step != null && step.getIdentifier().equals(expectedIdentifier) && ++idx < expectedOrder.length);

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

        String[] expectedOrder = new String[] {
                "step1", "step2", "stepA.1", "stepB.1", "stepB.2", "stepA.2" };

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

        } while (step != null && step.getIdentifier().equals(expectedIdentifier) && ++idx < expectedOrder.length);

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

    @Test
    public void testNavigationExpectedAnswerRulesPassed() {
        String eligibilityJson = getJsonResource("eligibilityrequirements");
        OnboardingSection section = mSurveyFactoryHelper.gson.fromJson(eligibilityJson, OnboardingSection.class);

        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(mSurveyFactoryHelper.mockContext, section.surveyItems);

        String taskId = "Parent Task";
        NavigableOrderedTask task = new NavigableOrderedTask(taskId, stepList);
        TaskResult result = new TaskResult(taskId);

        Step step = task.getStepAfterStep(null, result);
        assertTrue(step instanceof ToggleFormStep);
        ToggleFormStep toggleFormStep = (ToggleFormStep)step;

        StepResult<Boolean> question1Result = new StepResult<>(toggleFormStep.getFormSteps().get(0));
        question1Result.setResult(true);
        result.getResults().put(toggleFormStep.getFormSteps().get(0).getIdentifier(), question1Result);

        StepResult<Boolean> question2Result = new StepResult<>(toggleFormStep.getFormSteps().get(1));
        question2Result.setResult(true);
        result.getResults().put(toggleFormStep.getFormSteps().get(1).getIdentifier(), question2Result);

        StepResult<Boolean> question3Result = new StepResult<>(toggleFormStep.getFormSteps().get(2));
        question3Result.setResult(true);
        result.getResults().put(toggleFormStep.getFormSteps().get(2).getIdentifier(), question3Result);

        // Since we answered all the questions with the correct "expectedAnswer"
        // We should see the eligible instruction
        step = task.getStepAfterStep(step, result);
        assertEquals("eligibleInstruction", step.getIdentifier());
    }

    @Test
    public void testNavigationExpectedAnswerRulesFailed() {
        String eligibilityJson = getJsonResource("eligibilityrequirements");
        OnboardingSection section = mSurveyFactoryHelper.gson.fromJson(eligibilityJson, OnboardingSection.class);

        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(mSurveyFactoryHelper.mockContext, section.surveyItems);

        String taskId = "Parent Task";
        NavigableOrderedTask task = new NavigableOrderedTask(taskId, stepList);
        TaskResult result = new TaskResult(taskId);

        Step step = task.getStepAfterStep(null, result);
        assertTrue(step instanceof ToggleFormStep);
        ToggleFormStep toggleFormStep = (ToggleFormStep)step;

        StepResult<Boolean> question1Result = new StepResult<>(toggleFormStep.getFormSteps().get(0));
        question1Result.setResult(true);
        result.getResults().put(toggleFormStep.getFormSteps().get(0).getIdentifier(), question1Result);

        StepResult<Boolean> question2Result = new StepResult<>(toggleFormStep.getFormSteps().get(1));
        question2Result.setResult(false);
        result.getResults().put(toggleFormStep.getFormSteps().get(1).getIdentifier(), question2Result);

        StepResult<Boolean> question3Result = new StepResult<>(toggleFormStep.getFormSteps().get(2));
        question3Result.setResult(true);
        result.getResults().put(toggleFormStep.getFormSteps().get(2).getIdentifier(), question3Result);

        // Since we answered all the questions with the correct "expectedAnswer"
        // We should see the eligible instruction
        step = task.getStepAfterStep(step, result);
        assertEquals("ineligibleInstruction", step.getIdentifier());

        step = task.getStepAfterStep(step, result);
        assertNull(step);
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