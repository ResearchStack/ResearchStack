package org.researchstack.backbone.ui.step.layout;

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
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveSelfieCaptureStep;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class ActiveSelfieCaptureStepLayout extends ActiveStepLayout {

    private Step step;
    private StepCallbacks callbacks;
    private File outputFile;
    private ImageCapture capture;
    private Camera camera;
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
        this.camera = null;
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

        FloatingActionButton captureButton = (FloatingActionButton)this.findViewById(R.id.selfieCaptureButton);
        captureButton.setOnClickListener(new TakePictureButtonClickListener());

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

        PreviewView previewView = (PreviewView)findViewById(R.id.camera_preview);
        ImageView overlay = (ImageView)findViewById(R.id.camera_preview_overlay);
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        capture = new ImageCapture.Builder().build();
        FaceDetector faceDetector = FaceDetection.getClient();

        final ActiveSelfieCaptureStep.FaceDetectListener faceDetectListener = ((ActiveSelfieCaptureStep)step).getFaceDetectListener();

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(activity);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                provider.unbindAll();

                previewView.getPreviewStreamState().observe(activity, new Observer<PreviewView.StreamState>()
                {
                    @Override
                    public void onChanged(PreviewView.StreamState state) {
                        if (state != PreviewView.StreamState.STREAMING)
                            return;
                        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity), new ImageAnalysis.Analyzer() {
                            @Override
                            @androidx.camera.core.ExperimentalGetImage
                            public void analyze(@NonNull ImageProxy proxy) {
                                final Image faceImage = proxy.getImage();
                                if (faceImage == null) {
                                    proxy.close();
                                    return;
                                }

                                if (faceDetectListener != null) {
                                    InputImage input = InputImage.fromMediaImage(faceImage, proxy.getImageInfo().getRotationDegrees());
                                    faceDetector.process(input).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(@NonNull List<Face> faces) {
                                            try {
                                                if (faces.isEmpty()) {
                                                    activity.runOnUiThread(() -> overlay.setImageBitmap(Bitmap.createBitmap(
                                                            previewView.getBitmap().getWidth(),
                                                            previewView.getBitmap().getHeight(),
                                                            Bitmap.Config.ARGB_8888)));
                                                    return;
                                                }

                                                Bitmap overlayImage = Bitmap.createBitmap(
                                                        previewView.getBitmap().getWidth(),
                                                        previewView.getBitmap().getHeight(),
                                                        Bitmap.Config.ARGB_8888);
                                                Canvas canvas = new Canvas(overlayImage);
                                                Face face = faces.iterator().next();
                                                faceDetectListener.overlayDraw(canvas, face, faceImage);
                                                activity.runOnUiThread(() -> overlay.setImageBitmap(overlayImage));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                proxy.close();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {

                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            proxy.close();
                                        }
                                    });
                                }
                            }
                        });

                    }
                });
                camera = provider.bindToLifecycle(activity, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalysis, capture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    private void setCaptureMode() {
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

    private class TakePictureButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View clickView) {
            FloatingActionButton button = (FloatingActionButton)clickView;
            ContextThemeWrapper wrapper = (ContextThemeWrapper)button.getContext();
            Context context = wrapper.getApplicationContext();
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
    }

    public static class ActiveSelfieCaptureResults implements Serializable {
        public String outputFileName;
    }
}
