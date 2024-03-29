package org.researchstack.backbone.ui.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

/**
 * Created by mdephillips on 4/20/15
 *
 * ArcDrawable can be set as the background of any view to show or animate an arc
 */

public class ArcDrawable extends Drawable {

    private static final int DEFAULT_STROKE_COLOR = Color.GREEN;
    private static final float DEFAULT_STROKE_WIDTH = 10.0f; // 10 px wide
    public static final float FULL_SWEEPING_ANGLE = 360.0f; // full circle
    private static final float DEFAULT_START_ANGLE = -90.0f; // 12 o'clock

    private final Paint mPaint;
    private float mSweepingAngle;
    public void setSweepAngle(float degrees) {
        mSweepingAngle = degrees;
        invalidateSelf();
    }
    private float mStartAngle;
    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
    }

    private Path.Direction direction = Path.Direction.CCW;
    public void setDirection(Path.Direction newDirection) {
        direction = newDirection;
    }

    private static final int DEFAULT_FULL_CIRCLE_COLOR = Color.GRAY;
    private static final float DEFAULT_FULL_CIRCLE_STROKE_PERCENTAGE = 0.25f;
    /**
     * The full circle preview is a ring that shows behind the arc as an indication
     * Of how and where the arc will follow
     */
    private Paint mFullCirclePreviewPaint;
    private boolean mIncludeFullCirclePreview;
    public void setIncludeFullCirclePreview(boolean mIncludeFullCirclePreview) {
        this.mIncludeFullCirclePreview = mIncludeFullCirclePreview;
    }
    private int mFullCirclePreviewColor = DEFAULT_FULL_CIRCLE_COLOR;
    public void setFullCirclePreviewColor(int mFullCirclePreviewColor) {
        this.mFullCirclePreviewColor = mFullCirclePreviewColor;
    }

    public ArcDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        mPaint.setColor(DEFAULT_STROKE_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mFullCirclePreviewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFullCirclePreviewPaint.setStyle(Paint.Style.STROKE);
        mSweepingAngle = FULL_SWEEPING_ANGLE;
        mStartAngle = DEFAULT_START_ANGLE;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        float halfStrokeWidth = mPaint.getStrokeWidth() * 0.5f;
        RectF rect = new RectF(
                halfStrokeWidth,
                halfStrokeWidth,
                canvas.getWidth() - halfStrokeWidth,
                canvas.getHeight() - halfStrokeWidth);
        float angle = (direction == Path.Direction.CCW) ? -mSweepingAngle : mSweepingAngle;

        // Draw the preview first, if applicable, so it is under the main arc
        if (mIncludeFullCirclePreview) {
            mFullCirclePreviewPaint.setColor(mFullCirclePreviewColor);
            float fullPreviewStroke = DEFAULT_FULL_CIRCLE_STROKE_PERCENTAGE * mPaint.getStrokeWidth();
            mFullCirclePreviewPaint.setStrokeWidth(fullPreviewStroke);
            RectF fullCircleRect = new RectF(halfStrokeWidth, halfStrokeWidth,
                    canvas.getWidth() - halfStrokeWidth,
                    canvas.getHeight() - halfStrokeWidth);
            canvas.drawArc(fullCircleRect, mStartAngle, FULL_SWEEPING_ANGLE, false, mFullCirclePreviewPaint);
        }

        // Draw the arc over top the preview
        canvas.drawArc(rect, mStartAngle, angle, false, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setArchWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // not supported at this time, use setColor(int color) method
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}