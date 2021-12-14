package org.researchstack.backbone.ui.step.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.media.MicrophoneDirection;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveAudioCaptureStep;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActiveAudioCaptureStepLayout extends ActiveStepLayout {

    private static final String BUTTON_RECORD = "Record";
    private static final String BUTTON_STOP = "Stop";
    private static final String BUTTON_RESET = "Reset";

    private Step step;
    private StepCallbacks callbacks;
    private MediaRecorder recorder;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> stopwatchFuture, autoStopFuture;
    private File outputFile;

    private AtomicBoolean isStoppingRecording;

    public ActiveAudioCaptureStepLayout(Context context) {
        super(context);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.active_audio_capture_step;
    }

    @Override
    public void initialize(Step step, StepResult stepResult) {
        super.initialize(step, stepResult);
        this.step = step;
        this.recorder = null;
        this.isStoppingRecording = new AtomicBoolean(false);
        initializeView(stepResult);
    }

    @Override
    public View getLayout() {
        return this;
    }

    public void initializeView(StepResult stepResult) {
        TextView title = (TextView)this.findViewById(R.id.audioCaptureTitle);
        title.setText(this.step.getTitle());

        TextView instructions = (TextView)this.findViewById(R.id.audioCaptureDescription);
        instructions.setText(((ActiveAudioCaptureStep)this.step).getInstructionsText());

        TextView time = (TextView)this.findViewById(R.id.audioCaptureTime);

        FloatingActionButton captureButton = (FloatingActionButton)this.findViewById(R.id.audioCaptureButton);
        captureButton.setOnClickListener(clickView -> {
            ContextThemeWrapper wrapper = (ContextThemeWrapper)clickView.getContext();
            Activity activity = (Activity)wrapper.getBaseContext();
            TextView captureButtonText = (TextView)activity.findViewById(R.id.audioCaptureButtonText);
            if (isRecording(captureButtonText)) {
                captureButtonText.setText(BUTTON_RESET);
                stopRecording();
            } else if (isReadyToRecord(captureButtonText)) {
                if (recorder != null)
                    throw new RuntimeException("MediaRecorder currently in use, cannot reinitialize");
                captureButtonText.setText(BUTTON_STOP);
                startRecording(activity);
            } else if (isReadyToReset(captureButtonText)) {
                resetRecording(activity, captureButtonText);
            }
        });

        TextView captureButtonText = (TextView)this.findViewById(R.id.audioCaptureButtonText);
        if (stepResult == null) {
            time.setText(buildStartTime());
            time.setTextColor(ContextCompat.getColor(getContext(), R.color.rsb_black));
            captureButtonText.setText(BUTTON_RECORD);
        } else {
            time.setText(((AudioCaptureResults)stepResult.getResult()).lastRecordedTime);
            captureButtonText.setText(BUTTON_RESET);
            outputFile = new File(((AudioCaptureResults)stepResult.getResult()).outputFileName);
        }

        SubmitBar submitBar = (SubmitBar)this.findViewById(R.id.rsb_submit_bar);
        submitBar.setPositiveTitle(R.string.rsb_BUTTON_NEXT);
        submitBar.getNegativeActionView().setVisibility(View.INVISIBLE);
        submitBar.setPositiveAction(result -> {
            ViewTaskActivity activity = (ViewTaskActivity)this.getContext();
            if (isReadyToReset(captureButtonText) && outputFile != null && outputFile.exists()) {
                activity.onSaveStep(StepCallbacks.ACTION_NEXT, step, buildStepResult());
                return;
            }
            AlertDialog alert = new AlertDialog.Builder(activity).create();
            alert.setTitle("Error");
            alert.setMessage("Must record audio to continue.");
            alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (dialog, witch) -> dialog.dismiss());
            alert.show();
        });
    }

    private String buildStartTime() {
        int durationSeconds = ((ActiveAudioCaptureStep)step).getStepDuration();
        long current = System.currentTimeMillis();
        long time = current - (current - (durationSeconds * 1000));
        SimpleDateFormat df = new SimpleDateFormat("mm:ss.S");
        return df.format(time);
    }

    private StepResult buildStepResult() {
        try {
            AudioCaptureResults r = new AudioCaptureResults();
            r.outputFileName = outputFile.getAbsolutePath();
            TextView time = (TextView) this.findViewById(R.id.audioCaptureTime);
            r.lastRecordedTime = time.getText().toString();
            StepResult result = new StepResult(step);
            result.setResult(r);
            return result;
        } catch (Exception e) {
            // catch this exception here and return null so the app doesn't crash
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isBackEventConsumed() {
        StepResult result = null;
        TextView captureButtonText = (TextView)this.findViewById(R.id.audioCaptureButtonText);
        if (isReadyToReset(captureButtonText) && outputFile != null && outputFile.exists())
            result = buildStepResult();
        this.callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    private boolean isRecording(TextView buttonText) {
        return BUTTON_STOP.equals(buttonText.getText());
    }

    private boolean isReadyToRecord(TextView buttonText) {
        return BUTTON_RECORD.equals(buttonText.getText());
    }

    private boolean isReadyToReset(TextView buttonText) {
        return BUTTON_RESET.equals(buttonText.getText());
    }

    private void resetRecording(Activity activity, TextView captureButtonText) {
        AlertDialog alert = new AlertDialog.Builder(activity).create();
        alert.setTitle("Reset Audio");
        alert.setMessage("Are you sure?");
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, witch) -> {
            captureButtonText.setText(BUTTON_RECORD);
            TextView time = (TextView)activity.findViewById(R.id.audioCaptureTime);
            time.setText(buildStartTime());
            time.setTextColor(ContextCompat.getColor(getContext(), R.color.rsb_black));
            outputFile.delete();
            dialog.dismiss();
        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, witch) -> dialog.dismiss());
        alert.show();
    }

    private void startRecording(Activity activity) {
        try {
            executorService = Executors.newSingleThreadScheduledExecutor();
            int duration = ((ActiveAudioCaptureStep)step).getStepDuration();
            stopwatchFuture = executorService.scheduleWithFixedDelay(new UpdateTimeTask(activity, duration), 0, 100, TimeUnit.MILLISECONDS);

            File filesDir = activity.getFilesDir();
            filesDir.mkdir();
            outputFile = Paths.get(filesDir.toURI()).resolve(UUID.randomUUID().toString() + ".aac").toFile();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(outputFile);
            recorder.setAudioChannels(1);
            recorder.setAudioSamplingRate(96000); // highest supported for AAC
            recorder.setAudioEncodingBitRate(512000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                recorder.setPreferredMicrophoneDirection(MicrophoneDirection.MIC_DIRECTION_TOWARDS_USER);
            }
            recorder.prepare();
            recorder.start();

            int durationMS = (duration * 1000) + 200; // add some small buffer to the end
            autoStopFuture = executorService.schedule(new AutoStopTask(activity), durationMS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (isStoppingRecording.getAndSet(true))
            return;

        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stopwatchFuture != null) {
                stopwatchFuture.cancel(true);
                stopwatchFuture = null;
            }

            if (autoStopFuture != null) {
                autoStopFuture.cancel(true);
                autoStopFuture = null;
            }

            if (executorService != null) {
                executorService.shutdownNow();
                executorService = null;
            }
        }

        isStoppingRecording.set(false);
    }

    private class UpdateTimeTask implements Runnable {
        private final SimpleDateFormat df;
        private final Activity activity;
        private final long startTime;
        private final int durationMS;

        public UpdateTimeTask(Activity activity, int duration) {
            this.activity = activity;
            this.startTime = System.currentTimeMillis();
            this.durationMS = duration * 1000;
            this.df = new SimpleDateFormat("mm:ss.S");
        }

        @Override
        public void run() {
             activity.runOnUiThread(() -> {
                TextView timeView = (TextView)activity.findViewById(R.id.audioCaptureTime);
                if (timeView == null)
                    return;
                 long remaining = durationMS - (System.currentTimeMillis() - startTime);
                 if (remaining < 0)
                     remaining = 0;
                 if (remaining <= 3000) {
                     timeView.setTextColor(ContextCompat.getColor(getContext(), R.color.rsb_red));
                 }
                 timeView.setText(df.format(remaining));
            });
        }
    }

    private class AutoStopTask implements Runnable {
        private final Activity activity;

        public AutoStopTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            activity.runOnUiThread(() -> {
                TextView captureButtonText = (TextView)activity.findViewById(R.id.audioCaptureButtonText);
                if (captureButtonText != null)
                    captureButtonText.setText(BUTTON_RESET);
            });
            stopRecording();
        }
    }

    public static class AudioCaptureResults implements Serializable {
        public String outputFileName;
        public String lastRecordedTime;
    }
}
