package org.researchstack.backbone.task.factory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.RequireSystemFeatureStep;
import org.researchstack.backbone.step.Step;
//import org.researchstack.backbone.step.active.TimedWalkStep;
import org.researchstack.backbone.step.active.RangeOfMotionStep;
//import org.researchstack.backbone.step.active.recorder.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.TouchAnywhereStep;
//import org.researchstack.backbone.step.active.CountdownStep;
//import org.researchstack.backbone.step.active.FitnessStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorderConfig;
//import org.researchstack.backbone.step.active.recorder.LocationRecorderConfig;
//import org.researchstack.backbone.step.active.recorder.PedometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
//import org.researchstack.backbone.step.active.WalkingTaskStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by David Evans, 2019.
 *
 * In iOS, they included a bunch of static methods for building OrderedTasks in the
 * OrderedTask class.  However, this class was created to further encapsulate the creation
 * of Range of Motion (ROM) Tasks, specifically the knee ROM task, and shoulder ROM task.
 */

public class RangeOfMotionTaskFactory {
//    private static final float DEFAULT_STEP_DURATION_FALLBACK_FACTOR = 1.5f;
//    private static final int DEFAULT_COUNTDOWN_DURATION = 5; // in seconds
//    private static final int DEFAULT_LIMB_OPTION = right; // this might be incorrect

//    private static final int IGNORE_NUMBER_OF_STEPS = Integer.MAX_VALUE;
//    private static final int SPEAK_WALK_DURATION_HALFWAY_THRESHOLD = 20; // in seconds

//    public static final String GpsFeatureStepIdentifier               = "gpsfeature";
//    public static final String LocationPermissionsStepIdentifier      = "locationpermission";
//    public static final String ShortWalkOutboundStepIdentifier        = "walking.outbound";
//    public static final String ShortWalkReturnStepIdentifier          = "walking.return";
//    public static final String ShortWalkRestStepIdentifier            = "walking.rest";
//    public static final String TimedWalkFormStepIdentifier            = "timed.walk.form";
//
//    public static final String TimedWalkFormAFOStepIdentifier         = "timed.walk.form.afo";
//    public static final String TimedWalkFormAssistanceStepIdentifier  = "timed.walk.form.assistance";
//    public static final String TimedWalkTrial1StepIdentifier          = "timed.walk.trial1";
//    public static final String TimedWalkTurnAroundStepIdentifier      = "timed.walk.turn.around";
//    public static final String TimedWalkTrial2StepIdentifier          = "timed.walk.trial2";

//    public static final String TouchAnywhere1StepIdentifier            = "touchAnywhere1";
//    public static final String SpokenInstructionStepIdentifier        = "spokenInstruction";

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
            //     int     numberOfStepsPerLeg,
            //     int     restDuration,
            //String limbOption, // This might be incorrect
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

            {
                String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1);
                String text = String.format(textFormat, limbOption);
                InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_1));
                step.setImage(ResUtils.Audio.PHONE_SOUND_ON);
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2);
                String text = String.format(textFormat, limbOption);
                InstructionStep step = new InstructionStep(Instruction2StepIdentifier, title, text);
                //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_2));
                step.setImage(ResUtils.RangeOfMotion.KNEE_START_LEFT);
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_knee_range_of_motion_title);
                String textFormat = context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3);
                String text = String.format(textFormat, limbOption);
                InstructionStep step = new InstructionStep(Instruction3StepIdentifier, title, text);
                //step.setMoreDetailText(context.getString(R.string.rsb_knee_range_of_motion_text_instruction_3));
                step.setImage(ResUtils.RangeOfMotion.KNEE_MAXIMUM_LEFT);
                stepList.add(step);
            }
        }

        //This next step is the first 'touch anywhere' on the screen step. When this step begins, the spoken instruction commences automatically. Touching the screen ends the step and the next step begins.
        {
            {
                {
                    TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhere1StepIdentifier);
                    String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction);
                    String title = String.format(titleFormat, limbOption);
                    //step.setTitle(context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction));
                    step.setTitle(title);
                    step.setSpokenInstruction(title);
                    //step.setSpokenInstruction(step.getSpokenInstruction());
                    //step.setSpokenInstruction(step.getTitle());
                    //step.getSpokenInstruction(context.getString(R.string.rsb_knee_range_of_motion_touch_anywhere_step_instruction));
                    //step.setRecorderConfigurationList(recorderConfigList);
                    //step.setLimbOption(computeFallbackLimbOption(limbOption));
                    //step.setlimbOption(limbOption);
                    step.setShouldVibrateOnStart(true);
                    step.setShouldPlaySoundOnStart(true);
                    step.setShouldContinueOnFinish(true);
                    step.setShouldStartTimerAutomatically(true);
                    step.setShouldVibrateOnFinish(true);
                    step.setShouldPlaySoundOnFinish(true);
                    stepList.add(step);
                }
            }

            //This next step is the second 'touch anywhere' (on the screen) step. When this step begins, the spoken instruction commences automatically and device motion is recorded. Touchng the screen ends the step and the recording of device motion, and the next step begins.
            {
                {
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();
                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }
                    {
                        TouchAnywhereStep step = new TouchAnywhereStep(TouchAnywhere2StepIdentifier);
                        //step.setTitle(context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction));
                        String titleFormat = context.getString(R.string.rsb_knee_range_of_motion_spoken_instruction);
                        String title = String.format(titleFormat, limbOption);
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


            /**
             * @return a limb option that can be used if a limb option is not selected
             */
//          private static int computeFallbackLimbOption ( int limbOption){
//          return (int) (limbOption * DEFAULT_LIMB_OPTION);
//      }

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
