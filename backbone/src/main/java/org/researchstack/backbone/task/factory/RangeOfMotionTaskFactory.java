package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TouchAnywhereStep;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
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
 * In iOS, they included static methods for building OrderedTasks in the
 * OrderedTask class.  However, this class was created to further encapsulate the creation
 * of Range of Motion (ROM) Tasks, specifically the knee ROM task, and shoulder ROM task.
 */

public class RangeOfMotionTaskFactory {

    public static final String RangeOfMotionStepIdentifier = "rangeOfMotion";

    /**
     * Returns a predefined task that measures the range of motion for either a left or right knee.
     *
     * The data collected by this task is device motion data.
     *
     * @param context                can be app or activity, used for resources
     * @param identifier             The task identifier to use for this task, appropriate to the study.
     * @param limbOption             Which knee is being measured.
     * @param intendedUseDescription A localized string describing the intended use of the data
     *                               collected. If the value of this parameter is `nil`, the default
     *                               localized text is displayed.
     * @param optionList             Options that affect the features of the predefined task.
     * @return                       An active knee range of motion task that can be presented with an `ActiveTaskActivity` object.
     */

    public static OrderedTask kneeRangeOfMotionTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            LimbTaskOptions.Limb limbOption, // Based on TremorTaskFactory
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();


        // Obtain sensor frequency for Range of Motion Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_range_of_motion_task);

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            {
                String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                //String title = String.format(title, limbOption);
                InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, intendedUseDescription);
                step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_0));
                stepList.add(step);
            }

            if (limbOption == LimbTaskOptions.Limb.RIGHT || limbOption == LimbTaskOptions.Limb.BOTH) {
                {
                    String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                    //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1));
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                    //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2));
                    step.setImage(ResUtils.RangeOfMotion.KNEE_START_RIGHT);
                    stepList.add(step);
                }

                {
                    String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.RIGHT);
                    InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                    //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3));
                    step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_RIGHT);
                    stepList.add(step);
                }
            }

            if (limbOption == LimbTaskOptions.Limb.LEFT || limbOption == LimbTaskOptions.Limb.BOTH) {
                {
                    String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                    //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1));
                    step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                    stepList.add(step);
                }

                {
                    String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                    //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2));
                    step.setImage(ResUtils.RangeOfMotion.KNEE_START_LEFT);
                    stepList.add(step);
                }

                {
                    String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                    step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3));
                    step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_LEFT);
                    stepList.add(step);
                }

                /* This next step is the 'touch anywhere' (on the screen) step. When this step begins, the spoken
                instruction commences automatically. Touching the screen ends the step and the next step begins. */

                {
                    String textFormat = context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction);
                    String text = String.format(textFormat, LimbTaskOptions.Limb.LEFT);
                    TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhereStepIdentifier);
                    stepList.add(step);
                }

                /* When this step begins, the spoken instruction commences automatically and device motion recording
                begins. Touching the screen ends the step and the recording of device motion, and the next step begins */

                {
                    //use this (from Walking Task)?
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();
                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }
                    //or use this (from TremorTaskFactory)?
                    //step.setRecorderConfigurationList(Arrays.asList(
                    //        new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq)
                    //));
                    
                    {
                        RangeOfMotionStep step = new RangeOfMotionStep(RangeOfMotionStepIdentifier);
                        //step.setTitle(context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction));
                        String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction);
                        String title = String.format(titleFormat, LimbTaskOptions.Limb.LEFT);
                        step.setSpokenInstruction(title);
                        //step.setSpokenInstruction(step.getSpokenInstruction());
                        //step.getSpokenInstruction(context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction));
                        step.setRecorderConfigurationList(recorderConfigList);
                        step.setShouldVibrateOnStart(true);
                        step.setShouldPlaySoundOnStart(true);
                        step.setShouldContinueOnFinish(true);
                        step.setShouldStartTimerAutomatically(true);
                        step.setShouldVibrateOnFinish(true);
                        step.setShouldPlaySoundOnFinish(true);
                        stepList.add(step);
                    
                    /*
                     //Possibly useful for above?
                     String voiceTitleFormat = context.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT);
                     String voiceTitle = String.format(voiceTitleFormat, TaskFactory.convertDurationToString(context, restDuration));
                     step.setSpokenInstruction(voiceTitle);
                     */

                    }
                }
            }

            if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
                stepList.add(TaskFactory.makeCompletionStep(context));
            }

            return new OrderedTask(identifier, stepList);

        }
    }
}


/*

  iOS version... for comparison

 The knee range of motion task returns a task that measures the range of motion for either a left or right knee.

 @param identifier              The task identifier to use for this task, appropriate to the study.
 @param limbOption              Which knee is being measured.
 @param intendedUseDescription  A localized string describing the intended use of the data collected. If the value of this parameter is `nil`, default localized text is used.
 @param options                 Options that affect the features of the predefined task.

+ (ORKOrderedTask *)kneeRangeOfMotionTaskWithIdentifier:(NSString *)identifier
        limbOption:(ORKPredefinedTaskLimbOption)limbOption
        intendedUseDescription:(nullable NSString *)intendedUseDescription
        options:(ORKPredefinedTaskOption)options;


        #pragma mark - kneeRangeOfMotionTask

        NSString *const ORKKneeRangeOfMotionStepIdentifier = @"knee.range.of.motion";

        + (ORKOrderedTask *)kneeRangeOfMotionTaskWithIdentifier:(NSString *)identifier
        limbOption:(ORKPredefinedTaskLimbOption)limbOption
        intendedUseDescription:(NSString *)intendedUseDescription
        options:(ORKPredefinedTaskOption)options {
        NSMutableArray *steps = [NSMutableArray array];
        NSString *limbType = ORKLocalizedString(@"LIMB_RIGHT", nil);
        UIImage *kneeStartImage = [UIImage imageNamed:@"knee_start_right" inBundle:[NSBundle bundleForClass:[self class]] compatibleWithTraitCollection:nil];
        UIImage *kneeMaximumImage = [UIImage imageNamed:@"knee_maximum_right" inBundle:[NSBundle bundleForClass:[self class]] compatibleWithTraitCollection:nil];

        if (limbOption == ORKPredefinedTaskLimbOptionLeft) {
        limbType = ORKLocalizedString(@"LIMB_LEFT", nil);

        kneeStartImage = [UIImage imageNamed:@"knee_start_left" inBundle:[NSBundle bundleForClass:[self class]] compatibleWithTraitCollection:nil];
        kneeMaximumImage = [UIImage imageNamed:@"knee_maximum_left" inBundle:[NSBundle bundleForClass:[self class]] compatibleWithTraitCollection:nil];
        }

        if (!(options & ORKPredefinedTaskOptionExcludeInstructions)) {
        ORKInstructionStep *instructionStep0 = [[ORKInstructionStep alloc] initWithIdentifier:ORKInstruction0StepIdentifier];
        instructionStep0.title = ORKLocalizedString(@"RANGE_OF_MOTION_TITLE", nil);
        instructionStep0.text = intendedUseDescription;
        instructionStep0.detailText = ([limbType isEqualToString:ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_LEFT", nil) : ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_0_RIGHT", nil);
        instructionStep0.shouldTintImages = YES;
        ORKStepArrayAddStep(steps, instructionStep0);

        ORKInstructionStep *instructionStep1 = [[ORKInstructionStep alloc] initWithIdentifier:ORKInstruction1StepIdentifier];
        instructionStep1.title = ORKLocalizedString(@"RANGE_OF_MOTION_TITLE", nil);
        instructionStep1.text = ([limbType isEqualToString:ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_LEFT", nil) : ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_1_RIGHT", nil);
        ORKStepArrayAddStep(steps, instructionStep1);

        ORKInstructionStep *instructionStep2 = [[ORKInstructionStep alloc] initWithIdentifier:ORKInstruction2StepIdentifier];
        instructionStep2.title = ORKLocalizedString(@"RANGE_OF_MOTION_TITLE", nil);
        instructionStep2.text = ([limbType isEqualToString:ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TITLE_LEFT", nil) : ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TITLE_RIGHT", nil);

        instructionStep2.detailText = ([limbType isEqualToString:ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_LEFT", nil) : ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_2_RIGHT", nil);
        instructionStep2.image = kneeStartImage;
        instructionStep2.shouldTintImages = YES;
        ORKStepArrayAddStep(steps, instructionStep2);

        ORKInstructionStep *instructionStep3 = [[ORKInstructionStep alloc] initWithIdentifier:ORKInstruction3StepIdentifier];
        instructionStep3.title = ORKLocalizedString(@"RANGE_OF_MOTION_TITLE", nil);
        instructionStep3.text = ([limbType isEqualToString:ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_LEFT", nil) : ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TEXT_INSTRUCTION_3_RIGHT", nil);

        instructionStep3.image = kneeMaximumImage;
        instructionStep3.shouldTintImages = YES;
        ORKStepArrayAddStep(steps, instructionStep3);
        }
        NSString *instructionText = ([limbType isEqualToString:ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TOUCH_ANYWHERE_STEP_INSTRUCTION_LEFT", nil) : ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_TOUCH_ANYWHERE_STEP_INSTRUCTION_RIGHT", nil);
        ORKTouchAnywhereStep *touchAnywhereStep = [[ORKTouchAnywhereStep alloc] initWithIdentifier:ORKTouchAnywhereStepIdentifier instructionText:instructionText];
        touchAnywhereStep.title = ORKLocalizedString(@"RANGE_OF_MOTION_TITLE", nil);
        ORKStepArrayAddStep(steps, touchAnywhereStep);

        touchAnywhereStep.spokenInstruction = touchAnywhereStep.title;

        ORKDeviceMotionRecorderConfiguration *deviceMotionRecorderConfig = [[ORKDeviceMotionRecorderConfiguration alloc] initWithIdentifier:ORKDeviceMotionRecorderIdentifier frequency:100];

        ORKRangeOfMotionStep *kneeRangeOfMotionStep = [[ORKRangeOfMotionStep alloc] initWithIdentifier:ORKKneeRangeOfMotionStepIdentifier limbOption:limbOption];
        kneeRangeOfMotionStep.title = ORKLocalizedString(@"RANGE_OF_MOTION_TITLE", nil);
        kneeRangeOfMotionStep.text = ([limbType isEqualToString: ORKLocalizedString(@"LIMB_LEFT", nil)])? ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_SPOKEN_INSTRUCTION_LEFT", nil) :
        ORKLocalizedString(@"KNEE_RANGE_OF_MOTION_SPOKEN_INSTRUCTION_RIGHT", nil);

        kneeRangeOfMotionStep.spokenInstruction = kneeRangeOfMotionStep.text;
        kneeRangeOfMotionStep.recorderConfigurations = @[deviceMotionRecorderConfig];
        kneeRangeOfMotionStep.optional = NO;

        ORKStepArrayAddStep(steps, kneeRangeOfMotionStep);

        if (!(options & ORKPredefinedTaskOptionExcludeConclusion)) {
        ORKCompletionStep *completionStep = [self makeCompletionStep];
        ORKStepArrayAddStep(steps, completionStep);
        }

        ORKOrderedTask *task = [[ORKOrderedTask alloc] initWithIdentifier:identifier steps:steps];
        return task;
        }

 */
