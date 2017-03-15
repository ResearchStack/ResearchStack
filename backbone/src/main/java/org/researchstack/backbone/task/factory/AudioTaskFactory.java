package org.researchstack.backbone.task.factory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.AudioTooLoudStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.AudioStep;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorderConfig;
import org.researchstack.backbone.step.active.recorder.AudioRecorderSettings;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;

/**
 * Created by TheMDP on 2/26/17.
 */

public class AudioTaskFactory {

    /**
     * Threshold can be anywhere from 0.0 - 1.0
     * This will determine if background noise is too loud and direct the user to
     * try again in a quieter environment
     */
    public static final double LOUDNESS_THRESHOLD = 0.45;

    public static final String AudioStepIdentifier                  = "audio";
    public static final String AudioTooLoudStepIdentifier           = "audio.tooloud";
    public static final String MicrophonePermissionsStepIdentifier  = "microphonepermission";

    /**
     * Returns a predefined task that enables an audio recording possibly with a check of the audio level.
     *
     * In an audio recording task, the participant is asked to make some kind of sound
     * with their voice, and the audio data is collected.
     *
     * An audio task can be used to measure properties of the user's voice, such as
     * frequency range, or the ability to pronounce certain sounds.
     *
     * If `checkAudioLevel == true` then a navigation rule is added to do a simple check of the background
     * noise level. If the background noise is too loud, then the participant is instructed to move to a
     * quieter location before trying again.
     *
     * Data collected in this task consists of audio information.
     *
     * @param context                 Can be app or activity, used for string and other resources
     * @param identifier              The task identifier to use for this task, appropriate to the study.
     * @param intendedUseDescription  A localized string describing the intended use of the data
     *                                collected. If the value of this parameter is `null`, default
     *                                localized text is used.
     * @param speechInstruction       Instructional content describing what the user needs to do when
     *                                recording begins. If the value of this parameter is `null`,
     *                                default localized text is used.
     * @param shortSpeechInstruction  Instructional content shown during audio recording. If the value of
     *                                this parameter is `null`, default localized text is used.
     * @param duration                The length of the count down timer that runs while audio data is
     *                                collected.
     * @param recordingSettings       See class for possible values, all based on MediaRecorder class
     * @param checkAudioLevel         If `true` then add navigational rules to check the background noise level.
     * @param optionList              Hand that affect the features of the predefined task.
     *
     * @return An active audio task that can be presented with an `ORKTaskViewController` object.
     */
    public static NavigableOrderedTask audioTask(
            Context context,
            String  identifier,
            String  intendedUseDescription,
            String  speechInstruction,
            String  shortSpeechInstruction,
            int     duration,
            AudioRecorderSettings recordingSettings,
            boolean checkAudioLevel,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        if (recordingSettings == null) {
            recordingSettings = AudioRecorderSettings.defaultSettings();
        }

        if (optionList.contains(TaskExcludeOption.AUDIO)) {
            throw new IllegalStateException("Audio collection cannot be excluded from audio task");
        }

        // In-app permissions were added in Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // This isn't in iOS, but in Android we need to check for this so that microphone permission is granted
            PackageManager pm = context.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.RECORD_AUDIO, context.getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                // include a permission request step that requires microphone
                stepList.add(new PermissionsStep(MicrophonePermissionsStepIdentifier, null, null));
            }
        }

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {
            {
                String title = context.getString(R.string.rsb_AUDIO_TASK_TITLE);
                if (intendedUseDescription == null) {
                    intendedUseDescription = context.getString(R.string.rsb_AUDIO_INTENDED_USE);
                }
                InstructionStep step = new InstructionStep(Instruction0StepIdentifier, title, intendedUseDescription);
                step.setImage(ResUtils.Audio.PHONE_WAVES);
                stepList.add(step);
            }

            {
                String title = context.getString(R.string.rsb_AUDIO_TASK_TITLE);
                String text = speechInstruction;
                if (text == null) {
                    text = context.getString(R.string.rsb_AUDIO_INTRO_TEXT);
                }
                InstructionStep step = new InstructionStep(Instruction1StepIdentifier, title, text);
                step.setMoreDetailText(context.getString(R.string.rsb_AUDIO_CALL_TO_ACTION));
                step.setImage(ResUtils.Audio.PHONE_SOUND_WAVES);
                stepList.add(step);
            }
        }

        {
            CountdownStep step = new CountdownStep(CountdownStepIdentifier);

            //  Collect audio during the countdown step too, to provide a "too loud" baseline
            step.setRecorderConfigurationList(Collections.singletonList(new AudioRecorderConfig(
                    AudioRecorderSettings.defaultSettings(), AudioRecorderIdentifier)));

            // If checking the sound level then add text indicating that's what is happening
            if (checkAudioLevel) {
                step.setText(context.getString(R.string.rsb_AUDIO_LEVEL_CHECK_LABEL));
            }

            stepList.add(step);
        }

        if (checkAudioLevel) {
            String text = context.getString(R.string.rsb_AUDIO_TOO_LOUD_MESSAGE);
            AudioTooLoudStep step = new AudioTooLoudStep(AudioTooLoudStepIdentifier, null, text);
            step.setMoreDetailText(context.getString(R.string.rsb_AUDIO_TOO_LOUD_ACTION_NEXT));

            // Configure the step's Navigation Rules so that the NavigableOrderedTask
            // can correctly direct the user based on the results of the audio recording
            step.setLoudnessThreshold(LOUDNESS_THRESHOLD);
            step.setAudioStepResultIdentifier(CountdownStepIdentifier);
            step.setNextStepIdentifier(CountdownStepIdentifier);

            stepList.add(step);
        }

        {
            AudioStep step = new AudioStep(AudioStepIdentifier, null, null);
            if (shortSpeechInstruction == null) {
                step.setTitle(context.getString(R.string.rsb_AUDIO_INSTRUCTION));
            } else {
                step.setTitle(shortSpeechInstruction);
            }
            step.setRecorderConfigurationList(Collections.singletonList(new AudioRecorderConfig(
                    recordingSettings, AudioRecorderIdentifier)));
            step.setStepDuration(duration);
            step.setShouldContinueOnFinish(true);

            stepList.add(step);
        }

        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }

        return new NavigableOrderedTask(identifier, stepList);
    }
}
