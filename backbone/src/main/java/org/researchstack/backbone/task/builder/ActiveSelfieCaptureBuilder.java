package org.researchstack.backbone.task.builder;

import android.Manifest;
import android.content.Context;

import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.factory.TaskFactory;
import org.researchstack.backbone.step.active.ActiveSelfieCaptureStep;

import java.util.ArrayList;
import java.util.List;

public class ActiveSelfieCaptureBuilder {
    public static final String SELFIE_RESULTS_STEP_ID = "selfieStep";

    private Context context;
    private String identifier, infoTitle, infoInstructions, captureTitle, captureInstructions;
    private ActiveSelfieCaptureStep.DrawOverlayListener drawOverlayListener;
    private int waitTimeSeconds;

    public ActiveSelfieCaptureBuilder setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ActiveSelfieCaptureBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public ActiveSelfieCaptureBuilder setInfoText(String infoTitle, String infoInstructions) {
        this.infoTitle = infoTitle;
        this.infoInstructions = infoInstructions;
        return this;
    }

    public ActiveSelfieCaptureBuilder setCaptureText(String captureTitle, String captureInstructions) {
        this.captureTitle = captureTitle;
        this.captureInstructions = captureInstructions;
        return this;
    }

    public ActiveSelfieCaptureBuilder setDrawOverlayListener(ActiveSelfieCaptureStep.DrawOverlayListener drawOverlayListener) {
        this.drawOverlayListener = drawOverlayListener;
        return this;
    }

    public ActiveSelfieCaptureBuilder setWaitTimeSeconds(int waitTimeSeconds) {
        this.waitTimeSeconds = waitTimeSeconds;
        return this;
    }

    public Task build() {
        List<Step> steps = new ArrayList<>();

        List<PermissionRequestManager.PermissionRequest> newRequests = new ArrayList<>();
        if (!PermissionRequestManager.getInstance().hasPermission(context, Manifest.permission.CAMERA)) {
            PermissionRequestManager.PermissionRequest request = new PermissionRequestManager.PermissionRequest(
                    Manifest.permission.CAMERA,
                    R.drawable.ic_baseline_photo_camera_24,
                    R.string.rsb_permission_camera_name,
                    R.string.rsb_permission_camera_description);
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

        InstructionStep step2 = new InstructionStep("intro",
                infoTitle,
                infoInstructions);
        step2.setStepTitle(R.string.rsb_active_selfie_capture_test_title);
        step2.setImage("rsb_tremor_in_hand");
        steps.add(step2);

        ActiveSelfieCaptureStep step3 = new ActiveSelfieCaptureStep(
                SELFIE_RESULTS_STEP_ID,
                captureTitle,
                null);
        step3.setInstructionsText(captureInstructions);
        step3.setCaptureWaitTimeSeconds(waitTimeSeconds);
        if (drawOverlayListener != null)
            step3.setDrawOverlayListener(drawOverlayListener);
        steps.add(step3);

        steps.add(TaskFactory.makeCompletionStep(context));

        OrderedTask task = new OrderedTask(identifier, steps);
        return task;
    }
}
