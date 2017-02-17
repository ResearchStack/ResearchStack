package org.researchstack.backbone.task.factory;

import android.content.Context;
import android.text.format.DateUtils;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.FitnessStep;
import org.researchstack.backbone.step.active.PedometerRecorderConfig;
import org.researchstack.backbone.step.active.RecorderConfig;
import org.researchstack.backbone.step.active.WalkingTaskStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by TheMDP on 2/15/17.
 *
 * In iOS, they included a bunch of static methods for building OrderedTasks in the
 * OrderedTask class.  However, this class was created to furthur encapsulate the creation
 * of Walking Tasks, specifically the timed walking task, walking back and forth task, and
 * the short walking task.
 */

public class WalkingTaskFactory {

    private static final float DEFAULT_STEP_DURATION_FALLBACK_FACTOR = 1.5f;
    private static final int DEFAULT_COUNTDOWN_DURATION = 5; // in seconds

    private static final int IGNORE_NUMBER_OF_STEPS = Integer.MAX_VALUE;
    private static final int SPEAK_WALK_DURATION_HALFWAY_THRESHOLD = 20; // in seconds

    public static final String ShortWalkOutboundStepIdentifier        = "walking.outbound";
    public static final String ShortWalkReturnStepIdentifier          = "walking.return";
    public static final String ShortWalkRestStepIdentifier            = "walking.rest";
    public static final String TimedWalkFormStepIdentifier            = "timed.walk.form";
    public static final String TimedWalkFormAFOStepIdentifier         = "timed.walk.form.afo";
    public static final String TimedWalkFormAssistanceStepIdentifier  = "timed.walk.form.assistance";
    public static final String TimedWalkTrial1StepIdentifier          = "timed.walk.trial1";
    public static final String TimedWalkTurnAroundStepIdentifier      = "timed.walk.turn.around";
    public static final String TimedWalkTrial2StepIdentifier          = "timed.walk.trial2";

    /**
     * Returns a predefined task that consists of a short walk.
     *
     * In a short walk task, the participant is asked to walk a short distance, which may be indoors.
     * Typical uses of the resulting data are to assess stride length, smoothness, sway, or other aspects
     * of the participant's gait.
     *
     * The presentation of the short walk task differs from the fitness check task in that the distance is
     * replaced by the number of steps taken, and the walk is split into a series of legs. After each leg,
     * the user is asked to turn and reverse direction.
     *
     * The data collected by this task can include accelerometer, device motion, and pedometer data.
     *
     * @param context                 can be app or activity, used for resources
     * @param identifier              The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription  A localized string describing the intended use of the data
     *                                collected. If the value of this parameter is `nil`, the default
     *                                localized text is displayed.
     * @param numberOfStepsPerLeg     The number of steps the participant is asked to walk. If the
     *                                pedometer is unavailable, a distance is suggested and a suitable
     *                                count down timer is displayed for each leg of the walk.
     * @param restDuration            The duration of the rest period in seconds. When the value of
     *                                this parameter is nonzero, the user is asked to stand still
     *                                for the specified rest period after the turn sequence
     *                                has been completed, and baseline data is collected.
     * @param optionList              Options that affect the features of the predefined task.
     *
     * @return An active short walk task that can be presented with an `ActiveTaskActivity` object.
     */
    public static OrderedTask shortWalkTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            int    numberOfStepsPerLeg,
            int    restDuration,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // Obtain sensor frequency for Walking Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_tremor_task);

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            {
                String title = context.getString(R.string.rsb_WALK_TASK_TITLE);
                InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, intendedUseDescription);
                step.setMoreDetailText(context.getString(R.string.rsb_WALK_INTRO_TEXT));
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_WALK_TASK_TITLE);
                String textFormat = context.getString(R.string.rsb_walk_intro_2_text_ld);
                String text = String.format(textFormat, numberOfStepsPerLeg);
                InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                step.setMoreDetailText(context.getString(R.string.rsb_WALK_INTRO_2_DETAIL));
                step.setImage(ResUtils.PHONE_IN_POCKET);
                stepList.add(step);
            }
        }

        {
            CountdownStep step = new CountdownStep(CountdownStepIdentifier);
            step.setStepDuration(DEFAULT_COUNTDOWN_DURATION);
            stepList.add(step);
        }

        {
            {
                List<RecorderConfig> recorderConfigList = new ArrayList<>();
                if (!optionList.contains(TaskExcludeOption.PEDOMETER)) {
                    recorderConfigList.add(new PedometerRecorderConfig(PedometerRecorderIdentifier));
                }
                if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                }
                if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                }

                {
                    WalkingTaskStep step = new WalkingTaskStep(ShortWalkOutboundStepIdentifier);
                    String titleFormat = context.getString(R.string.rsb_WALK_OUTBOUND_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, numberOfStepsPerLeg);
                    step.setTitle(title);
                    step.setSpokenInstruction(step.getTitle());
                    step.setRecorderConfigurationList(recorderConfigList);
                    step.setShouldContinueOnFinish(true);
                    step.setShouldStartTimerAutomatically(true);
                    step.setStepDuration(computeFallbackDuration(numberOfStepsPerLeg));
                    step.setNumberOfStepsPerLeg(numberOfStepsPerLeg);
                    step.setShouldVibrateOnStart(true);
                    step.setShouldPlaySoundOnStart(true);
                    stepList.add(step);
                }
            }

            {
                List<RecorderConfig> recorderConfigList = new ArrayList<>();
                if (!optionList.contains(TaskExcludeOption.PEDOMETER)) {
                    recorderConfigList.add(new PedometerRecorderConfig(PedometerRecorderIdentifier));
                }
                if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                }
                if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                }

                {
                    WalkingTaskStep step = new WalkingTaskStep(ShortWalkReturnStepIdentifier);
                    step.setTitle(context.getString(R.string.rsb_WALK_RETURN_INSTRUCTION_FORMAT));
                    step.setSpokenInstruction(step.getTitle());
                    step.setRecorderConfigurationList(recorderConfigList);
                    step.setShouldContinueOnFinish(true);
                    step.setShouldStartTimerAutomatically(true);
                    step.setStepDuration(computeFallbackDuration(numberOfStepsPerLeg));
                    step.setNumberOfStepsPerLeg(numberOfStepsPerLeg);
                    step.setShouldVibrateOnStart(true);
                    step.setShouldPlaySoundOnStart(true);
                    stepList.add(step);
                }
            }

            if (restDuration > 0) {
                if (restDuration > 0) {
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();
                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }
                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    FitnessStep step = new FitnessStep(ShortWalkRestStepIdentifier);

                    String titleFormat = context.getString(R.string.rsb_WALK_STAND_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, convertDurationToString(context, restDuration));
                    step.setTitle(title);
                    String voiceTitleFormat = context.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT);
                    String voiceTitle = String.format(voiceTitleFormat, convertDurationToString(context, restDuration));
                    step.setSpokenInstruction(voiceTitle);
                    step.setRecorderConfigurationList(recorderConfigList);
                    step.setShouldContinueOnFinish(true);
                    step.setShouldStartTimerAutomatically(true);
                    step.setStepDuration(restDuration);
                    step.setShouldVibrateOnStart(true);
                    step.setShouldPlaySoundOnStart(true);
                    step.setShouldVibrateOnFinish(true);
                    step.setShouldPlaySoundOnFinish(true);
                    stepList.add(step);
                }
            }
        }

        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }

        return new OrderedTask(identifier, stepList);
    }

    /**
     * Returns a predefined task that consists of a short walk back and forth.
     *
     * In a short walk task, the participant is asked to walk a short distance, which may be indoors.
     * Typical uses of the resulting data are to assess stride length, smoothness, sway, or other aspects
     * of the participant's gait.
     *
     * The presentation of the back and forth walk task differs from the short walk in that the participant
     * is asked to walk back and forth rather than walking in a straight line for a certain number of steps.
     *
     * The participant is then asked to turn in a full circle and then stand still.
     *
     * This task is intended to allow the participant to walk in a confined space where the participant
     * does not have access to a long hallway to walk in a continuous straight line. Additionally, by asking
     * the participant to turn in a full circle and then stand still, the activity can access balance and
     * concentration.
     *
     * The data collected by this task can include accelerometer, device motion, and pedometer data.
     *
     * @param context                 can be app or activity, used for resources
     * @param identifier              The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription  A localized string describing the intended use of the data
     *                                collected. If the value of this parameter is `nil`, the default
     *                                localized text is displayed.
     * @param walkDuration            The duration of the walking period in seconds.
     * @param restDuration            The duration of the rest period in seconds.
     *                                When the value of this parameter is
     *                                nonzero, the user is asked to stand still for the specified rest
     *                                period after the turn sequence has been completed, and baseline
     *                                data is collected.
     * @param optionList              Options that affect the features of the predefined task.
     *
     * @return An active short walk task that can be presented with an `ActiveTaskActivity` object.
     */
    public static OrderedTask walkBackAndForthTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            int walkDuration,
            int restDuration,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // Obtain sensor frequency for Walking Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_tremor_task);

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            {
                String title = context.getString(R.string.rsb_WALK_TASK_TITLE);
                InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, intendedUseDescription);
                step.setMoreDetailText(context.getString(R.string.rsb_WALK_INTRO_TEXT));
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_WALK_TASK_TITLE);
                String text = context.getString(R.string.rsb_WALK_INTRO_2_TEXT_BACK_AND_FORTH_INSTRUCTION);
                InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                step.setMoreDetailText(context.getString(R.string.rsb_WALK_INTRO_2_DETAIL_BACK_AND_FORTH_INSTRUCTION));
                step.setImage(ResUtils.PHONE_IN_POCKET);
                stepList.add(step);
            }
        }

        {
            CountdownStep step = new CountdownStep(CountdownStepIdentifier);
            step.setStepDuration(DEFAULT_COUNTDOWN_DURATION);
            stepList.add(step);
        }

        {
            {
                List<RecorderConfig> recorderConfigList = new ArrayList<>();
                if (!optionList.contains(TaskExcludeOption.PEDOMETER)) {
                    recorderConfigList.add(new PedometerRecorderConfig(PedometerRecorderIdentifier));
                }
                if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                }
                if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                    recorderConfigList.add(new AccelerometerRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                }

                {
                    WalkingTaskStep step = new WalkingTaskStep(ShortWalkOutboundStepIdentifier);
                    String titleFormat = context.getString(R.string.rsb_WALK_BACK_AND_FORTH_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, convertDurationToString(context, walkDuration));
                    step.setTitle(title);
                    step.setSpokenInstruction(title);
                    step.setRecorderConfigurationList(recorderConfigList);
                    step.setShouldContinueOnFinish(true);
                    step.setShouldStartTimerAutomatically(true);
                    step.setStepDuration(walkDuration);
                    step.setNumberOfStepsPerLeg(IGNORE_NUMBER_OF_STEPS);
                    step.setShouldSpeakRemainingTimeAtHalfway(walkDuration > SPEAK_WALK_DURATION_HALFWAY_THRESHOLD);
                    step.setShouldVibrateOnStart(true);
                    step.setShouldPlaySoundOnStart(true);
                    stepList.add(step);
                }
            }

            if (restDuration > 0) {
                if (restDuration > 0) {
                    List<RecorderConfig> recorderConfigList = new ArrayList<>();
                    if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
                    }
                    if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                        recorderConfigList.add(new AccelerometerRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    FitnessStep step = new FitnessStep(ShortWalkRestStepIdentifier);

                    String titleFormat = context.getString(R.string.rsb_WALK_BACK_AND_FORTH_STAND_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, convertDurationToString(context, restDuration));
                    step.setTitle(title);
                    step.setSpokenInstruction(title);
                    step.setRecorderConfigurationList(recorderConfigList);
                    step.setShouldContinueOnFinish(true);
                    step.setShouldStartTimerAutomatically(true);
                    step.setStepDuration(restDuration);
                    step.setShouldVibrateOnStart(true);
                    step.setShouldPlaySoundOnStart(true);
                    step.setShouldVibrateOnFinish(true);
                    step.setShouldPlaySoundOnFinish(true);
                    step.setFinishedSpokenInstruction(context.getString(R.string.rsb_WALK_BACK_AND_FORTH_FINISHED_VOICE));
                    step.setShouldSpeakRemainingTimeAtHalfway(restDuration > SPEAK_WALK_DURATION_HALFWAY_THRESHOLD);
                    stepList.add(step);
                }
            }
        }

        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }

        return new OrderedTask(identifier, stepList);
    }

    /**
     * In iOS, this method turns duration into "for X minutes, Y seconds" but in Android,
     * you can only localize a duration to be  "in X minutes, Y seconds", so use that instead
     * @param durationInSeconds the duration in seconds
     * @return a string formatted to "in X minutes, Y seconds" where x & y are from durationInSeconds
     */
    private static String convertDurationToString(Context context, int durationInSeconds) {
        int minutes = durationInSeconds / 60;
        int seconds = durationInSeconds - minutes * 60;
        if (minutes > 0) {
            return String.format(Locale.getDefault(), "%d %s, %d %s",
                    minutes, context.getString(R.string.rsb_minutes).toLowerCase(),
                    seconds, context.getString(R.string.rsb_time_seconds).toLowerCase());
        } else {
            return String.format(Locale.getDefault(), "%d %s",
                    seconds, context.getString(R.string.rsb_time_seconds).toLowerCase());
        }
    }

    /**
     * @return a step duration value that can be used if number of steps takes too long
     */
    private static int computeFallbackDuration(int numberOfStepsPerLeg) {
        return (int)(numberOfStepsPerLeg * DEFAULT_STEP_DURATION_FALLBACK_FACTOR);
    }
}
