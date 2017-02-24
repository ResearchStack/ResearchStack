package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TappingIntervalResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TappingIntervalStep;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.researchstack.backbone.result.TappingIntervalResult.TappingButtonIdentifier.TappingButtonIdentifierLeft;
import static org.researchstack.backbone.result.TappingIntervalResult.TappingButtonIdentifier.TappingButtonIdentifierNone;
import static org.researchstack.backbone.result.TappingIntervalResult.TappingButtonIdentifier.TappingButtonIdentifierRight;

/**
 * Created by TheMDP on 2/23/17.
 */

public class TappingIntervalStepLayout extends ActiveStepLayout {

    protected TappingIntervalStep tappingIntervalStep;

    protected Rect stepViewRect;
    protected Rect leftButtonRect;
    protected Rect rightButtonRect;

    protected long startTime;
    protected int tapCount;
    protected List<TappingIntervalResult.Sample> sampleList;

    private static final int LEFT_BUTTON     = 0;
    private static final int RIGHT_BUTTON    = 1;
    private static final int NO_BUTTON       = 2;
    protected TappingIntervalResult.Sample[] buttonSamples = new TappingIntervalResult.Sample[NO_BUTTON + 1];

    protected int[] lastPointerIdx = new int[NO_BUTTON + 1];
    private static final int INVALID_POINTER_IDX  = -1;

    protected RelativeLayout tappingStepLayout;
    protected TextView tapCountTextView;
    protected FloatingActionButton leftTappingButton;
    protected FloatingActionButton rightTappingButton;

    public TappingIntervalStepLayout(Context context) {
        super(context);
    }

    public TappingIntervalStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TappingIntervalStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TappingIntervalStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);
        if (!(step instanceof TappingIntervalStep)) {
            throw new IllegalStateException("TappingIntervalStepLayout must have an TappingIntervalStep");
        }
        tappingIntervalStep = (TappingIntervalStep) step;
    }

    @Override
    protected void setupActiveViews() {
        super.setupActiveViews();

        remainingHeightOfContainer(new HeightCalculatedListener() {
            @Override
            public void heightCalculated(int height) {
                tappingStepLayout = (RelativeLayout)layoutInflater.inflate(R.layout.rsb_step_layout_tapping_interval, activeStepLayout, false);
                tapCountTextView = (TextView) tappingStepLayout.findViewById(R.id.rsb_total_taps_counter);
                tapCountTextView.setText(String.format(Locale.getDefault(), "%2d", 0));
                leftTappingButton = (FloatingActionButton) tappingStepLayout.findViewById(R.id.rsb_tapping_interval_button_left);
                rightTappingButton = (FloatingActionButton) tappingStepLayout.findViewById(R.id.rsb_tapping_interval_button_right);

                progressBarHorizontal.setProgress(0);
                progressBarHorizontal.setMax(activeStep.getStepDuration());
                progressBarHorizontal.setVisibility(View.VISIBLE);

                activeStepLayout.addView(tappingStepLayout, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height));
            }
        });
    }

    @Override
    protected void doUIAnimationPerSecond() {
        super.doUIAnimationPerSecond();
        progressBarHorizontal.setProgress(progressBarHorizontal.getProgress() + 1);
    }

    @Override
    protected void start() {
        super.start();

        startTime = System.currentTimeMillis();
        tapCount = 0;
        progressBar.setProgress(0);
        sampleList = new ArrayList<>();
        tapCountTextView.setText(String.format(Locale.getDefault(), "%2d", tapCount));
        for (int i = 0; i <= NO_BUTTON; i++) {
            lastPointerIdx[i] = INVALID_POINTER_IDX;
        }

        // Calculate view sizes
        int[] activeStepLayoutXY = new int[2];
        activeStepLayout.getLocationOnScreen(activeStepLayoutXY);
        stepViewRect = new Rect(activeStepLayoutXY[0], activeStepLayoutXY[1],
                activeStepLayout.getWidth(), activeStepLayout.getHeight());

        // Button rects are relative to the stepViewRect
        int[] leftButtonXY = new int[2];
        leftTappingButton.getLocationOnScreen(leftButtonXY);
        leftButtonRect = new Rect(
                leftButtonXY[0] - activeStepLayoutXY[0],
                leftButtonXY[1] - activeStepLayoutXY[1],
                leftTappingButton.getWidth(), leftTappingButton.getHeight());

        int[] rightButtonXY = new int[2];
        rightTappingButton.getLocationOnScreen(rightButtonXY);
        rightButtonRect = new Rect(
                rightButtonXY[0] - activeStepLayoutXY[0],
                rightButtonXY[1] - activeStepLayoutXY[1],
                rightTappingButton.getWidth(), rightTappingButton.getHeight());

        // Assign and wait for user touches
        setupTouchListener(leftTappingButton, LEFT_BUTTON, TappingButtonIdentifierLeft, true);
        setupTouchListener(rightTappingButton, RIGHT_BUTTON, TappingButtonIdentifierRight, true);
        setupTouchListener(activeStepLayout, NO_BUTTON, TappingButtonIdentifierNone, false);
    }

    protected void setupTouchListener(
            final View view,
            final int idx,
            TappingIntervalResult.TappingButtonIdentifier buttonId,
            boolean countsAsATap)
    {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Make sure we aren't overriding another finger's down tap
                        if (lastPointerIdx[idx] == INVALID_POINTER_IDX) {
                            buttonSamples[idx] = new TappingIntervalResult.Sample();
                            buttonSamples[idx].setTimestamp(motionEvent.getEventTime() - startTime);
                            buttonSamples[idx].setButtonIdentifier(buttonId);
                            buttonSamples[idx].setLocation(new Point(
                                    (int)(motionEvent.getX() + leftButtonRect.left),
                                    (int)(motionEvent.getY() + leftButtonRect.top)));
                            lastPointerIdx[idx] = motionEvent.getActionMasked();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:

                        // We need to make sure the finger index matches up with the
                        // finger index that started the "down" motion event
                        boolean correctFingerForIdx =
                                motionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL ||
                                (motionEvent.getActionMasked() == MotionEvent.ACTION_UP &&
                                lastPointerIdx[idx] == MotionEvent.ACTION_DOWN) ||
                                (motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP &&
                                lastPointerIdx[idx] == MotionEvent.ACTION_POINTER_DOWN);

                        // Make sure we have the same finger's up tap
                        if (buttonSamples[idx] != null && correctFingerForIdx) {
                            buttonSamples[idx].setDuration(motionEvent.getDownTime());
                            sampleList.add(buttonSamples[idx]);
                            buttonSamples[idx] = null;
                            lastPointerIdx[idx] = INVALID_POINTER_IDX;
                            if (countsAsATap) {
                                countATap();
                            }
                            Log.d("TODO_REMOVE", "tap up with idx " + idx);
                        }
                        break;
                }

                if (!countsAsATap) {
                    return true;
                } else {
                    return view.onTouchEvent(motionEvent);
                }
            }
        });
    }

    @Override
    protected void stop() {
        super.stop();

        // Complete any touches that have had a down but no up
        for (int i = 0; i <= RIGHT_BUTTON; i++) {
            if (buttonSamples[i] != null) {
                buttonSamples[i].setDuration(System.currentTimeMillis() - buttonSamples[i].getTimestamp());
                sampleList.add(buttonSamples[i]);
                buttonSamples[i] = null;
            }
        }

        if (sampleList == null || sampleList.isEmpty()) {
            return;
        }

        TappingIntervalResult tappingResult = new TappingIntervalResult(tappingIntervalStep.getIdentifier());
        tappingResult.setStartDate(new Date(startTime));
        tappingResult.setEndDate(new Date());
        tappingResult.setSamples(sampleList);
        tappingResult.setButtonRect1(leftButtonRect);
        tappingResult.setButtonRect2(rightButtonRect);
        tappingResult.setStepViewSize(new TappingIntervalResult.Size(
                stepViewRect.width(), stepViewRect.height()));

        stepResult.getResults().put(tappingResult.getIdentifier(), tappingResult);

        leftTappingButton.setOnTouchListener(null);
        rightTappingButton.setOnTouchListener(null);
        activeStepLayout.setOnTouchListener(null);
    }

    protected void countATap() {
        tapCount++;
        tapCountTextView.setText(String.format(Locale.getDefault(), "%2d", tapCount));
    }
}
