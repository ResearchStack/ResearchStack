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
import org.researchstack.backbone.step.active.TimedWalkStep;
import org.researchstack.backbone.step.active.recorder.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.FitnessStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorderConfig;
import org.researchstack.backbone.step.active.recorder.LocationRecorderConfig;
import org.researchstack.backbone.step.active.recorder.PedometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.step.active.WalkingTaskStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static final String GpsFeatureStepIdentifier               = "gpsfeature";
    public static final String LocationPermissionsStepIdentifier      = "locationpermission";
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
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_walking_task);

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
                    recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
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
                    recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
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
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    FitnessStep step = new FitnessStep(ShortWalkRestStepIdentifier);

                    String titleFormat = context.getString(R.string.rsb_WALK_STAND_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, TaskFactory.convertDurationToString(context, restDuration));
                    step.setTitle(title);
                    String voiceTitleFormat = context.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT);
                    String voiceTitle = String.format(voiceTitleFormat, TaskFactory.convertDurationToString(context, restDuration));
                    step.setSpokenInstruction(voiceTitle);
                    step.setRecorderConfigurationList(recorderConfigList);
                    step.setShouldShowDefaultTimer(true);
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
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_walking_task);

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
                    recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                }

                {
                    WalkingTaskStep step = new WalkingTaskStep(ShortWalkOutboundStepIdentifier);
                    String titleFormat = context.getString(R.string.rsb_WALK_BACK_AND_FORTH_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, TaskFactory.convertDurationToString(context, walkDuration));
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
                        recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
                    }

                    FitnessStep step = new FitnessStep(ShortWalkRestStepIdentifier);

                    String titleFormat = context.getString(R.string.rsb_WALK_BACK_AND_FORTH_STAND_INSTRUCTION_FORMAT);
                    String title = String.format(titleFormat, TaskFactory.convertDurationToString(context, restDuration));
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
     * Returns a predefined task that consists of a timed walk.
     *
     * In a timed walk task, the participant is asked to walk for a specific distance as quickly as
     * possible, but safely. The task is immediately administered again by having the patient walk back
     * the same distance.
     * A timed walk task can be used to measure lower extremity function.
     *
     * The presentation of the timed walk task differs from both the fitness check task and the short
     * walk task in that the distance is fixed. After a first walk, the user is asked to turn and reverse
     * direction.
     *
     * The data collected by this task can include accelerometer, device motion, pedometer data,
     * and location where available.
     *
     * Data collected by the task is in the form of an `TimedWalkResult` object.
     *
     * @param context                     Can be app or activity, used to get resources
     * @param identifier                  The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription      A localized string describing the intended use of the data
     *                                    collected. If the value of this parameter is `nil`, the default
     *                                    localized text is displayed.
     * @param distanceInMeters            The timed walk distance in meters.
     * @param timeLimit                   The time limit to complete the trials in seconds
     * @param turnAroundTimeLimit         The turn around time limit in seconds
     * @param includeAssistiveDeviceForm  A Boolean value that indicates whether to inlude the form step
     *                                    about the usage of an assistive device.
     * @param optionList                  Options that affect the features of the predefined task.
     *
     * @return An active timed walk task that can be presented with an `ORKTaskViewController` object.
     */
    public static OrderedTask timedWalkTask(
            Context context,
            String identifier,
            String intendedUseDescription,
            double distanceInMeters,
            int    timeLimit,
            int    turnAroundTimeLimit,
            boolean includeAssistiveDeviceForm,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // In-app permissions were added in Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // This isn't in iOS, but in Android we need to check for this so that location permission is granted
            PackageManager pm = context.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                // include a permission request step that requires location
                String title = context.getString(R.string.rsb_permission_location_title);
                String text = context.getString(R.string.rsb_permission_location_desc);
                stepList.add(new PermissionsStep(LocationPermissionsStepIdentifier, title, text));
            }
        }

        // We also need to check if GPS is turned on, and turn it on if it is not
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            String title = context.getString(R.string.rsb_system_feature_gps_title);
            String text = context.getString(R.string.rsb_system_feature_gps_text);
            stepList.add(new RequireSystemFeatureStep(
                    RequireSystemFeatureStep.SystemFeature.GPS, GpsFeatureStepIdentifier, title, text));
        }

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            String title = context.getString(R.string.rsb_TIMED_WALK_TITLE);
            InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, intendedUseDescription);
            step.setMoreDetailText(context.getString(R.string.rsb_TIMED_WALK_INTRO_DETAIL));
            stepList.add(step);
        }

        if (includeAssistiveDeviceForm) {

            BooleanAnswerFormat answerFormat1 = new BooleanAnswerFormat(
                    context.getString(R.string.rsb_BOOL_YES),
                    context.getString(R.string.rsb_BOOL_NO));
            QuestionStep questionStep1 = new QuestionStep(TimedWalkFormAFOStepIdentifier, null, answerFormat1);
            questionStep1.setText(context.getString(R.string.rsb_TIMED_WALK_QUESTION_TEXT));
            questionStep1.setOptional(false);

            String choice1Text = context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE);
            Choice<String> choice1 = new Choice<>(choice1Text, choice1Text);

            String choice2Text = context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_2);
            Choice<String> choice2 = new Choice<>(choice2Text, choice2Text);

            String choice3Text = context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_3);
            Choice<String> choice3 = new Choice<>(choice3Text, choice3Text);

            String choice4Text = context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_4);
            Choice<String> choice4 = new Choice<>(choice4Text, choice4Text);

            String choice5Text = context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_5);
            Choice<String> choice5 = new Choice<>(choice5Text, choice5Text);

            String choice6Text = context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_6);
            Choice<String> choice6 = new Choice<>(choice6Text, choice6Text);

            ChoiceAnswerFormat answerFormat2 = new ChoiceAnswerFormat(
                    AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                    choice1, choice2, choice3, choice4, choice5, choice6);

            QuestionStep questionStep2 = new QuestionStep(TimedWalkFormAssistanceStepIdentifier, null, answerFormat2);
            questionStep2.setText(context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_TITLE));
            questionStep2.setPlaceholder(context.getString(R.string.rsb_TIMED_WALK_QUESTION_2_TEXT));
            questionStep2.setOptional(false);

            String formStepTitle = context.getString(R.string.rsb_TIMED_WALK_FORM_TITLE);
            String formStepText  = context.getString(R.string.rsb_TIMED_WALK_FORM_TEXT);

            List<QuestionStep> questionStepList = Arrays.asList(questionStep1, questionStep2);
            FormStep formStep = new FormStep(TimedWalkFormStepIdentifier, formStepTitle, formStepText, questionStepList);

            stepList.add(formStep);
        }

        String formattedLength = FormatHelper.localizeDistance(context, distanceInMeters, Locale.getDefault());

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            String title = context.getString(R.string.rsb_TIMED_WALK_TITLE);
            String textFormat = context.getString(R.string.rsb_timed_walk_intro_2_text);
            String text = String.format(textFormat, formattedLength);
            InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
            step.setMoreDetailText(context.getString(R.string.rsb_TIMED_WALK_INTRO_2_DETAIL));
            step.setImage(ResUtils.TIMER);
            stepList.add(step);
        }

        {
            CountdownStep step = new CountdownStep(CountdownStepIdentifier);
            step.setStepDuration(DEFAULT_COUNTDOWN_DURATION);
            stepList.add(step);
        }

        // Obtain sensor frequency for Walking Task recorders
        double sensorFreq = context.getResources().getInteger(R.integer.rsb_sensor_frequency_walking_task);

        {
            List<RecorderConfig> recorderConfigList = new ArrayList<>();
            if (!optionList.contains(TaskExcludeOption.PEDOMETER)) {
                recorderConfigList.add(new PedometerRecorderConfig(PedometerRecorderIdentifier));
            }
            if (!optionList.contains(TaskExcludeOption.ACCELEROMETER)) {
                recorderConfigList.add(new AccelerometerRecorderConfig(AccelerometerRecorderIdentifier, sensorFreq));
            }
            if (!optionList.contains(TaskExcludeOption.DEVICE_MOTION)) {
                recorderConfigList.add(new DeviceMotionRecorderConfig(DeviceMotionRecorderIdentifier, sensorFreq));
            }
            if (!optionList.contains(TaskExcludeOption.LOCATION)) {
                recorderConfigList.add(new LocationRecorderConfig(LocationRecorderIdentifier));
            }

            {
                String titleFormat = context.getString(R.string.rsb_timed_walk_instruction);
                String title = String.format(titleFormat, formattedLength);
                String text  = context.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TEXT);
                TimedWalkStep step = new TimedWalkStep(TimedWalkTrial1StepIdentifier, title, text, distanceInMeters);
                step.setSpokenInstruction(title);
                step.setRecorderConfigurationList(recorderConfigList);
                step.setStepDuration(timeLimit == 0 ? Integer.MAX_VALUE : timeLimit);
                step.setImageResName(ResUtils.TimedWalking.MAN_OUTBOUND);
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TURN);
                String text  = context.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TEXT);
                TimedWalkStep step = new TimedWalkStep(TimedWalkTurnAroundStepIdentifier, title, text, 1);
                step.setSpokenInstruction(title);
                step.setRecorderConfigurationList(recorderConfigList);
                step.setStepDuration(turnAroundTimeLimit == 0 ? Integer.MAX_VALUE : turnAroundTimeLimit);
                step.setImageResName(ResUtils.TimedWalking.TURNAROUND);
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_2);
                String text  = context.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TEXT);
                TimedWalkStep step = new TimedWalkStep(TimedWalkTrial2StepIdentifier, title, text, distanceInMeters);
                step.setSpokenInstruction(title);
                step.setRecorderConfigurationList(recorderConfigList);
                step.setStepDuration(timeLimit == 0 ? Integer.MAX_VALUE : timeLimit);
                step.setImageResName(ResUtils.TimedWalking.MAN_RETURN);
                stepList.add(step);
            }
        }

        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }

        return new OrderedTask(identifier, stepList);
    }

    /**
     * @return a step duration value that can be used if number of steps takes too long
     */
    private static int computeFallbackDuration(int numberOfStepsPerLeg) {
        return (int)(numberOfStepsPerLeg * DEFAULT_STEP_DURATION_FALLBACK_FACTOR);
    }
}
