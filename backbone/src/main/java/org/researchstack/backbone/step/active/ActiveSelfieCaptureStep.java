package org.researchstack.backbone.step.active;

import android.graphics.Canvas;
import android.media.Image;

import com.google.mlkit.vision.face.Face;

import org.researchstack.backbone.ui.step.layout.ActiveSelfieCaptureStepLayout;

import java.io.Serializable;

public class ActiveSelfieCaptureStep extends ActiveStep {

    private String instructionsText;
    private FaceDetectListener faceDetectListener;

    public ActiveSelfieCaptureStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    public Class getStepLayoutClass() {
        return ActiveSelfieCaptureStepLayout.class;
    }

    public void setInstructionsText(String instructionsText) {
        this.instructionsText = instructionsText;
    }

    public String getInstructionsText() {
        return this.instructionsText;
    }

    public FaceDetectListener getFaceDetectListener() {
        return faceDetectListener;
    }

    public void setFaceDetectListener(FaceDetectListener faceDetectListener) {
        this.faceDetectListener = faceDetectListener;
    }

    public interface FaceDetectListener extends Serializable {
        void overlayDraw(Canvas overlay, Face face, Image faceImage);
    }
}
