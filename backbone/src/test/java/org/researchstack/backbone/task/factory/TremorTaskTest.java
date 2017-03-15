package org.researchstack.backbone.task.factory;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import static org.researchstack.backbone.task.factory.TremorTaskFactory.*;
import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by TheMDP on 2/5/17.
 */

public class TremorTaskTest {

    private Context mockContext;
    private Resources mockResources;

    @Before
    public void setUp() throws Exception {
        mockContext = Mockito.mock(Context.class);
        mockResources = Mockito.mock(Resources.class);
        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        // All the strings that the TremorTask uses
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_FINISHED_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_1_DETAIL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DEFAULT_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_LEFT_HAND_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_RIGHT_HAND_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_in_lap_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_extend_arm_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_bend_arm_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_touch_nose_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_turn_wrist_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_COMPLETED_INSTRUCTION)).thenReturn("Activity Completed");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_SWITCH_HANDS_RIGHT_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_SWITCH_HANDS_LEFT_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_1_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_2_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_3_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_4_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_5_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_skip_question_both_hands)).thenReturn("%1$s");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_intro_2_detail_default)).thenReturn("%1$s");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_SKIP_RIGHT_HAND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_SKIP_LEFT_HAND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_SKIP_NEITHER)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_COMPLETED_INSTRUCTION)).thenReturn("");

        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
    }

    @Test
    public void testTremorTaskBothHandsNoSkipping() {
        NavigableOrderedTask task = tremorTask(
                mockContext, "tremorttaskid", "intendedUseDescription", 10000,
                Arrays.asList(new TremorTaskExcludeOption[] {}),
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] {}), true);

        String[] stepIds = getFullTremorStepIds(true, true);
        Step step = null;
        int i = 0;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                assertEquals(step.getIdentifier(), stepIds[i]);
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testTremorTaskBothHandsExcludeRightHandLeftHandFirst() {
        NavigableOrderedTask task = tremorTask(
                mockContext, "tremorttaskid", "intendedUseDescription", 10000,
                Arrays.asList(new TremorTaskExcludeOption[] {}),
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] {}), true);

        String[] stepIds = getFullTremorStepIds(false, true);
        Step step = null;
        int i = 0;
        TaskResult result = new TaskResult("tremorttaskid");
        do {
            step = task.getStepAfterStep(step, result);

            if (step != null) {
                // Set hand result when we get to it, and it will imply skipping the right hand
                if (step.getIdentifier().equals(ActiveTaskSkipHandStepIdentifier)) {
                    StepResult<String> handResult = new StepResult<>(step);
                    handResult.setResult(ActiveTaskRightHandIdentifier);
                    result.setStepResultForStepIdentifier(step.getIdentifier(), handResult);
                }

                // This is no longer a condition
                // When we skip a hand, we edit the spoken text of the last hand test to be activity completed
//                if (step.getIdentifier().equals(TremorTestTurnWristStepIdentifier)) {
//                    assertTrue(step instanceof ActiveStep);
//                    ActiveStep activeStep = (ActiveStep)step;
//                    assertEquals(activeStep.getFinishedSpokenInstruction(), "Activity Completed");
//                }

                assertEquals(step.getIdentifier(), stepIds[i]);
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testTremorTaskBothHandsExcludeRightHandRightHandFirst() {
        NavigableOrderedTask task = tremorTask(
                mockContext, "tremorttaskid", "intendedUseDescription", 10000,
                Arrays.asList(new TremorTaskExcludeOption[] {}),
                HandTaskOptions.Hand.BOTH,
                Arrays.asList(new TaskExcludeOption[] {}), false);

        String[] stepIds = getFullTremorStepIds(false, true);
        Step step = null;
        int i = 0;
        TaskResult result = new TaskResult("tremorttaskid");
        do {
            step = task.getStepAfterStep(step, result);

            if (step != null) {
                // Set hand result when we get to it, and it will imply skipping the right hand
                if (step.getIdentifier().equals(ActiveTaskSkipHandStepIdentifier)) {
                    StepResult<String> handResult = new StepResult<>(step);
                    handResult.setResult(ActiveTaskRightHandIdentifier);
                    result.setStepResultForStepIdentifier(step.getIdentifier(), handResult);
                }

                // This is no longer a condition
                // When we skip a hand, we edit the spoken text of the last hand test to be activity completed
//                if (step.getIdentifier().equals(TremorTestTurnWristStepIdentifier)) {
//                    assertTrue(step instanceof ActiveStep);
//                    ActiveStep activeStep = (ActiveStep)step;
//                    assertEquals(activeStep.getFinishedSpokenInstruction(), "Activity Completed");
//                }

                assertEquals(step.getIdentifier(), stepIds[i]);
                i++;
            }
        } while (step != null);
    }

    @Test
    public void testTremorTaskBothHandExcludeTremorTasks() {
        List<TremorTaskFactory.TremorTaskExcludeOption> excludeOptionList =
                Arrays.asList(TremorTaskExcludeOption.values());
        List<String> excludeIdentifierList = Arrays.asList(
                TremorTestInLapStepIdentifier,
                TremorTestExtendArmStepIdentifier,
                TremorTestBendArmStepIdentifier,
                TremorTestTouchNoseStepIdentifier,
                TremorTestTurnWristStepIdentifier
        );

        for (int i = 0; i < excludeOptionList.size(); i++) {
            NavigableOrderedTask task = tremorTask(
                    mockContext, "tremorttaskid", "intendedUseDescription", 10000,
                    Collections.singletonList(excludeOptionList.get(i)),
                    HandTaskOptions.Hand.BOTH,
                    Arrays.asList(new TaskExcludeOption[] {}));

            Step step = null;
            do {
                step = task.getStepAfterStep(step, null);
                if (step != null) {
                    // We must make sure that known of the steps included are our excluded type
                    if (excludeIdentifierList.get(i).equals(TremorTestExtendArmStepIdentifier)) {
                        // special case where handAtShouldLength is contained within another identifier
                        if (!step.getIdentifier().contains(TremorTestBendArmStepIdentifier)) {
                            assertFalse(step.getIdentifier().contains(excludeIdentifierList.get(i)));
                        }
                    } else {
                        assertFalse(step.getIdentifier().contains(excludeIdentifierList.get(i)));
                    }
                }
            } while (step != null);
        }
    }

    @Test
    public void testTremorTaskBothHandsExcludeInstructions() {
        NavigableOrderedTask task = tremorTask(
                mockContext, "tremorttaskid", "intendedUseDescription", 10000,
                Arrays.asList(new TremorTaskExcludeOption[] {}),
                HandTaskOptions.Hand.BOTH,
                Collections.singletonList(TaskExcludeOption.INSTRUCTIONS));

        Step step = null;
        do {
            step = task.getStepAfterStep(step, null);
            if (step != null) {
                // We allow this one instruction always
                if (!step.getIdentifier().contains(Instruction1StepIdentifier) &&
                    !step.getIdentifier().contains(ConclusionStepIdentifier))
                {
                    assertFalse(step instanceof InstructionStep);
                }
            }
        } while (step != null);
    }

    private String[] getFullTremorStepIds(boolean bothHands, boolean leftIsFirst) {
        List<String> stringIdList = new ArrayList<>();
        stringIdList.addAll(Arrays.asList(
                Instruction0StepIdentifier,
                ActiveTaskSkipHandStepIdentifier));

        stringIdList.addAll(getOneHandTremorStepIds(leftIsFirst ?
                ActiveTaskLeftHandIdentifier : ActiveTaskRightHandIdentifier));
        if (bothHands) {
            stringIdList.addAll(getOneHandTremorStepIds(!leftIsFirst ?
                    ActiveTaskLeftHandIdentifier : ActiveTaskRightHandIdentifier));
        }

        stringIdList.add(ConclusionStepIdentifier);
        return stringIdList.toArray(new String[stringIdList.size()]);
    }

    private List<String> getOneHandTremorStepIds(String handId) {
        return Arrays.asList(
                stepIdentifierWithHandId(Instruction1StepIdentifier, handId),
                stepIdentifierWithHandId(Instruction2StepIdentifier, handId),
                stepIdentifierWithHandId(Countdown1StepIdentifier, handId),
                stepIdentifierWithHandId(TremorTestInLapStepIdentifier, handId),
                stepIdentifierWithHandId(Instruction4StepIdentifier, handId),
                stepIdentifierWithHandId(Countdown2StepIdentifier, handId),
                stepIdentifierWithHandId(TremorTestExtendArmStepIdentifier, handId),
                stepIdentifierWithHandId(Instruction5StepIdentifier, handId),
                stepIdentifierWithHandId(Countdown3StepIdentifier, handId),
                stepIdentifierWithHandId(TremorTestBendArmStepIdentifier, handId),
                stepIdentifierWithHandId(Instruction6StepIdentifier, handId),
                stepIdentifierWithHandId(Countdown4StepIdentifier, handId),
                stepIdentifierWithHandId(TremorTestTouchNoseStepIdentifier, handId),
                stepIdentifierWithHandId(Instruction7StepIdentifier, handId),
                stepIdentifierWithHandId(Countdown5StepIdentifier, handId),
                stepIdentifierWithHandId(TremorTestTurnWristStepIdentifier, handId));
    }
}
