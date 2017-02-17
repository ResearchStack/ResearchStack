package org.researchstack.backbone.task.factory;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.PedometerRecorder;
import org.researchstack.backbone.step.active.PedometerRecorderConfig;
import org.researchstack.backbone.step.active.RecorderConfig;
import org.researchstack.backbone.step.active.WalkingTaskStep;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;
import static org.researchstack.backbone.task.factory.TremorTaskFactory.tremorTask;
import static org.researchstack.backbone.task.factory.WalkingTaskFactory.*;

/**
 * Created by TheMDP on 2/16/17.
 */

public class WalkingTaskTests {

    private Context mockContext;
    private Resources mockResources;
    private DateUtils mockDateUtils;

    @Before
    public void setUp() throws Exception {
        mockContext = Mockito.mock(Context.class);
        mockResources = Mockito.mock(Resources.class);
        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        // All the strings that the TremorTask uses
        Mockito.when(mockContext.getString(R.string.rsb_WALK_TASK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_walk_intro_2_text_ld)).thenReturn("Find a place where you can safely walk unassisted for about %1$d steps in a straight line.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_DETAIL)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_OUTBOUND_INSTRUCTION_FORMAT)).thenReturn("Walk up to %1$d steps in a straight line.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_RETURN_INSTRUCTION_FORMAT)).thenReturn("Walk up to %1$d steps in a straight line.");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT)).thenReturn("Now stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_walk_stand_instruction_format)).thenReturn("Stand still for %1$s.");

        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TEXT)).thenReturn("");

        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
    }

    @Test
    public void testShortWalkTask() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Arrays.asList(new TaskExcludeOption[] {}));

        List<String> stepIds = getShortWalkStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {

                if (step.getIdentifier().equals(ShortWalkOutboundStepIdentifier) ||
                    step.getIdentifier().equals(ShortWalkReturnStepIdentifier))
                {
                    WalkingTaskStep walkingStep = (WalkingTaskStep)step;
                    boolean hasPedometer = false;
                    for (RecorderConfig config : walkingStep.getRecorderConfigurationList()) {
                        if (config instanceof PedometerRecorderConfig) {
                            hasPedometer = true;
                        }
                    }
                    assertTrue(hasPedometer);
                }

                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testShortWalkTaskNoInstructions() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.INSTRUCTIONS));

        List<String> stepIds = getShortWalkStepIds();
        stepIds.remove(Instruction0StepIdentifier);
        stepIds.remove(Instruction1StepIdentifier);
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testShortWalkTaskNoConclusion() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.CONCLUSION));

        List<String> stepIds = getShortWalkStepIds();
        stepIds.remove(ConclusionStepIdentifier);
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testShortWalkTaskNoPedometer() {
        OrderedTask task = WalkingTaskFactory.shortWalkTask(
                mockContext, "walkingtaskid", "intendedUseDescription",
                50, 10, Collections.singletonList(TaskExcludeOption.PEDOMETER));

        List<String> stepIds = getShortWalkStepIds();
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                if (step.getIdentifier().equals(ShortWalkOutboundStepIdentifier) ||
                    step.getIdentifier().equals(ShortWalkReturnStepIdentifier))
                {
                    WalkingTaskStep walkingStep = (WalkingTaskStep)step;
                    for (RecorderConfig config : walkingStep.getRecorderConfigurationList()) {
                        assertFalse(config instanceof PedometerRecorderConfig);
                    }
                }
                assertEquals(step.getIdentifier(), stepIds.get(i));
                i++;
            }
        } while (step != null);
    }

    private List<String> getShortWalkStepIds() {
        return new LinkedList<>(Arrays.asList(
                Instruction0StepIdentifier,
                Instruction1StepIdentifier,
                CountdownStepIdentifier,
                ShortWalkOutboundStepIdentifier,
                ShortWalkReturnStepIdentifier,
                ShortWalkRestStepIdentifier,
                ConclusionStepIdentifier));
    }
}
