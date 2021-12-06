package org.researchstack.backbone.task.factory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import org.researchstack.backbone.step.active.ActiveSelfieCaptureStep;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.builder.ActiveSelfieCaptureBuilder;

public class ActiveSelfieCaptureFactory {

    public static Task selfieCaptureTask(String identifier,
                                         Context context,
                                         String infoText,
                                         String infoInstructions,
                                         String captureText,
                                         String captureInstructions,
                                         int waitTimeSeconds) {
        return new ActiveSelfieCaptureBuilder()
                .setIdentifier(identifier)
                .setContext(context)
                .setInfoText(infoText, infoInstructions)
                .setCaptureText(captureText, captureInstructions)
                .setWaitTimeSeconds(waitTimeSeconds)
                .setDrawOverlayListener(new DefaultFaceOverlay())
                .build();
    }

    public static Task selfieCaptureTask(String identifier,
                                         Context context,
                                         String infoText,
                                         String infoInstructions,
                                         String captureText,
                                         String captureInstructions,
                                         int waitTimeSeconds,
                                         ActiveSelfieCaptureStep.DrawOverlayListener listener) {
        return new ActiveSelfieCaptureBuilder()
                .setIdentifier(identifier)
                .setContext(context)
                .setInfoText(infoText, infoInstructions)
                .setCaptureText(captureText, captureInstructions)
                .setWaitTimeSeconds(waitTimeSeconds)
                .setDrawOverlayListener(listener)
                .build();
    }

    public static class DefaultFaceOverlay implements ActiveSelfieCaptureStep.DrawOverlayListener {

        @Override
        public void draw(Canvas overlay) {
            float vWidth = overlay.getWidth();
            float vHeight = overlay.getHeight();
            RectF vMaxBounds = getViewportBoundingBox(vWidth, vHeight, new RectF(20f, 10f, 180f, 190f));
            RectF all = new RectF(0, 0, vWidth, vHeight);

            Paint fill = new Paint();
            fill.setStyle(Paint.Style.FILL);
            fill.setColor(Color.BLACK);
            fill.setAlpha(100);

            Path oval = new Path();
            oval.addOval(vMaxBounds, Path.Direction.CW);

            overlay.clipOutPath(oval);
            overlay.drawRect(all, fill);

            Paint stroke = new Paint();
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setColor(Color.WHITE);
            stroke.setStrokeWidth(20);
            overlay.drawPath(oval, stroke);
        }

        @Override
        public boolean isFaceInPosition(RectF overlay, RectF faceImage, Rect face) {
            float vWidth = overlay.width();
            float vHeight = overlay.height();
            float wWidth = faceImage.width();
            float wHeight = faceImage.height();

            // flip the left and right coordinates for the face bounds
            PointF t1 = transform(wWidth, wHeight, vWidth, vHeight, translate(wWidth, wHeight, face, new PointF(face.right, face.top)));
            PointF t2 = transform(wWidth, wHeight, vWidth, vHeight, translate(wWidth, wHeight, face, new PointF(face.left, face.bottom)));
            RectF vFaceBounds = new RectF(t1.x, t1.y, t2.x, t2.y);
            RectF vMinBounds = getViewportBoundingBox(vWidth, vHeight, new RectF(50f, 50f, 150f, 150f));

            return vFaceBounds.contains(vMinBounds);
        }

        private PointF translate(float width, float height, Rect bounds, PointF p) {
            float xBuffer = (width - bounds.width()) / 2f;
            float xDelta = Math.abs(p.x - bounds.left);

            float yBuffer = (height - bounds.height()) / 2f;
            float yDelta = Math.abs(p.y - bounds.top);

            PointF out = new PointF();
            out.x = width - (xBuffer + xDelta);
            out.y = yBuffer + yDelta;
            return out;
        }

        private PointF transform(float wWidth, float wHeight, float vWidth, float vHeight, PointF wPoint) {
            float sX = vWidth / wWidth;
            float sY = vHeight / wHeight;
            PointF out = new PointF();
            out.x = wPoint.x * sX;
            out.y = wPoint.y * sY;
            return out;
        }

        private RectF getViewportBoundingBox(float vWidth, float vHeight, RectF box) {
            float wWidth = 200f;
            float wHeight = 200f;
            float sX = vWidth / wWidth;
            float sY = vHeight / wHeight;
            RectF out = new RectF();
            out.left = box.left * sX;
            out.top = box.top * sY;
            out.right = box.right * sX;
            out.bottom = box.bottom * sY;
            return out;
        }
    }
}
