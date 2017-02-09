package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.ArcDrawable;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;

/**
 * Created by TheMDP on 2/4/17.
 */

public class CountdownStepLayout extends FixedSubmitBarLayout implements StepLayout {

    private StepCallbacks callbacks;

    private static final long ANIMATION_DELAY = (long)(1000.0 / 60.0); // ~60fps

    private long startTime;
    private Handler mainHandler;
    private Runnable animationRunnable;
    private ArcDrawable arcDrawable;

    protected CountdownStep step;
    protected long stepDurationInMs;

    protected TextView titleTextView;
    protected TextView countdownTextview;

    public CountdownStepLayout(Context context) {
        super(context);
    }

    public CountdownStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountdownStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public CountdownStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_step_layout_countdown;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateStep(step);

        setupViews();

        long timeOfAnimationStart = System.currentTimeMillis();
        if (result != null && result.getResult() instanceof Long) {
            timeOfAnimationStart = (Long)result.getResult();
        }
        startAnimation(timeOfAnimationStart);
    }

    private void setupViews() {

        titleTextView = (TextView) contentContainer.findViewById(R.id.rsb_countdown_title);
        countdownTextview = (TextView) contentContainer.findViewById(R.id.rsb_countdown_timer_textview);
        countdownTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                .getDimensionPixelSize(R.dimen.rsb_step_layout_countdown_text_size));
        arcDrawable = new ArcDrawable();
        arcDrawable.setColor(ContextCompat.getColor(getContext(), R.color.rsb_countdown_step_layout_timer_color));
        arcDrawable.setArchWidth(getContext().getResources()
                .getDimensionPixelSize(R.dimen.rsb_step_layout_countdown_stroke_width));

        countdownTextview.setBackground(arcDrawable);

        if (step.getTitle() == null) {
            step.setTitle(getContext().getString(R.string.rsb_COUNTDOWN_LABEL));
        }
        titleTextView.setText(step.getTitle());

        if (this.step.getShouldContinueOnFinish()) {
            submitBar.setVisibility(View.GONE);
        } else {
            submitBar.getPositiveActionView().setEnabled(false);
        }
    }

    protected void validateStep(Step step) {
        if (!(step instanceof CountdownStep)) {
            throw new IllegalStateException("CountdownStepLayout must be passed a CountdownStep");
        }

        this.step = (CountdownStep)step;

        if (this.step.getStepDuration() < 3) {
            throw new IllegalStateException("CountdownStep cannot have a step duration less than 3 seconds");
        }

        stepDurationInMs = this.step.getStepDuration() * 1000;  // 1000 = ms per sec
    }

    private void startAnimation(long startTime) {
        this.startTime = startTime;
        mainHandler = new Handler();

        animationRunnable = new Runnable() {
            @Override
            public void run() {
                long timeElapsedInMs = System.currentTimeMillis() - startTime;
                float percentComplete = 1.0f - ((float)timeElapsedInMs / (float)stepDurationInMs);

                if (percentComplete < 0) {
                    arcDrawable.setSweepAngle(0.0f);
                    countdownTextview.setText("0");
                    onCountdownComplete();
                } else {
                    arcDrawable.setSweepAngle(ArcDrawable.FULL_SWEEPING_ANGLE * percentComplete);
                    int secondsLeft = (int)(step.getStepDuration() * percentComplete) + 1;
                    countdownTextview.setText(String.valueOf(secondsLeft));
                    mainHandler.postDelayed(animationRunnable, ANIMATION_DELAY);
                }
            }
        };
        mainHandler.post(animationRunnable);
    }

    protected void onCountdownComplete() {
        if (step.getShouldContinueOnFinish()) {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
        } else {
            submitBar.getPositiveActionView().setEnabled(true);
        }
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return true;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mainHandler.removeCallbacks(animationRunnable);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        StepResult<Long> stepResult = new StepResult<>(step);
        stepResult.setResult(startTime);
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, step, stepResult);
        return super.onSaveInstanceState();
    }
}
