package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ConsentReviewSignatureView extends View
{

    private static final boolean DEBUG = false;

    private SignatureCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Paint
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
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

    public ConsentReviewSignatureView(Context context)
    {
        super(context);
        init(null);
    }

    public ConsentReviewSignatureView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }


    public ConsentReviewSignatureView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Init all paint objects
     * TODO: Read attrs of signature paint and hint paint from attrs
     */
    private void init(AttributeSet attrs)
    {
        hintText = "Sign Here";
        guidelineHeight = (int) (getResources().getDisplayMetrics().density * 1);
        guidelineMargin = (int) (getResources().getDisplayMetrics().density * 12);

        sigPaint.setAntiAlias(true);
        sigPaint.setColor(Color.BLACK);
        sigPaint.setStyle(Paint.Style.STROKE);
        sigPaint.setStrokeJoin(Paint.Join.ROUND);
        sigPaint.setStrokeCap(Paint.Cap.ROUND);
        sigPaint.setPathEffect(new CornerPathEffect(20));
        sigPaint.setStrokeWidth(5f);

        hintPaint.setAntiAlias(true);
        hintPaint.setColor(Color.LTGRAY);
        hintPaint.setStyle(Paint.Style.FILL);
        hintPaint.setTextSize(42);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        drawBounds.left = getPaddingLeft();
        drawBounds.top = getPaddingTop();
        drawBounds.right = w - getPaddingRight();
        drawBounds.bottom = h - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // Debug
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        if(DEBUG)
        {
            Paint debug = new Paint();

            //Draw canvas
            debug.setColor(Color.parseColor("#70f9fc"));
            canvas.drawColor(debug.getColor());

            //Draw drawing area
            debug.setColor(Color.parseColor("#f9f9f9"));
            canvas.drawRect(drawBounds, debug);
        }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // Draw Guide
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        canvas.drawRect(drawBounds.left, drawBounds.bottom - guidelineHeight,
                        drawBounds.right, drawBounds.bottom, hintPaint);

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        // Draw signature or hint text
        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        if(sigPath.isEmpty())
        {
            int baselineY = drawBounds.bottom - guidelineMargin - guidelineHeight;
            canvas.drawText(hintText, drawBounds.left, baselineY, hintPaint);
        }
        else
        {
            canvas.drawPath(sigPath, sigPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eX = event.getX();
        float eY = event.getY();

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(sigPath.isEmpty())
                {
                    callbacks.onSignatureDrawn();
                }

                sigPath.moveTo(eX, eY);

                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int hSize = event.getHistorySize();

                for (int i = 0; i < hSize; i++) {
                    float hX = event.getHistoricalX(i);
                    float hY = event.getHistoricalY(i);
                    sigPath.lineTo(hX, hY);
                }

                sigPath.lineTo(eX, eY);
                break;
            default:
                break;
        }

        //TODO Pass in dirty rect instead of invalidating the entire view.
        ViewCompat.postInvalidateOnAnimation(this);

        return true;
    }

    public void clearSignature()
    {
        sigPath.rewind();

        ViewCompat.postInvalidateOnAnimation(this);

        if(callbacks != null)
        {
            callbacks.onSignatureCleared();
        }
    }

    public boolean isSignatureDrawn()
    {
        return ! sigPath.isEmpty();
    }

    public void setCallbacks(SignatureCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
