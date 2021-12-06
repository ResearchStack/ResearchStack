package org.researchstack.backbone.step.active;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;

import com.google.mlkit.vision.face.Face;

import org.researchstack.backbone.ui.step.layout.ActiveSelfieCaptureStepLayout;

import java.io.Serializable;

public class ActiveSelfieCaptureStep extends ActiveStep {

    private String instructionsText;
    private DrawOverlayListener drawOverlayListener;
    private int captureWaitTimeSeconds;

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

    public DrawOverlayListener getDrawOverlayListener() {
        return drawOverlayListener;
    }

    public void setDrawOverlayListener(DrawOverlayListener drawOverlayListener) {
        this.drawOverlayListener = drawOverlayListener;
    }

    public int getCaptureWaitTimeSeconds() {
        return captureWaitTimeSeconds;
    }

    public void setCaptureWaitTimeSeconds(int captureWaitTimeSeconds) {
        this.captureWaitTimeSeconds = captureWaitTimeSeconds;
    }

    public interface DrawOverlayListener extends Serializable {
        void draw(Canvas overlay);
        boolean isFaceInPosition(RectF overlay, RectF faceImage, Rect face);
    }
}
