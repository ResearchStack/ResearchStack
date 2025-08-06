package org.researchstack.backbone.task;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TaskTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetTitleForStep() throws Exception {
        Task task = new TaskImpl("id");

        Context mockContext = Mockito.mock(Context.class);
        Step mockStepWithoutTitle = Mockito.mock(Step.class);
        Mockito.when(mockStepWithoutTitle.getStepTitle()).thenReturn(0);
        String noTitle = task.getTitleForStep(mockContext, mockStepWithoutTitle);
        assertEquals("Gets empty string when no title id on step", "", noTitle);
    }

    private static class TaskImpl extends Task {
        public TaskImpl(String identifier) {
            super(identifier);
        }

        @Override
        public Step getStepAfterStep(Step step, TaskResult result) {
            return null;
        }

        @Override
        public Step getStepBeforeStep(Step step, TaskResult result) {
            return null;
        }

        @Override
        public Step getStepWithIdentifier(String identifier) {
            return null;
        }

        @Override
        public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
            return null;
        }

        @Override
        public void validateParameters() {

        }
    }
}