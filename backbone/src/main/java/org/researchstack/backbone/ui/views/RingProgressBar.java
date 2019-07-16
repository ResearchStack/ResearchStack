package org.researchstack.backbone.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.R;


/**
 * Created by hcc on 16/7/13 19:54
 * 100332338@qq.com
 * <p/>
 */
public class RingProgressBar extends View {

    private Paint paint;

    private int width;

    private int height;

    private int result = 0;

    private float padding = 0;

    private float dashGap = 0;

    private int ringColor;

    private int ringProgressColor;

    private int textColor;

    private float textSize;

    private float ringWidth;

    private int max;

    private int progress;

    private boolean textIsShow;

    private int style;

    public static final int STROKE = 0;

    public static final int FILL = 1;

    private OnProgressListener mOnProgressListener;

    private int centre;

    private int radius;


    public RingProgressBar(Context context) {

        this(context, null);
    }


    public RingProgressBar(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }


    public RingProgressBar(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        paint = new Paint();

        result = dp2px(100);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar);

        ringColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringColor, Color.BLACK);
        ringProgressColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringProgressColor,
                Color.WHITE);
        textColor = mTypedArray.getColor(R.styleable.RingProgressBar_textColor, Color.BLACK);
        textSize = mTypedArray.getDimension(R.styleable.RingProgressBar_textSize, 16);
        ringWidth = mTypedArray.getDimension(R.styleable.RingProgressBar_ringWidth, 4.5f);
        max = mTypedArray.getInteger(R.styleable.RingProgressBar_max, 100);
        textIsShow = mTypedArray.getBoolean(R.styleable.RingProgressBar_textIsShow, true);
        style = mTypedArray.getInt(R.styleable.RingProgressBar_style, 0);
        progress = mTypedArray.getInteger(R.styleable.RingProgressBar_progress, 0);
        padding = mTypedArray.getDimension(R.styleable.RingProgressBar_ringPadding, 10);

        mTypedArray.recycle();
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        centre = getWidth() / 2;
        radius = (int) (centre - ringWidth / 2);

        drawCircle(canvas);
        drawTextContent(canvas);
        drawProgress(canvas);
    }


    private void drawCircle(Canvas canvas) {
        paint.setColor(ringColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ringWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(centre, centre, radius, paint);
    }

    private void drawTextContent(Canvas canvas) {
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT);
        int percent = (int) (((float) progress / (float) max) * 100);
        float textWidth = paint.measureText(percent + "%");
        if (textIsShow && percent != 0 && style == STROKE) {
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize / 2, paint);
        }
    }


    private void drawProgress(Canvas canvas) {
        paint.setStrokeWidth(ringWidth);
        paint.setColor(ringProgressColor);

        RectF strokeOval = new RectF(centre - radius, centre - radius, centre + radius,
                centre + radius);
        RectF fillOval = new RectF(centre - radius + ringWidth + padding,
                centre - radius + ringWidth + padding, centre + radius - ringWidth - padding,
                centre + radius - ringWidth - padding);

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                canvas.drawArc(strokeOval, -90, 360 * progress / max, false, paint);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                if (progress != 0) {
                    canvas.drawArc(fillOval, -90, 360 * progress / max, true, paint);
                }
                break;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {
            width = result;
        } else {
            width = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = result;
        } else {
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }


    public synchronized int getMax() {

        return max;
    }


    public synchronized void setMax(int max) {

        if (max < 0) {
            throw new IllegalArgumentException("The max progress of 0");
        }
        this.max = max;
    }


    public synchronized int getProgress() {

        return progress;
    }


    public synchronized void setProgress(int progress) {

        if (progress < 0) {
            throw new IllegalArgumentException("The progress of 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
        if (progress == max) {
            if (mOnProgressListener != null) {
                mOnProgressListener.progressToComplete();
            }
        }
    }


    public int getRingColor() {

        return ringColor;
    }


    public void setRingColor(int ringColor) {

        this.ringColor = ringColor;
    }


    public int getRingProgressColor() {

        return ringProgressColor;
    }


    public void setRingProgressColor(int ringProgressColor) {

        this.ringProgressColor = ringProgressColor;
    }

    public int getTextColor() {

        return textColor;
    }

    public void setTextColor(int textColor) {

        this.textColor = textColor;
    }


    public float getTextSize() {

        return textSize;
    }


    public void setTextSize(float textSize) {

        this.textSize = textSize;
    }


    public float getRingWidth() {

        return ringWidth;
    }


    public void setRingWidth(float ringWidth) {

        this.ringWidth = ringWidth;
    }


    public int dp2px(int dp) {

        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }


    public interface OnProgressListener {

        void progressToComplete();
    }


    public void setOnProgressListener(OnProgressListener mOnProgressListener) {

        this.mOnProgressListener = mOnProgressListener;
    }
}
