package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.shapes.PathShape;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.callbacks.SignatureCallbacks;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: For save-state to work, the view MUST have an ID
 */
public class SignatureView extends View {

    private static final boolean DEBUG = false;
    private static final String TAG = SignatureView.class.getSimpleName();

    private SignatureCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Paint
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private List<LinePathPoint> sigPoints = new ArrayList<>();

    private Path sigPath = new Path();
    private Paint sigPaint = new Paint();
    private Paint hintPaint = new Paint();
    private Rect drawBounds = new Rect();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Properties
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private String hintText;
    private int guidelineMargin;
    private int guidelineHeight;
    private int hintTextColor;
    private int guidelineColor;
    private int sigPrintColor;

    public SignatureView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }


    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0);
    }

    /**
     * Init all paint objects
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SignatureView,
                defStyleAttr,
                R.style.Widget_Backbone_SignatureView);

        int signatureColor = a.getColor(R.styleable.SignatureView_signatureColor, Color.BLACK);

        sigPrintColor = a.getColor(R.styleable.SignatureView_signaturePrintColor, Color.BLACK);

        int defSignatureStroke = (int) (getResources().getDisplayMetrics().density * 1);

        int signatureStroke = a.getDimensionPixelSize(R.styleable.SignatureView_signatureStrokeSize,
                defSignatureStroke);

        hintText = a.getString(R.styleable.SignatureView_hintText);

        hintTextColor = a.getColor(R.styleable.SignatureView_hintTextColor, Color.LTGRAY);

        int defHintTextSize = (int) (getResources().getDisplayMetrics().density * 14);
        int hintTextSize = a.getDimensionPixelSize(R.styleable.SignatureView_hintTextSize,
                defHintTextSize);

        guidelineColor = a.getColor(R.styleable.SignatureView_guidelineColor, hintTextColor);

        int defGuidelineMargin = (int) (getResources().getDisplayMetrics().density * 12);
        guidelineMargin = a.getDimensionPixelSize(R.styleable.SignatureView_guidelineMargin,
                defGuidelineMargin);

        int defGuidelineHeight = (int) (getResources().getDisplayMetrics().density * 1);
        guidelineHeight = a.getDimensionPixelSize(R.styleable.SignatureView_guidelineHeight,
                defGuidelineHeight);

        a.recycle();

        sigPaint.setAntiAlias(true);
        sigPaint.setColor(signatureColor);
        sigPaint.setStyle(Paint.Style.STROKE);
        sigPaint.setStrokeJoin(Paint.Join.ROUND);
        sigPaint.setStrokeCap(Paint.Cap.ROUND);
        sigPaint.setPathEffect(new CornerPathEffect(20));
        sigPaint.setStrokeWidth(signatureStroke);

        hintPaint.setAntiAlias(true);
        hintPaint.setColor(hintTextColor);
        hintPaint.setStyle(Paint.Style.FILL);
        hintPaint.setTextSize(hintTextSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sigPoints.add(new LinePathPoint(x, y, LinePathPoint.TYPE_LINE_START));

                if (sigPath.isEmpty()) {
                    callbacks.onSignatureStarted();
                }

                sigPath.moveTo(x, y);

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:

                int hSize = event.getHistorySize();

                for (int i = 0; i < hSize; i++) {
                    int hX = (int) event.getHistoricalX(i);
                    int hY = (int) event.getHistoricalY(i);
                    sigPath.lineTo(hX, hY);
                    sigPoints.add(new LinePathPoint(x, y, LinePathPoint.TYPE_LINE_POINT));
                }

                sigPath.lineTo(x, y);
                sigPoints.add(new LinePathPoint(x, y, LinePathPoint.TYPE_LINE_POINT));

                invalidate();
                break;
        }

        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawBounds.left = getPaddingLeft();
        drawBounds.top = getPaddingTop();
        drawBounds.right = w - getPaddingRight();
        drawBounds.bottom = h - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // Draw Guide
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        hintPaint.setColor(guidelineColor);
        canvas.drawRect(drawBounds.left,
                drawBounds.bottom - guidelineHeight,
                drawBounds.right,
                drawBounds.bottom,
                hintPaint);

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // Draw signature or hint text
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        if (sigPath.isEmpty()) {
            hintPaint.setColor(hintTextColor);
            int baselineY = drawBounds.bottom - guidelineMargin - guidelineHeight;
            canvas.drawText(hintText, drawBounds.left, baselineY, hintPaint);
        } else {
            canvas.drawPath(sigPath, sigPaint);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SignatureSavedState ss = new SignatureSavedState(superState);
        ss.points = sigPoints;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SignatureSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SignatureSavedState ss = (SignatureSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        sigPoints = ss.points;
        sigPath.rewind();

        for (LinePathPoint point : sigPoints) {
            if (point.isStartPoint()) {
                sigPath.moveTo(point.x, point.y);
            } else {
                sigPath.lineTo(point.x, point.y);
            }
        }
    }

    public void clearSignature() {
        sigPath.rewind();
        sigPoints.clear();

        ViewCompat.postInvalidateOnAnimation(this);

        if (callbacks != null) {
            callbacks.onSignatureCleared();
        }
    }

    public boolean isSignatureDrawn() {
        return !sigPath.isEmpty();
    }

    public void setCallbacks(SignatureCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * 1. Iterating over the path points, saving them in an array ... not bueno. Try to use
     * {@link android.graphics.PathMeasure} class and get minX and minY from that.
     * <p>
     * 2. Scale bitmap down. Currently drawing at density of device.
     */
    public Bitmap createSignatureBitmap() {

        Paint bitmapSigPaint = new Paint(sigPaint);
        bitmapSigPaint.setColor(sigPrintColor);

        RectF sigBounds = new RectF();
        sigPath.computeBounds(sigBounds, true);

        if (sigBounds.width() != 0 && sigBounds.height() != 0) {
            float density = getResources().getDisplayMetrics().density;
            int dipWidth = (int) (sigBounds.width() / density);
            int dipHeight = (int) (sigBounds.height() / density);

            Bitmap returnedBitmap = Bitmap.createBitmap(dipWidth, dipHeight,
                    Bitmap.Config.ARGB_4444);

            float minX = Integer.MAX_VALUE;
            float minY = Integer.MAX_VALUE;

            for (LinePathPoint point : sigPoints) {
                minX = Math.min(minX, point.x);
                minY = Math.min(minY, point.y);
            }

            sigPath.offset(-minX, -minY);

            PathShape pathShape = new PathShape(sigPath, sigBounds.width(), sigBounds.height());
            pathShape.resize(dipWidth, dipHeight);

            Canvas canvas = new Canvas(returnedBitmap);
            pathShape.draw(canvas, bitmapSigPaint);

            return returnedBitmap;
        }

        return null;
    }

    private static class SignatureSavedState extends BaseSavedState {

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SignatureSavedState> CREATOR = new Parcelable.Creator<SignatureSavedState>() {
            public SignatureSavedState createFromParcel(Parcel in) {
                return new SignatureSavedState(in);
            }

            public SignatureSavedState[] newArray(int size) {
                return new SignatureSavedState[size];
            }
        };
        List<LinePathPoint> points;

        SignatureSavedState(Parcelable superState) {
            super(superState);
        }

        private SignatureSavedState(Parcel in) {
            super(in);
            this.points = new ArrayList<>();
            in.readList(points, LinePathPoint.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeList(points);
        }
    }

    public static class LinePathPoint extends Point {

        public static final int TYPE_LINE_START = 0;

        public static final int TYPE_LINE_POINT = 1;
        public static final Parcelable.Creator<LinePathPoint> CREATOR = new Parcelable.Creator<LinePathPoint>() {
            /**
             * Return a new point from the data in the specified parcel.
             */
            public LinePathPoint createFromParcel(Parcel in) {
                LinePathPoint r = new LinePathPoint();
                r.readFromParcel(in);
                return r;
            }

            /**
             * Return an array of rectangles of the specified size.
             */
            public LinePathPoint[] newArray(int size) {
                return new LinePathPoint[size];
            }
        };
        private int type;

        public LinePathPoint() {
        }

        public LinePathPoint(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public LinePathPoint(LinePathPoint src) {
            this.x = src.x;
            this.y = src.y;
            this.type = src.type;
        }

        public boolean isStartPoint() {
            return type == TYPE_LINE_START;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(type);
        }

        @Override
        public void readFromParcel(Parcel in) {
            super.readFromParcel(in);
            type = in.readInt();
        }
    }
}
