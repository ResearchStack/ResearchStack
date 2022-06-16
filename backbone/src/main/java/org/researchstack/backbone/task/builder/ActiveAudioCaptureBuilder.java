package org.researchstack.backbone.task.builder;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.Instruction1StepIdentifier;

import android.Manifest;
import android.content.Context;

import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.factory.TaskFactory;
import org.researchstack.backbone.step.active.ActiveAudioCaptureStep;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;

public class ActiveAudioCaptureBuilder {
    public static final String AUDIO_RESULTS_STEP_ID = "audioStep";

    private Context context;
    private String identifier, infoTitle, infoInstructions, captureTitle, captureInstructions;
    private int durationSeconds;

    public ActiveAudioCaptureBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public ActiveAudioCaptureBuilder setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ActiveAudioCaptureBuilder setInfoText(String infoTitle, String infoInstructions) {
        this.infoTitle = infoTitle;
        this.infoInstructions = infoInstructions;
        return this;
    }

    public ActiveAudioCaptureBuilder setCaptureText(String captureTitle, String captureInstructions) {
        this.captureTitle = captureTitle;
        this.captureInstructions = captureInstructions;
        return this;
    }

    public ActiveAudioCaptureBuilder setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
        return this;
    }

    public Task build() {
        List<Step> steps = new ArrayList<>();

        List<PermissionRequestManager.PermissionRequest> newRequests = new ArrayList<>();
        if (!PermissionRequestManager.getInstance().hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
            PermissionRequestManager.PermissionRequest request = new PermissionRequestManager.PermissionRequest(
                    Manifest.permission.RECORD_AUDIO,
                    R.drawable.ic_baseline_mic_24,
                    R.string.rsb_permission_record_audio_name,
                    R.string.rsb_permission_record_audio_description);
            request.setIsBlockingPermission(true);
            request.setIsSystemPermission(true);
            newRequests.add(request);
        }

//        if (!PermissionRequestManager.getInstance().hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            PermissionRequestManager.PermissionRequest request = new PermissionRequestManager.PermissionRequest(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    R.drawable.ic_baseline_storage_24,
//                    R.string.rsb_permission_write_external_storage_name,
//                    R.string.rsb_permission_write_external_storage_description);
//            request.setIsBlockingPermission(true);
//            request.setIsSystemPermission(true);
//            newRequests.add(request);
//        }

        if (!newRequests.isEmpty()) {
            PermissionRequestManager.getInstance().setPermissionRequests(newRequests);
            PermissionsStep step1 = new PermissionsStep("permissionsStep",
                    context.getString(R.string.rsb_permission_permission_check_title),
                    context.getString(R.string.rsb_permission_permission_check_text));
            steps.add(step1);
        }

        String step2Title = (infoTitle == null) ? context.getString(R.string.rsb_AUDIO_TASK_TITLE) : infoTitle;
        InstructionStep step2 = new InstructionStep(Instruction1StepIdentifier, step2Title, infoInstructions);
        step2.setMoreDetailText(context.getString(R.string.rsb_AUDIO_CALL_TO_ACTION));
        step2.setImage(ResUtils.Audio.PHONE_SOUND_WAVES);
        steps.add(step2);

//        AudioRecorderSettings recordingSettings = AudioRecorderSettings.defaultSettings();

//        CountdownStep step3 = new CountdownStep(CountdownStepIdentifier);
//        step3.setRecorderConfigurationList(Collections.singletonList(new AudioRecorderConfig(
//                AudioRecorderSettings.defaultSettings(), AudioRecorderIdentifier)));
//        step3.setText(this.getString(R.string.rsb_AUDIO_LEVEL_CHECK_LABEL));
//        steps.add(step3);
//
//        String tooLoudMessage = this.getString(R.string.rsb_AUDIO_TOO_LOUD_MESSAGE);
//        AudioTooLoudStep step5 = new AudioTooLoudStep("tooLoudStep", null, tooLoudMessage);
//        step5.setMoreDetailText(this.getString(R.string.rsb_AUDIO_TOO_LOUD_ACTION_NEXT));
//        step5.setLoudnessThreshold(0.45);
//        step5.setAudioStepResultIdentifier(CountdownStepIdentifier);
//        step5.setNextStepIdentifier(Instruction1StepIdentifier);
//        steps.add(step5);
//
//        AudioStep step6 = new AudioStep("recordAudioStep", null, null);
//        step6.setTitle("Speak for 10 seconds.");
//        step6.setRecorderConfigurationList(Collections.singletonList(new AudioRecorderConfig(
//                recordingSettings, AudioRecorderIdentifier)));
//        step6.setStepDuration(10);
//        step6.setShouldContinueOnFinish(true);
//        steps.add(step6);

        ActiveAudioCaptureStep step3 = new ActiveAudioCaptureStep(
                AUDIO_RESULTS_STEP_ID,
                captureTitle,
                null,
                durationSeconds);
        step3.setInstructionsText(captureInstructions);
        steps.add(step3);

        steps.add(TaskFactory.makeCompletionStep(context));

        OrderedTask task = new NavigableOrderedTask(identifier, steps);
        return task;
    }
}
