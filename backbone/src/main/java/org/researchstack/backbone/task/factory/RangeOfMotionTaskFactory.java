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

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by David Evans, Laurence Hurst, Simon Hartley, 2019.
 *
 * In iOS ResearchKit, they included static methods for building OrderedTasks in the OrderedTask
 * class. However, this class was created to further encapsulate the creation of Range of Motion
 * (ROM) Tasks, specifically the knee ROM task.
 */

public class RangeOfMotionTaskFactory {

    public static final String RangeOfMotionStepIdentifier = "rangeOfMotion";

    /**
     * Returns a predefined task that measures the range of motion for a left knee, a right knee, or both knees.
     * <p>
     * The data collected by this task is device motion data.
     *
     * @param context                can be app or activity, used for resources
     * @param identifier             The task identifier to use for this task, appropriate to the study.
     * @param limbOption             The limb that is being measured.
     * @param intendedUseDescription A localized string describing the intended use of the data
     *                               collected. If the value of this parameter is `nil`, the default
     *                               localized text is displayed.
     * @param optionList             Options that affect the features of the predefined task.
     * @return An active knee range of motion task that can be presented with an `ActiveTaskActivity` object.
     */

    public static OrderedTask kneeRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            LimbTaskOptions.Limb limbOption,
            List<TaskExcludeOption> optionList) {
        List<Step> stepList = new ArrayList<>();

        // Obtain sensor frequency for Range of Motion Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

            if (limbOption == LimbTaskOptions.Limb.RIGHT || limbOption == LimbTaskOptions.Limb.BOTH) {

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.RIGHT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_0);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, text);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.RIGHT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.RIGHT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_START_RIGHT);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.RIGHT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_RIGHT);
                    stepList.add(step);
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step begins. */

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.RIGHT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhereStepIdentifier, title, text);
                    step.setSpokenInstruction(text);
                    //step.setSpokenInstruction(step.getSpokenInstruction());
                    stepList.add(step);
                }

                /* When the RangeOfMotionStep begins, the spoken instruction commences automatically and device motion recording
                begins. Touching the screen ends the step and recording of device motion, and the next step begins */

                {   //use this format (from Walking Task)?
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();

                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }

                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    {
                        String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                        String title = String.format(titleFormat, LimbTaskOptions.Limb.RIGHT);
                        String textFormat = context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction);
                        String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                        RangeOfMotionStep step = new RangeOfMotionStep(RangeOfMotionStepIdentifier, title, text);
                        step.setSpokenInstruction(text);
                        step.setRecorderConfigurationList(recorderConfigList);
                        stepList.add(step);
                    }
                }
            }

            if (limbOption == LimbTaskOptions.Limb.LEFT || limbOption == LimbTaskOptions.Limb.BOTH) {

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_0);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, text);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_START_LEFT);
                    stepList.add(step);
                }

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                    step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_LEFT);
                    stepList.add(step);
                }

                /* When this next step (TouchAnywhereStep) begins, the spoken instruction commences
                automatically. Touching the screen ends the step and the next step begins. */

                {
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhereStepIdentifier, title, text);
                    step.setSpokenInstruction(text);
                    //step.setSpokenInstruction(step.getSpokenInstruction());
                    stepList.add(step);
                }

                /* When the RangeOfMotionStep begins, the spoken instruction commences automatically and device motion recording
                begins. Touching the screen ends the step and recording of device motion, and the next step begins */

                {   //use this format (from Walking Task)?
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();

                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }

                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    {
                        String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_title);
                        String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                        String textFormat = context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction);
                        String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                        RangeOfMotionStep step = new RangeOfMotionStep(RangeOfMotionStepIdentifier, title, text);
                        step.setSpokenInstruction(text);
                        step.setRecorderConfigurationList(recorderConfigList);
                        stepList.add(step);
                    }
                }

                if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
                    stepList.add(TaskFactory.makeCompletionStep(context));
                }
            }
        }
        return new OrderedTask(identifier, stepList);
    }
}
