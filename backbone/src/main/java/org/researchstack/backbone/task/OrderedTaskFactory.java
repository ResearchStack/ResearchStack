package org.researchstack.backbone.task;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.CompletionStep;
import org.researchstack.backbone.step.NavigationQuestionStep;
import org.researchstack.backbone.step.active.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.DeviceMotionRecorderConfig;
import org.researchstack.backbone.step.active.NavigationActiveStep;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by TheMDP on 2/4/17.
 *
 * In iOS, they included a bunch of static methods for building OrderedTasks in the
 * OrderedTask class.  However, I think they belong in this Factory class
 */

public class OrderedTaskFactory {

    // Recorder Config Identifiers
    public static final String Accelerometer1ConfigIdentifier   = "ac1_acc";
    public static final String DeviceMotion1ConfigIdentifier    = "ac1_motion";
    public static final String Accelerometer2ConfigIdentifier   = "ac2_acc";
    public static final String DeviceMotion2ConfigIdentifier    = "ac2_motion";
    public static final String Accelerometer3ConfigIdentifier   = "ac3_acc";
    public static final String DeviceMotion3ConfigIdentifier    = "ac3_motion";
    public static final String Accelerometer4ConfigIdentifier   = "ac4_acc";
    public static final String DeviceMotion4ConfigIdentifier    = "ac4_motion";
    public static final String Accelerometer5ConfigIdentifier   = "ac5_acc";
    public static final String DeviceMotion5ConfigIdentifier    = "ac5_motion";

    // Step Identifiers
    public static final String Instruction0StepIdentifier   = "instruction";
    public static final String Instruction1StepIdentifier   = "instruction1";
    public static final String Instruction2StepIdentifier   = "instruction2";
    public static final String Instruction3StepIdentifier   = "instruction3";
    public static final String Instruction4StepIdentifier   = "instruction4";
    public static final String Instruction5StepIdentifier   = "instruction5";
    public static final String Instruction6StepIdentifier   = "instruction6";
    public static final String Instruction7StepIdentifier   = "instruction7";
    public static final String CountdownStepIdentifier      = "countdown";
    public static final String Countdown1StepIdentifier     = "countdown1";
    public static final String Countdown2StepIdentifier     = "countdown2";
    public static final String Countdown3StepIdentifier     = "countdown3";
    public static final String Countdown4StepIdentifier     = "countdown4";
    public static final String Countdown5StepIdentifier     = "countdown5";
    // Tremor Step Identifiers
    public static final String TremorTestInLapStepIdentifier        = "tremor.handInLap";
    public static final String TremorTestExtendArmStepIdentifier    = "tremor.handAtShoulderLength";
    public static final String TremorTestBendArmStepIdentifier      = "tremor.handAtShoulderLengthWithElbowBent";
    public static final String TremorTestTouchNoseStepIdentifier    = "tremor.handToNose";
    public static final String TremorTestTurnWristStepIdentifier    = "tremor.handQueenWave";
    public static final String ActiveTaskMostAffectedHandIdentifier = "mostAffected";
    public static final String ActiveTaskLeftHandIdentifier         = "left";
    public static final String ActiveTaskRightHandIdentifier        = "right";
    public static final String ActiveTaskSkipHandStepIdentifier     = "skipHand";
    // Conclusion Step Identifiers
    public static final String ConclusionStepIdentifier     = "conclusion";

    /**
     * Returns a predefined task that measures hand tremor.
     *
     * In a tremor assessment task, the participant is asked to hold the device with their most affected
     * hand in various positions while accelerometer and motion data are captured.
     *
     * @param context                can be app or activity, used for resources
     * @param identifier             The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription A localized string describing the intended use of the data
     *                               collected. If the value of this parameter is null, none will be used
     * @param activeStepDuration     The duration for each active step in the task in seconds
     * @param tremorOptionList       Options that affect which active steps are presented for this task.
     * @param handOption             Options for determining which hand(s) to test.
     * @param taskOptionList             Options that affect the features of the predefined task,
     *                               conclusion option will be ignored at this time.
     *
     * @return An active tremor test task that can be presented with an `ORKTaskViewController` object.
     */
    public static NavigableOrderedTask tremorTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            int activeStepDuration,
            List<TremorTaskExcludeOption> tremorOptionList,
            HandOptions handOption,
            List<TaskExcludeOption> taskOptionList)
    {
        // Coin toss for which hand first (in case we're doing both)
        final boolean leftFirstIfDoingBoth = (new Random()).nextBoolean();
        return tremorTask(context, identifier, intendedUseDescription, activeStepDuration,
                tremorOptionList, handOption, taskOptionList, leftFirstIfDoingBoth);
    }

    // This method is separate mainly for unit testing purposes, to eliminate randomness
    protected static NavigableOrderedTask tremorTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            int activeStepDuration,
            List<TremorTaskExcludeOption> tremorOptionList,
            HandOptions handOption,
            List<TaskExcludeOption> taskOptionList,
            boolean leftFirstIfDoingBoth)
    {
        List<Step> stepList = new ArrayList<>();

        final boolean doingBoth = handOption == HandOptions.BOTH;
        final boolean firstIsLeft = (leftFirstIfDoingBoth && doingBoth) || (!doingBoth && handOption == HandOptions.LEFT);

        if (!taskOptionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            String title = context.getString(R.string.rsb_TREMOR_TEST_TITLE);
            String text = intendedUseDescription;
            String detailText = context.getString(R.string.rsb_TREMOR_TEST_INTRO_1_DETAIL);
            InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, text);
            step.setMoreDetailText(detailText);
            step.setImage(ResUtils.TREMOR_TEST_1);
            if (firstIsLeft) {
                step.setImage(ResUtils.TREMOR_TEST_1_FLIPPED);
            }
            stepList.add(step);
        }

        // Build the string for the detail texts
        String[] detailStringForNumberOfTasks = new String[] {
                context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_1_TASK),
                context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_2_TASK),
                context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_3_TASK),
                context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_4_TASK),
                context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_5_TASK)
        };

        // Get the actual count for the end index based on the exclusion parameters
        int actualTasksIndex = TremorTaskExcludeOption.values().length - tremorOptionList.size() - 1;

        String detailFormat = doingBoth ?
                context.getString(R.string.rsb_tremor_test_skip_question_both_hands):
                context.getString(R.string.rsb_tremor_test_intro_2_detail_default);
        String detailText = String.format(detailFormat, detailStringForNumberOfTasks[actualTasksIndex]);

        NavigationQuestionStep handQuestionStep = null;
        if (doingBoth) {
            // If doing both hands then ask the user if they need to skip one of the hands
            ChoiceAnswerFormat answerFormat = new ChoiceAnswerFormat(
                    AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                    new Choice<>(context.getString(R.string.rsb_TREMOR_SKIP_RIGHT_HAND), ActiveTaskRightHandIdentifier),
                    new Choice<>(context.getString(R.string.rsb_TREMOR_SKIP_LEFT_HAND), ActiveTaskLeftHandIdentifier),
                    new Choice<>(context.getString(R.string.rsb_TREMOR_SKIP_NEITHER), "")
            );

            String title = context.getString(R.string.rsb_TREMOR_TEST_TITLE);
            handQuestionStep = new NavigationQuestionStep(ActiveTaskSkipHandStepIdentifier, title, answerFormat);
            handQuestionStep.setText(detailText);
            handQuestionStep.setOptional(false);

            stepList.add(handQuestionStep);
        }

        // right or most-affected hand
        List<Step> rightSteps = new ArrayList<>();
        if (handOption == HandOptions.BOTH || handOption == HandOptions.RIGHT) {
            rightSteps = stepsForOneHandTremorTest(context, identifier,
                    activeStepDuration, tremorOptionList, firstIsLeft, false,
                    ActiveTaskRightHandIdentifier, detailText, taskOptionList);
        }

        List<Step> leftSteps = new ArrayList<>();
        if (handOption == HandOptions.BOTH || handOption == HandOptions.LEFT) {
            leftSteps = stepsForOneHandTremorTest(context, identifier,
                    activeStepDuration, tremorOptionList, !firstIsLeft, true,
                    ActiveTaskLeftHandIdentifier, detailText, taskOptionList);
        }

        if (firstIsLeft && !leftSteps.isEmpty()) {
            stepList.addAll(leftSteps);
        }

        if (!rightSteps.isEmpty()) {
            stepList.addAll(rightSteps);
        }

        if (!firstIsLeft && !leftSteps.isEmpty()) {
            stepList.addAll(leftSteps);
        }

        // iOS has the conclusion step optional, but we can't since we don't support step modifiers
        // However, there should always be a conclusion step, so this really isn't an issue
        CompletionStep completionStep = makeCompletionStep(context);
        stepList.add(completionStep);
        final String completionStepId = completionStep.getIdentifier();

        NavigableOrderedTask task = new NavigableOrderedTask(identifier, stepList);

        // Setup rules for skipping all the steps in either the left or right hand if called upon to do so.
        if (doingBoth) {
            List<Step> firstHandStepList  = firstIsLeft ? leftSteps  : rightSteps;
            List<Step> secondHandStepList = firstIsLeft ? rightSteps : leftSteps;
            final String secondHandStepId = secondHandStepList.get(0).getIdentifier();

            // This step can be used to skip the second hand if we need to
            final NavigationActiveStep lastStepOfFirstHands = (NavigationActiveStep)firstHandStepList.get(firstHandStepList.size()-1);

            // The question step can be used to skip the first steps if we need to
            String handResultString = firstIsLeft ? ActiveTaskLeftHandIdentifier : ActiveTaskRightHandIdentifier;
            handQuestionStep.setCustomRules(Collections.singletonList(new NavigableOrderedTask.ObjectEqualsNavigationRule(
                    handResultString, secondHandStepId, ActiveTaskSkipHandStepIdentifier)));

            // Next add a navigation rule to the end of the first set of hand steps to potentially skip the second steps
            String lastStepResultString = firstIsLeft ? ActiveTaskRightHandIdentifier : ActiveTaskLeftHandIdentifier;
            lastStepOfFirstHands.setCustomRules(Collections.singletonList(new NavigableOrderedTask.ObjectEqualsNavigationRule(
                    lastStepResultString, completionStepId, ActiveTaskSkipHandStepIdentifier)));
        }

        return task;
    }

    private static List<Step> stepsForOneHandTremorTest(
            Context context,
            String identifier,
            int activeStepDuration,
            List<TremorTaskExcludeOption> tremorOptionList,
            boolean lastHand,
            boolean leftHand,
            String handIdentifier,
            String detailText,
            List<TaskExcludeOption> taskOptionList)
    {
        List<Step> stepList = new ArrayList<>();

        String stepFinishedInstruction = context.getString(
                R.string.rsb_TREMOR_TEST_ACTIVE_STEP_FINISHED_INSTRUCTION);

        boolean rightHand = !leftHand && !ActiveTaskMostAffectedHandIdentifier.equals(handIdentifier);

        /*********************************************************************************************
         * Intro Instruction Step
         *********************************************************************************************/
        // Bracket blocks for variable encapsulation
        {
            String stepIdentifier = stepIdentifierWithHandId(Instruction1StepIdentifier, handIdentifier);
            String title = context.getString(R.string.rsb_TREMOR_TEST_TITLE);
            String text, stepDetailText = null;
            if (ActiveTaskMostAffectedHandIdentifier.equals(identifier)) {
                text = context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DEFAULT_TEXT);
                stepDetailText = detailText;
            } else {
                if (leftHand) {
                    text = context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_LEFT_HAND_TEXT);
                } else {
                    text = context.getString(R.string.rsb_TREMOR_TEST_INTRO_2_RIGHT_HAND_TEXT);
                }
            }
            InstructionStep step = new InstructionStep(stepIdentifier, title, text);
            step.setMoreDetailText(stepDetailText);

            step.setImage(ResUtils.TREMOR_TEST_2);
            if (leftHand) {
                step.setImage(ResUtils.TREMOR_TEST_2_FLIPPED);
            }

            stepList.add(step);
        }

        // Obtain sensor frequency for Tremor Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_tremor_task);

        /*********************************************************************************************
         * Hand in lap
         *********************************************************************************************/
        if (!tremorOptionList.contains(TremorTaskExcludeOption.HAND_IN_LAP)) {
            if (!taskOptionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                String stepIdentifier = stepIdentifierWithHandId(Instruction2StepIdentifier, handIdentifier);
                String title = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO);
                String text = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT);

                InstructionStep step = new InstructionStep(stepIdentifier, title, text);

                step.setImage(ResUtils.TREMOR_TEST_3);
                if (leftHand) {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO));
                    step.setImage(ResUtils.TREMOR_TEST_3_FLIPPED);
                } else {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO_RIGHT));
                }
                stepList.add(step);
            }

            {
                String stepIdentifier = stepIdentifierWithHandId(Countdown1StepIdentifier, handIdentifier);
                CountdownStep step = new CountdownStep(stepIdentifier);
                stepList.add(step);
            }

            {
                String titleFormat = context.getString(R.string.rsb_tremor_test_active_step_in_lap_instruction_ld);
                String stepIdentifier = stepIdentifierWithHandId(TremorTestInLapStepIdentifier, handIdentifier);
                NavigationActiveStep step = new NavigationActiveStep(stepIdentifier);
                step.setRecorderConfigurationList(Arrays.asList(
                        new AccelerometerRecorderConfig(Accelerometer1ConfigIdentifier, sensorFreq),
                        new DeviceMotionRecorderConfig(DeviceMotion1ConfigIdentifier, sensorFreq)
                ));
                String title = String.format(titleFormat, activeStepDuration);
                step.setTitle(title);
                step.setSpokenInstruction(title);
                step.setFinishedSpokenInstruction(stepFinishedInstruction);
                step.setStepDuration(activeStepDuration);
                step.setShouldPlaySoundOnStart(true);
                step.setShouldVibrateOnStart(true);
                step.setShouldPlaySoundOnFinish(true);
                step.setShouldVibrateOnFinish(true);
                step.setShouldContinueOnFinish(false);
                step.setShouldStartTimerAutomatically(true);

                stepList.add(step);
            }
        }

        /*********************************************************************************************
         * Hand at shoulder height
         *********************************************************************************************/
        if (!tremorOptionList.contains(TremorTaskExcludeOption.HAND_AT_SHOULDER_HEIGHT)) {
            if (!taskOptionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                String stepIdentifier = stepIdentifierWithHandId(Instruction4StepIdentifier, handIdentifier);
                String title = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO);
                String text = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT);
                InstructionStep step = new InstructionStep(stepIdentifier, title, text);
                step.setImage(ResUtils.TREMOR_TEST_4);
                if (leftHand) {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO_LEFT));
                    step.setImage(ResUtils.TREMOR_TEST_4_FLIPPED);
                } else {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO_RIGHT));
                }
                stepList.add(step);
            }

            {
                String stepIdentifier = stepIdentifierWithHandId(Countdown2StepIdentifier, handIdentifier);
                CountdownStep step = new CountdownStep(stepIdentifier);
                stepList.add(step);
            }

            {
                String titleFormat = context.getString(R.string.rsb_tremor_test_active_step_extend_arm_instruction_ld);
                String stepIdentifier = stepIdentifierWithHandId(TremorTestExtendArmStepIdentifier, handIdentifier);
                NavigationActiveStep step = new NavigationActiveStep(stepIdentifier);
                step.setRecorderConfigurationList(Arrays.asList(
                        new AccelerometerRecorderConfig(Accelerometer2ConfigIdentifier, sensorFreq),
                        new DeviceMotionRecorderConfig(DeviceMotion2ConfigIdentifier, sensorFreq)
                ));
                String title = String.format(titleFormat, activeStepDuration);
                step.setTitle(title);
                step.setSpokenInstruction(title);
                step.setFinishedSpokenInstruction(stepFinishedInstruction);
                step.setStepDuration(activeStepDuration);
                step.setShouldPlaySoundOnStart(true);
                step.setShouldVibrateOnStart(true);
                step.setShouldPlaySoundOnFinish(true);
                step.setShouldVibrateOnFinish(true);
                step.setShouldContinueOnFinish(false);
                step.setShouldStartTimerAutomatically(true);

                stepList.add(step);
            }
        }

        /*********************************************************************************************
         * Hand at shoulder height and elbow bent
         *********************************************************************************************/
        if (!tremorOptionList.contains(TremorTaskExcludeOption.HAND_AT_SHOULDER_HEIGHT_ELBOW_BENT)) {
            if (!taskOptionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                String stepIdentifier = stepIdentifierWithHandId(Instruction5StepIdentifier, handIdentifier);
                String title = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO);
                String text = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT);
                InstructionStep step = new InstructionStep(stepIdentifier, title, text);
                step.setImage(ResUtils.TREMOR_TEST_5);
                if (leftHand) {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO_LEFT));
                    step.setImage(ResUtils.TREMOR_TEST_5_FLIPPED);
                } else {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO_RIGHT));
                }
                stepList.add(step);
            }

            {
                String stepIdentifier = stepIdentifierWithHandId(Countdown3StepIdentifier, handIdentifier);
                CountdownStep step = new CountdownStep(stepIdentifier);
                stepList.add(step);
            }

            {
                String titleFormat = context.getString(R.string.rsb_tremor_test_active_step_bend_arm_instruction_ld);
                String stepIdentifier = stepIdentifierWithHandId(TremorTestBendArmStepIdentifier, handIdentifier);
                NavigationActiveStep step = new NavigationActiveStep(stepIdentifier);
                step.setRecorderConfigurationList(Arrays.asList(
                        new AccelerometerRecorderConfig(Accelerometer3ConfigIdentifier, sensorFreq),
                        new DeviceMotionRecorderConfig(DeviceMotion3ConfigIdentifier, sensorFreq)
                ));
                String title = String.format(titleFormat, activeStepDuration);
                step.setTitle(title);
                step.setSpokenInstruction(title);
                step.setFinishedSpokenInstruction(stepFinishedInstruction);
                step.setStepDuration(activeStepDuration);
                step.setShouldPlaySoundOnStart(true);
                step.setShouldVibrateOnStart(true);
                step.setShouldPlaySoundOnFinish(true);
                step.setShouldVibrateOnFinish(true);
                step.setShouldContinueOnFinish(false);
                step.setShouldStartTimerAutomatically(true);

                stepList.add(step);
            }
        }

        /*********************************************************************************************
         * Hand to Nose
         *********************************************************************************************/
        if (!tremorOptionList.contains(TremorTaskExcludeOption.HAND_TO_NOSE)) {
            if (!taskOptionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                String stepIdentifier = stepIdentifierWithHandId(Instruction6StepIdentifier, handIdentifier);
                String title = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO);
                String text = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT);
                InstructionStep step = new InstructionStep(stepIdentifier, title, text);
                step.setImage(ResUtils.TREMOR_TEST_6);
                if (leftHand) {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO_LEFT));
                    step.setImage(ResUtils.TREMOR_TEST_6_FLIPPED);
                } else {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO_RIGHT));
                }
                stepList.add(step);
            }

            {
                String stepIdentifier = stepIdentifierWithHandId(Countdown4StepIdentifier, handIdentifier);
                CountdownStep step = new CountdownStep(stepIdentifier);
                stepList.add(step);
            }

            {
                String titleFormat = context.getString(R.string.rsb_tremor_test_active_step_touch_nose_instruction_ld);
                String stepIdentifier = stepIdentifierWithHandId(TremorTestTouchNoseStepIdentifier, handIdentifier);
                NavigationActiveStep step = new NavigationActiveStep(stepIdentifier);
                step.setRecorderConfigurationList(Arrays.asList(
                        new AccelerometerRecorderConfig(Accelerometer4ConfigIdentifier, sensorFreq),
                        new DeviceMotionRecorderConfig(DeviceMotion4ConfigIdentifier, sensorFreq)
                ));
                String title = String.format(titleFormat, activeStepDuration);
                step.setTitle(title);
                step.setSpokenInstruction(title);
                step.setFinishedSpokenInstruction(stepFinishedInstruction);
                step.setStepDuration(activeStepDuration);
                step.setShouldPlaySoundOnStart(true);
                step.setShouldVibrateOnStart(true);
                step.setShouldPlaySoundOnFinish(true);
                step.setShouldVibrateOnFinish(true);
                step.setShouldContinueOnFinish(false);
                step.setShouldStartTimerAutomatically(true);

                stepList.add(step);
            }
        }

        /*********************************************************************************************
         * Queen Wave
         *********************************************************************************************/
        if (!tremorOptionList.contains(TremorTaskExcludeOption.QUEEN_WAVE)) {
            if (!taskOptionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                String stepIdentifier = stepIdentifierWithHandId(Instruction7StepIdentifier, handIdentifier);
                String title = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO);
                String text = context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT);
                InstructionStep step = new InstructionStep(stepIdentifier, title, text);
                step.setImage(ResUtils.TREMOR_TEST_7);
                if (leftHand) {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO_LEFT));
                    step.setImage(ResUtils.TREMOR_TEST_7_FLIPPED);
                } else {
                    step.setTitle(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO_RIGHT));
                }
                stepList.add(step);
            }

            {
                String stepIdentifier = stepIdentifierWithHandId(Countdown5StepIdentifier, handIdentifier);
                CountdownStep step = new CountdownStep(stepIdentifier);
                stepList.add(step);
            }

            {
                String titleFormat = context.getString(R.string.rsb_tremor_test_active_step_turn_wrist_instruction_ld);
                String stepIdentifier = stepIdentifierWithHandId(TremorTestTurnWristStepIdentifier, handIdentifier);
                NavigationActiveStep step = new NavigationActiveStep(stepIdentifier);
                step.setRecorderConfigurationList(Arrays.asList(
                        new AccelerometerRecorderConfig(Accelerometer5ConfigIdentifier, sensorFreq),
                        new DeviceMotionRecorderConfig(DeviceMotion5ConfigIdentifier, sensorFreq)
                ));
                String title = String.format(titleFormat, activeStepDuration);
                step.setTitle(title);
                step.setSpokenInstruction(title);
                step.setFinishedSpokenInstruction(stepFinishedInstruction);
                step.setStepDuration(activeStepDuration);
                step.setShouldPlaySoundOnStart(true);
                step.setShouldVibrateOnStart(true);
                step.setShouldPlaySoundOnFinish(true);
                step.setShouldVibrateOnFinish(true);
                step.setShouldContinueOnFinish(false);
                step.setShouldStartTimerAutomatically(true);

                stepList.add(step);
            }
        }

        // fix the spoken instruction on the last included step, depending on which hand we're on
        ActiveStep lastStep = (ActiveStep)stepList.get(stepList.size()-1);
        if (lastHand) {
            lastStep.setFinishedSpokenInstruction(context.getString(R.string.rsb_TREMOR_TEST_COMPLETED_INSTRUCTION));
        } else if (leftHand) {
            lastStep.setFinishedSpokenInstruction(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_SWITCH_HANDS_RIGHT_INSTRUCTION));
        } else {
            lastStep.setFinishedSpokenInstruction(context.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_SWITCH_HANDS_LEFT_INSTRUCTION));
        }

        return stepList;
    }

    protected static String stepIdentifierWithHandId(String stepId, String handId) {
        if (handId == null) {
            return stepId;
        }
        return String.format("%s.%s", stepId, handId);
    }

    public static CompletionStep makeCompletionStep(Context context) {
        String title = context.getString(R.string.rsb_TASK_COMPLETE_TITLE);
        String text = context.getString(R.string.rsb_TASK_COMPLETE_TEXT);
        CompletionStep step = new CompletionStep(ConclusionStepIdentifier, title, text);
        return step;
    }

    /**
     * The `TremorTaskExcludeOption` enum lets you exclude particular steps from the predefined active
     * tasks in the predefined Tremor `OrderedTask`.
     *
     * By default, all predefined active tasks will be included. The tremor active task option flags can
     * be used to explicitly specify that an active task is not to be included.
     */
    public enum TremorTaskExcludeOption {
        // Exclude the hand-in-lap steps.
        HAND_IN_LAP,
        // Exclude the hand-extended-at-shoulder-height steps.
        HAND_AT_SHOULDER_HEIGHT,
        // Exclude the hand-extended-at-shoulder-height steps.
        HAND_AT_SHOULDER_HEIGHT_ELBOW_BENT,
        // Exclude the elbow-bent-touch-nose steps.
        HAND_TO_NOSE,
        // Exclude the queen-wave steps.
        QUEEN_WAVE
    }

    /**
     * The `TaskExcludeOption` enum lets you exclude particular behaviors from the predefined active
     * tasks in the predefined category of `OrderedTask`.
     *
     * By default, all predefined tasks include instructions and conclusion steps, and may also include
     * one or more data collection recorder configurations. Although not all predefined tasks include all
     * of these data collection types, the predefined task enum flags can be used to explicitly specify
     * that a task option not be included.
     */
    public enum TaskExcludeOption {
        // Exclude the initial instruction steps.
        INSTRUCTIONS,
        // Exclude the conclusion step.
        CONCLUSION,
        // Exclude accelerometer data collection.
        ACCELEROMETER,
        // Exclude device motion data collection.
        DEVICE_MOTION,
        // Exclude pedometer data collection.
        PEDOMETER,
        // Exclude location data collection.
        LOCATION,
        // Exclude heart rate data collection.
        HEART_RATE,
        // Exclude audio data collection.
        AUDIO
    }

    /**
     * Values that identify the hand(s) to be used in an active task.
     *
     * By default, the participant will be asked to use their most affected hand.
     */
    public enum HandOptions {
        // Task should only test the left hand
        LEFT,
        // Task should only test the right hand
        RIGHT,
        // Task should test both left and right hands
        BOTH;
    }
}
