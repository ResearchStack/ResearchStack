package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.PedometerRecorder;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.WalkingTaskStep;

/**
 * Created by TheMDP on 2/16/17.
 *
 * The WalkingTaskStepLayout is basically the same as the ActiveStepLayout, except that it
 * limits the duration of the step based on the user's number of steps taken so far
 *
 * It also shows an indefinite progress dialog, since we don't know if it will end based on
 * the stepDuration or the numberOfStepsPerLeg
 */

public class WalkingTaskStepLayout extends ActiveStepLayout {

    private WalkingTaskStep walkingTaskStep;

    public WalkingTaskStepLayout(Context context) {
        super(context);
    }

    public WalkingTaskStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WalkingTaskStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WalkingTaskStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);

        timerTextview.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void validateStep(Step step) {
        if (!(step instanceof WalkingTaskStep)) {
            throw new IllegalStateException("WalkingTaskStepLayout must have an WalkingTaskStep");
        }
        walkingTaskStep = (WalkingTaskStep) step;
        super.validateStep(step);
    }

    @Override
    protected void start() {
        super.start();

        // Loop through and try to find the Pedometer recorder
        if (recorderList != null) {
            for (Recorder recorder : recorderList) {
                if (recorder instanceof PedometerRecorder) {
                    PedometerRecorder pedometerRecorder = (PedometerRecorder)recorder;
                    pedometerRecorder.setPedometerListener(new PedometerRecorder.PedometerListener() {
                        @Override
                        public void onStepTaken(int stepCount, float distance) {
                            if (walkingTaskStep.getNumberOfStepsPerLeg() > 0 &&
                               (stepCount >= walkingTaskStep.getNumberOfStepsPerLeg()))
                            {
                                WalkingTaskStepLayout.super.stop();
                            }
                        }
                    });
                }
            }
        }
    }
}
