package org.researchstack.backbone.task;

import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.step.Step;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


public class OrderedTaskTest {

    OrderedTask testTask;
    private Step stepOne = new Step("idOne");
    private Step stepTwo = new Step("idTwo");
    private Step stepThree = new Step("idThree");
    private Step stepOneDupe = new Step("idOne");

    @Before
    public void setUp() throws Exception {
        testTask = new OrderedTask("id", stepOne, stepTwo, stepThree);
    }

    @Test
    public void testGetStepAfterStep() throws Exception {
        Step nextStep = testTask.getStepAfterStep(stepOne, null);
        assertEquals("Returns the next sequential step in the task", stepTwo, nextStep);
        nextStep = testTask.getStepAfterStep(stepThree, null);
        assertNull("Returns null if passed in step is the last", nextStep);
    }

    @Test
    public void testGetStepBeforeStep() throws Exception {
        Step previousStep = testTask.getStepBeforeStep(stepThree, null);
        assertEquals("Returns the previous sequential step in the task", stepTwo, previousStep);
        previousStep = testTask.getStepBeforeStep(stepOne, null);
        assertNull("Returns null if passed in step is the first", previousStep);
    }

    @Test
    public void testGetStepWithIdentifier() throws Exception {
        Step foundStep = testTask.getStepWithIdentifier("idOne");
        assertEquals("Finds step if it is in the task", stepOne, foundStep);
        foundStep = testTask.getStepWithIdentifier("non-existent");
        assertNull("Returns null if no step in the task has that identifier", foundStep);
    }

    @Test
    public void testGetProgressOfCurrentStep() throws Exception {
        Task.TaskProgress progress = testTask.getProgressOfCurrentStep(stepOne, null);
        assertEquals("Current is accurate for first step (0)", 0, progress.getCurrent());
        progress = testTask.getProgressOfCurrentStep(stepTwo, null);
        assertEquals("Current is accurate for second step (1)", 1, progress.getCurrent());
        assertEquals("Total is accurate for task", 3, progress.getTotal());
    }

    @Test(expected = Task.InvalidTaskException.class)
    public void testValidateParametersDuplicate() throws Exception {
        Task invalidTask = new OrderedTask("id", stepOne, stepTwo, stepOneDupe);
        invalidTask.validateParameters();
    }

    @Test
    public void testValidateParametersValid() throws Exception {
        // exception will be thrown if this is not valid and fail the test
        testTask.validateParameters();
    }

    @Test
    public void testGetSteps() throws Exception {
        List<Step> steps = testTask.getSteps();
        assertEquals("Returns list of proper length", 3, steps.size());
        assertTrue("Contains expected step", steps.contains(stepTwo));
        steps.add(stepOneDupe);
        steps = testTask.getSteps();
        assertEquals("Adding items to the returned list doesn't affect task's internal list",
                3,
                steps.size());
        assertFalse("Does not contain illegally added step", steps.contains(stepOneDupe));
    }
}