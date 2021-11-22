package org.researchstack.backbone.ui.step.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveSelfieCaptureStep;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
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
import java.util.concurrent.atomic.AtomicLong;

public class ActiveSelfieCaptureStepLayout extends ActiveStepLayout {

    private Step step;
    private StepCallbacks callbacks;
    private File outputFile;
    private ImageCapture capture;
    private LinearLayout captureLayout, reviewLayout;

    public ActiveSelfieCaptureStepLayout(Context context) {
        super(context);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.active_selfie_capture_step;
    }

    @Override
    public void initialize(Step step, StepResult stepResult) {
        super.initialize(step, stepResult);
        this.step = step;
        initializeView(stepResult);
    }

    @Override
    public View getLayout() {
        return this;
    }

    public void initializeView(StepResult stepResult) {
        ViewTaskActivity activity = (ViewTaskActivity)this.getContext();

        captureLayout = (LinearLayout)this.findViewById(R.id.selfieCaptureLayout);
        reviewLayout = (LinearLayout)this.findViewById(R.id.selfieReviewLayout);

        TextView title = (TextView)this.findViewById(R.id.selfieCaptureTitle);
        title.setText(this.step.getTitle());

        TextView instructions = (TextView)this.findViewById(R.id.selfieCaptureDescription);
        instructions.setText(((ActiveSelfieCaptureStep)this.step).getInstructionsText());

        FloatingActionButton resetButton = (FloatingActionButton)this.findViewById(R.id.selfieResetButton);
        resetButton.setOnClickListener(v -> {
            if (outputFile != null)
                outputFile.delete();
            setCaptureMode();
        });

        FloatingActionButton submitButton = (FloatingActionButton)this.findViewById(R.id.selfieSubmitButton);
        submitButton.setOnClickListener(v -> {
            if (outputFile != null && outputFile.exists())
                activity.onSaveStep(StepCallbacks.ACTION_NEXT, step, buildStepResult());
        });

        if (stepResult != null)
            outputFile = new File(((ActiveSelfieCaptureResults)stepResult.getResult()).outputFileName);

        SubmitBar submitBar = (SubmitBar)this.findViewById(R.id.rsb_submit_bar);
        submitBar.setVisibility(View.GONE);

        initCamera();
    }

    private void initCamera() {
        ViewTaskActivity activity = (ViewTaskActivity)this.getContext();
        int waitTimeSeconds = ((ActiveSelfieCaptureStep)step).getCaptureWaitTimeSeconds();
        CountdownManager countdownManager = new CountdownManager(activity, new CountdownManagerListener() {
            @Override
            public void onStart(Activity activity) {
                String start = "" + waitTimeSeconds;
                TextView timeView = activity.findViewById(R.id.selfieCaptureTime);
                timeView.setText(start);
                timeView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStop(Activity activity) {
                TextView timeView = activity.findViewById(R.id.selfieCaptureTime);
                timeView.setVisibility(View.GONE);
            }

            @Override
            public void onShutdown(Activity activity) {
                TextView timeView = activity.findViewById(R.id.selfieCaptureTime);
                timeView.setVisibility(View.GONE);
                takePicture(activity);
            }
        }, waitTimeSeconds);
        PreviewView previewView = (PreviewView)findViewById(R.id.camera_preview);
        ImageView overlay = (ImageView)findViewById(R.id.camera_preview_overlay);
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        capture = new ImageCapture.Builder().build();
        FaceDetector faceDetector = FaceDetection.getClient(new FaceDetectorOptions.Builder()
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build());

        final ActiveSelfieCaptureStep.DrawOverlayListener drawOverlayListener = ((ActiveSelfieCaptureStep)step).getDrawOverlayListener();
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(activity);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                previewView.getPreviewStreamState().observe(activity, new Observer<PreviewView.StreamState>()
                {
                    @Override
                    @androidx.camera.core.ExperimentalGetImage
                    public void onChanged(PreviewView.StreamState state) {
                        if (state != PreviewView.StreamState.STREAMING)
                            return;

                        if (drawOverlayListener != null) {
                            Bitmap overlayImage = Bitmap.createBitmap(
                                    previewView.getBitmap().getWidth(),
                                    previewView.getBitmap().getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(overlayImage);
                            drawOverlayListener.draw(canvas);
                            activity.runOnUiThread(() -> overlay.setImageBitmap(overlayImage));
                        }

                        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity), proxy -> {
                             if (countdownManager.isShutdown())
                                return;

                            final Image faceImage = proxy.getImage();
                            if (faceImage == null) {
                                proxy.close();
                                return;
                            }

                            InputImage input = InputImage.fromMediaImage(faceImage, proxy.getImageInfo().getRotationDegrees());
                            faceDetector.process(input).addOnSuccessListener(faces -> {
                                try {
                                    if (faces.isEmpty()) {
                                        if (countdownManager.isCountdownRunning())
                                            countdownManager.stop();
                                        return;
                                    }

                                    if (countdownManager.isCountdownComplete()) {
                                        countdownManager.shutdown();
                                        return;
                                    }

                                    if (!countdownManager.isCountdownRunning())
                                        countdownManager.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    proxy.close();
                                }
                            }).addOnFailureListener(e -> {
                                e.printStackTrace();
                                proxy.close();
                            });
                        });
                    }
                });
                provider.unbindAll();
                provider.bindToLifecycle(activity, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalysis, capture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    private void shutdownCamera() {
        try {
            ProcessCameraProvider.getInstance(getContext()).get().unbindAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCaptureMode() {
        initCamera();

        captureLayout.setVisibility(View.VISIBLE);
        reviewLayout.setVisibility(View.GONE);
    }

    private void setReviewMode() {
        captureLayout.setVisibility(View.GONE);
        reviewLayout.setVisibility(View.VISIBLE);

        if (outputFile != null) {
            ImageView thumbnail = (ImageView) this.findViewById(R.id.selfieDisplayThumbnail);
            thumbnail.setImageURI(Uri.fromFile(outputFile));
        }

        shutdownCamera();
    }

    private StepResult buildStepResult() {
        try {
            ActiveSelfieCaptureResults r = new ActiveSelfieCaptureResults();
            r.outputFileName = outputFile.getAbsolutePath();
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
        if (outputFile != null && outputFile.exists())
            result = buildStepResult();
        this.callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    private void takePicture(Context context) {
        try {
            File filesDir = context.getFilesDir();
            filesDir.mkdir();
            outputFile = Paths.get(filesDir.toURI()).resolve(UUID.randomUUID().toString() + ".jpg").toFile();
            ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(outputFile).build();
            capture.takePicture(options, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults results) {
                    setReviewMode();
                }

                @Override
                public void onError(@NonNull ImageCaptureException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ActiveSelfieCaptureResults implements Serializable {
        public String outputFileName;
    }

    private interface CountdownManagerListener {
        void onStart(Activity activity);
        void onStop(Activity activity);
        void onShutdown(Activity activity);
    }

    private static class CountdownManager {
        private ScheduledFuture<?> future;
        private AtomicLong countdownTime;
        private ScheduledExecutorService executor;
        private boolean isShutdown;

        private final Activity activity;
        private final CountdownManagerListener listener;
        private final int waitTimeMS;

        public CountdownManager(Activity activity, CountdownManagerListener listener, int waitTimeSeconds) {
            this.activity = activity;
            this.listener = listener;
            this.waitTimeMS = waitTimeSeconds * 1000;
            isShutdown = false;
        }

        public boolean isCountdownRunning() {
            return future != null;
        }

        public boolean isCountdownComplete() {
            return (countdownTime != null && countdownTime.get() == 0);
        }

        public boolean isShutdown() {
            return isShutdown;
        }

        public void start() {
            if (isShutdown)
                return;

            if (executor == null) {
                executor = Executors.newSingleThreadScheduledExecutor();
            }

            if (future == null) {
                countdownTime = new AtomicLong(waitTimeMS);
                final long startTime = System.currentTimeMillis();
                final SimpleDateFormat df = new SimpleDateFormat("s");
                future = executor.scheduleWithFixedDelay(() -> {
                    activity.runOnUiThread(() -> {
                        if (countdownTime == null || countdownTime.get() == 0)
                            return;
                        TextView timeView = (TextView)activity.findViewById(R.id.selfieCaptureTime);
                        if (timeView == null)
                            return;
                        long remainingMS = waitTimeMS - (System.currentTimeMillis() - startTime);
                        if (remainingMS < 0) {
                            remainingMS = 0;
                            timeView.setText("Please hold");
                        } else {
                            long displayMS = remainingMS + 1000;
                            timeView.setText(df.format(displayMS));
                        }
                        countdownTime.set(remainingMS);
                    });
                }, 0, 100, TimeUnit.MILLISECONDS);
            }
            listener.onStart(activity);
        }

        public void stop() {
            if (isShutdown)
                return;

            if (future != null) {
                future.cancel(true);
                future = null;
                countdownTime = null;
            }
            listener.onStop(activity);
        }

        public void shutdown() {
            if (isShutdown)
                return;

            isShutdown = true;

            if (future != null) {
                future.cancel(true);
                future = null;
                countdownTime = null;
            }

            if (executor != null) {
                executor.shutdownNow();
                executor = null;
            }
            listener.onShutdown(activity);
        }
    }
}
