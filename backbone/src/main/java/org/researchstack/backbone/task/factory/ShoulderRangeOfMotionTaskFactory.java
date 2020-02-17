package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TouchAnywhereStep;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
import org.researchstack.backbone.step.active.recorder.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by David Evans, Laurence Hurst, Simon Hartley, 2019.
 *
 * This class was created to encapsulate the creation of a Shoulder Range of Motion (ROM) Task.
 */

public class ShoulderRangeOfMotionTaskFactory {

    public static final String ShoulderRangeOfMotionStepIdentifier = "shoulderRangeOfMotion";

    /**
     * Returns a predefined task that measures the range of motion for the left shoulder, right shoulder,
     * or both shoulders.
     * <p>
     * The data collected by this task is device motion data.
     *
     * @param context                can be app or activity, used for resources
     * @param identifier             The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription A localized string describing the intended use of the data
     *                               collected. If the value of this parameter is `nil`, the default
     *                               localized text is displayed.
     * @param sideOptions            The limb in which ROM is being measured.
     * @param optionList             Options that affect the features of the predefined task.
     * @return                       An active range of motion task that can be presented with an
     *                               `ActiveTaskActivity` object.
     */

    public static OrderedTask shoulderRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            TaskOptions.Side sideOptions,
            List<TaskExcludeOption> optionList)
    {
        // Coin toss to determine which side is tested first (in case both sides are being tested)
        final boolean leftFirstIfDoingBoth = (new Random()).nextBoolean();

        return shoulderRangeOfMotionTask(
                context, identifier, intendedUseDescription,
                sideOptions, optionList, leftFirstIfDoingBoth);
    }

    // This method is separate mainly for unit testing purposes, to eliminate side randomness
    protected static OrderedTask shoulderRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            TaskOptions.Side sideOptions,
            List<TaskExcludeOption> optionList,
            boolean leftFirstIfDoingBoth)

    {
        List<Step> stepList = new ArrayList<>();

        // Setup which side to start with and how many sides to test based on the sideOptions parameter
        // Side order is randomly determined.
        int sideCount = sideOptions == TaskOptions.Side.BOTH ? 2 : 1;  // 2 sides for both, 1 side for right or left
        boolean rightSide = false;
        switch (sideOptions) {
            case LEFT:
                rightSide = false;
                break;
            case RIGHT:
                rightSide = true;
                break;
            case BOTH:
                rightSide = !leftFirstIfDoingBoth;
                break;
        }

        // Obtain sensor frequency for Range of Motion Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);

        // Make steps for one or both sides
        for (int side = 1; side <= sideCount; side++) {
            String sideIdentifier = rightSide ?
                    stepIdentifierWithSideId(Instruction0StepIdentifier, ActiveTaskRightSideIdentifier) :
                    stepIdentifierWithSideId(Instruction0StepIdentifier, ActiveTaskLeftSideIdentifier)  ;

            if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
                {
                    String titleFormat = context.getString(R.string.rsb_shoulder_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_shoulder_range_of_motion_text_instruction_0);
                    if (rightSide) {
                        String title = String.format(titleFormat, TaskOptions.Side.RIGHT);
                        String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        stepList.add(step);
                    } else {
                        String title = String.format(titleFormat, TaskOptions.Side.LEFT);
                        String text = String.format(textFormat, TaskOptions.Side.LEFT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        stepList.add(step);
                    }
                }

                {
                    String titleFormat = context.getString(R.string.rsb_shoulder_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_shoulder_range_of_motion_text_instruction_1);
                    if (rightSide) {
                        String title = String.format(titleFormat, TaskOptions.Side.RIGHT);
                        String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                        stepList.add(step);
                    } else {
                        String title = String.format(titleFormat, TaskOptions.Side.LEFT);
                        String text = String.format(textFormat, TaskOptions.Side.LEFT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                        stepList.add(step);
                    }
                }

                {
                    String titleFormat = context.getString(R.string.rsb_shoulder_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_shoulder_range_of_motion_text_instruction_2);
                    if (rightSide) {
                        String title = String.format(titleFormat, TaskOptions.Side.RIGHT);
                        String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        step.setImage(ResUtils.RangeOfMotion.SHOULDER_START_RIGHT);
                        stepList.add(step);
                    } else {
                        String title = String.format(titleFormat, TaskOptions.Side.LEFT);
                        String text = String.format(textFormat, TaskOptions.Side.LEFT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        step.setImage(ResUtils.RangeOfMotion.SHOULDER_START_LEFT);
                        stepList.add(step);
                    }
                }

                {
                    String titleFormat = context.getString(R.string.rsb_shoulder_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_shoulder_range_of_motion_text_instruction_3);
                    if (rightSide) {
                        String title = String.format(titleFormat, TaskOptions.Side.RIGHT);
                        String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        step.setImage(ResUtils.RangeOfMotion.SHOULDER_MAXIMUM_RIGHT);
                        stepList.add(step);
                    } else {
                        String title = String.format(titleFormat, TaskOptions.Side.LEFT);
                        String text = String.format(textFormat, TaskOptions.Side.LEFT);
                        InstructionStep step = new InstructionStep(sideIdentifier, title, text);
                        step.setImage(ResUtils.RangeOfMotion.SHOULDER_MAXIMUM_LEFT);
                        stepList.add(step);
                    }
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step will begin. */

                {
                    String titleFormat = context.getString(R.string.rsb_shoulder_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_shoulder_range_of_motion_touch_anywhere_step_instruction);
                    if (rightSide) {
                        String title = String.format(titleFormat, TaskOptions.Side.RIGHT);
                        String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                        TouchAnywhereStep step = new TouchAnywhereStep(sideIdentifier, title, text);
                        step.setSpokenInstruction(text);
                        stepList.add(step);
                    } else {
                        String title = String.format(titleFormat, TaskOptions.Side.LEFT);
                        String text = String.format(textFormat, TaskOptions.Side.LEFT);
                        TouchAnywhereStep step = new TouchAnywhereStep(sideIdentifier, title, text);
                        step.setSpokenInstruction(text);
                        stepList.add(step);
                    }
                }

                /* When the RangeOfMotionStep begins, the spoken instruction commences automatically
                and device motion recording begins. Touching the screen ends the step and recording
                of device motion, and the next step will begin */

                {
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();

                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }
                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }

                    {
                        String titleFormat = context.getString(R.string.rsb_shoulder_range_of_motion_title);
                        String textFormat = context.getString(R.string.rsb_shoulder_range_of_motion_spoken_instruction);
                        if (rightSide) {
                            String title = String.format(titleFormat, TaskOptions.Side.RIGHT);
                            String text = String.format(textFormat, TaskOptions.Side.RIGHT);
                            RangeOfMotionStep step = new RangeOfMotionStep(sideIdentifier, title, text);
                            step.setSpokenInstruction(text);
                            step.setRecorderConfigurationList(recorderConfigList);
                            stepList.add(step);
                        } else {
                            String title = String.format(titleFormat, TaskOptions.Side.LEFT);
                            String text = String.format(textFormat, TaskOptions.Side.LEFT);
                            RangeOfMotionStep step = new RangeOfMotionStep(sideIdentifier, title, text);
                            step.setSpokenInstruction(text);
                            step.setRecorderConfigurationList(recorderConfigList);
                            stepList.add(step);
                        }
                    }
                }
            }
            if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
                stepList.add(TaskFactory.makeCompletionStep(context));
            }
        }
        return new OrderedTask(identifier, stepList);
    }

    public static String stepIdentifierWithSideId(String stepId, String sideId) {
        if (sideId == null) {
            return stepId;
        }
        return String.format("%s.%s", stepId, sideId);
    }
}
