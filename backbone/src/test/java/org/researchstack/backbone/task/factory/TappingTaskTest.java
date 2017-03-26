package org.researchstack.backbone.task.factory;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;
import static org.researchstack.backbone.task.factory.TremorTaskFactory.*;

/**
 * Created by TheMDP on 2/25/17.
 */

public class TappingTaskTest {

    @Mock private Context mockContext;
    @Mock private Resources mockResources;

    @Before
    public void setUp() throws Exception {
        mockContext = Mockito.mock(Context.class);
        mockResources = Mockito.mock(Resources.class);
        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        // All the strings that the TremorTask uses
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_TASK_TITLE_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_TASK_TITLE_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_REST_PHONE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_RIGHT_FIRST)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_LEFT_FIRST)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_RIGHT_SECOND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_LEFT_SECOND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_CALL_TO_ACTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_CALL_TO_ACTION_NEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INSTRUCTION_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_FORMAT)).thenReturn("Keep tapping for %1$s.");

        Mockito.when(mockContext.getString(R.string.rsb_time_minutes)).thenReturn("minutes");
        Mockito.when(mockContext.getString(R.string.rsb_time_seconds)).thenReturn("seconds");

        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
    }

    @Test
    public void testTappingTaskBothHandsNoSkipping() {
        OrderedTask task = TappingTaskFactory.twoFingerTappingIntervalTask(
                mockContext, "tappingtaskid", "intendedUseDescription", 10000,
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] {}), true);

        List<String> stepIds = getFullTappingStepIds(true, true);
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
    public void testTappingTaskBothHandsExcludeInstructions() {
        OrderedTask task = TappingTaskFactory.twoFingerTappingIntervalTask(
                mockContext, "tappingtaskid", "intendedUseDescription", 10000,
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] { TaskExcludeOption.INSTRUCTIONS }), true);

        List<String> stepIds = getFullTappingStepIds(true, true);

        stepIds.remove(Instruction0StepIdentifier);
        stepIds.remove(stepIdentifierWithHandId(Instruction1StepIdentifier, ActiveTaskLeftHandIdentifier));
        stepIds.remove(stepIdentifierWithHandId(Instruction1StepIdentifier, ActiveTaskRightHandIdentifier));

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
    public void testTappingTaskBothHandsExcludeConclusion() {
        OrderedTask task = TappingTaskFactory.twoFingerTappingIntervalTask(
                mockContext, "tappingtaskid", "intendedUseDescription", 10000,
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] { TaskExcludeOption.CONCLUSION }), true);

        List<String> stepIds = getFullTappingStepIds(true, true);

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
    public void testTappingTaskBothHandsRightIsFirst() {
        OrderedTask task = TappingTaskFactory.twoFingerTappingIntervalTask(
                mockContext, "tappingtaskid", "intendedUseDescription", 10000,
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] {}), false);

        List<String> stepIds = getFullTappingStepIds(true, false);

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
    public void testTappingTaskBothHandsRightOnly() {
        OrderedTask task = TappingTaskFactory.twoFingerTappingIntervalTask(
                mockContext, "tappingtaskid", "intendedUseDescription", 10000,
                HandTaskOptions.Hand.RIGHT,
                Arrays.asList(new TaskExcludeOption[] {}), false);

        List<String> stepIds = getFullTappingStepIds(false, false);

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
    public void testTappingTaskBothHandsLeftOnly() {
        OrderedTask task = TappingTaskFactory.twoFingerTappingIntervalTask(
                mockContext, "tappingtaskid", "intendedUseDescription", 10000,
                HandTaskOptions.Hand.LEFT,
                Arrays.asList(new TaskExcludeOption[] {}), false);

        List<String> stepIds = getFullTappingStepIds(false, true);

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

    private List<String> getFullTappingStepIds(boolean bothHands, boolean leftIsFirst) {
        List<String> stringIdList = new ArrayList<>();

        stringIdList.add(Instruction0StepIdentifier);

        stringIdList.addAll(getOneHandTappingStepIds(leftIsFirst ?
                ActiveTaskLeftHandIdentifier : ActiveTaskRightHandIdentifier));

        if (bothHands) {
            stringIdList.addAll(getOneHandTappingStepIds(!leftIsFirst ?
                    ActiveTaskLeftHandIdentifier : ActiveTaskRightHandIdentifier));
        }

        stringIdList.add(ConclusionStepIdentifier);

        return stringIdList;
    }

    private List<String> getOneHandTappingStepIds(String handId) {
        return new LinkedList<>(Arrays.asList(
                stepIdentifierWithHandId(Instruction1StepIdentifier, handId),
                stepIdentifierWithHandId(TappingStepIdentifier, handId)));
    }
}
