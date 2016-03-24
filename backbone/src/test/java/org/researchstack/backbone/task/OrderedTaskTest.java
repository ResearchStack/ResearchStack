package org.researchstack.backbone.task;
import org.junit.Ignore;
import org.junit.Test;
import org.researchstack.backbone.step.Step;

/**
 * Created by bradleymcdermott on 3/24/16.
 */
public class OrderedTaskTest
{

    @Ignore
    @Test
    public void testGetStepAfterStep() throws Exception
    {

    }

    @Ignore
    @Test
    public void testGetStepBeforeStep() throws Exception
    {

    }

    @Ignore
    @Test
    public void testGetStepWithIdentifier() throws Exception
    {

    }

    @Ignore
    @Test
    public void testGetProgressOfCurrentStep() throws Exception
    {

    }

    @Test(expected = Task.InvalidTaskException.class)
    public void testValidateParametersDuplicate() throws Exception
    {
        Step stepOne = new Step("idOne");
        Step stepTwo = new Step("idTwo");
        Step stepDupe = new Step("idOne");
        Task task = new OrderedTask("id", stepOne, stepTwo, stepDupe);
        task.validateParameters();

    }

    @Test
    public void testValidateParametersValid() throws Exception
    {
        Step stepOne = new Step("idOne");
        Step stepTwo = new Step("idTwo");
        Task task = new OrderedTask("id", stepOne, stepTwo);
        task.validateParameters();
        // exception will be thrown if this is not valid and fail the test
    }

    @Ignore
    @Test
    public void testOrderedTask() throws Exception
    {

    }

    // NavigableOrderedTask stuff, move to its own test class
    @Ignore
    @Test
    public void testNavigableOrderedTask() throws Exception
    {

    }

    @Ignore
    @Test
    public void testNavigableOrderedTaskEmpty() throws Exception
    {

    }

    @Ignore
    @Test
    public void testNavigableOrderedTaskHeadache() throws Exception
    {

    }

    @Ignore
    @Test
    public void testNavigableOrderedTaskDizziness() throws Exception
    {

    }

    @Ignore
    @Test
    public void testNavigableOrderedTaskSevereHeadache() throws Exception
    {

    }

    @Ignore
    @Test
    public void testNavigableOrderedTaskLightHeadache() throws Exception
    {

    }

    @Ignore
    @Test
    public void testPredicateStepNavigationRule() throws Exception
    {

    }

    @Ignore
    @Test
    public void testDirectStepNavigationRule() throws Exception
    {

    }

    @Ignore
    @Test
    public void testResultPredicates() throws Exception
    {

    }

}