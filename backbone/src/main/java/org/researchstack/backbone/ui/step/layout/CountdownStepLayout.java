package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.ArcDrawable;

/**
 * Created by TheMDP on 2/4/17.
 *
 * The CountdownStepLayout displays a simple countdown with a rotating arc that is full at the start
 * and becomes smaller until the countdown is over
 */

public class CountdownStepLayout extends ActiveStepLayout {

    private static final long ANIMATION_DELAY = (long)(1000.0 / 60.0); // ~60fps
    private ArcDrawable arcDrawable;
    protected Runnable fineAnimationRunnable; // runs once per ANIMATION_DELAY

    protected CountdownStep countdownStep;

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
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
        setupCountdownViews();
        startArcDrawableAnimation();
        startAnimation();
    }

    private void setupCountdownViews() {

        int countdownSize = getContext().getResources()
                .getDimensionPixelSize(R.dimen.rsb_step_layout_countdown_size);
        // Increase the size of the timer text view to fit the arc drawable
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)timerTextview.getLayoutParams();
        params.width = countdownSize;
        params.height = countdownSize;
        timerTextview.setLayoutParams(params);

        arcDrawable = new ArcDrawable();
        arcDrawable.setColor(ContextCompat.getColor(getContext(), R.color.rsb_countdown_step_layout_timer_color));
        arcDrawable.setArchWidth(getContext().getResources()
                .getDimensionPixelSize(R.dimen.rsb_step_layout_countdown_stroke_width));
        timerTextview.setBackground(arcDrawable);
        timerTextview.setVisibility(View.VISIBLE);

        if (countdownStep.getTitle() == null) {
            countdownStep.setTitle(getContext().getString(R.string.rsb_COUNTDOWN_LABEL));
        }
        titleTextview.setText(countdownStep.getTitle());
        titleTextview.setGravity(Gravity.CENTER);
        titleTextview.setPadding(0, 0, 0, 0);
        titleTextview.setVisibility(View.VISIBLE);
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);
        if (!(step instanceof CountdownStep)) {
            throw new IllegalStateException("CountdownStepLayout must be passed a CountdownStep");
        }

        countdownStep = (CountdownStep)step;

        if (countdownStep.getStepDuration() < 3) {
            throw new IllegalStateException("CountdownStep cannot have a activeStep duration less than 3 seconds");
        }
    }

    @Override
    protected void doUIAnimationPerSecond() {
        timerTextview.setText(String.valueOf(secondsLeft));
    }

    protected void startArcDrawableAnimation() {
        fineAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                long timeElapsedInMs = System.currentTimeMillis() - startTime;
                float percentLeft = (float)timeElapsedInMs / (float)(DateUtils.SECOND_IN_MILLIS * (activeStep.getStepDuration() - 1));
                float percentComplete = 1.0f - percentLeft;

                if (percentComplete < 0) {
                    arcDrawable.setSweepAngle(0.0f);
                } else {
                    arcDrawable.setSweepAngle(ArcDrawable.FULL_SWEEPING_ANGLE * percentComplete);
                    mainHandler.postDelayed(fineAnimationRunnable, ANIMATION_DELAY);
                }
            }
        };
        // All mainHandler callbacks are cancelled correctly by base class
        mainHandler.postDelayed(fineAnimationRunnable, ANIMATION_DELAY);
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, activeStep, null);
        return true;
    }
}
