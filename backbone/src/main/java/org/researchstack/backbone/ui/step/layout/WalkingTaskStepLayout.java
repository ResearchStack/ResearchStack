package org.researchstack.backbone.ui.step.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.step.active.recorder.PedometerRecorder;
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
    private BroadcastReceiver pedometerReceiver;

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
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);
        pedometerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                if (PedometerRecorder.BROADCAST_PEDOMETER_UPDATE_ACTION.equals(intent.getAction())) {
                    PedometerRecorder.PedometerUpdateHolder dataHolder =
                            PedometerRecorder.getPedometerUpdateHolder(intent);
                    if (dataHolder != null) {
                        if (walkingTaskStep.getNumberOfStepsPerLeg() > 0 &&
                                (dataHolder.getStepCount() >= walkingTaskStep.getNumberOfStepsPerLeg()))
                        {
                            // TODO: mdephillips 1/13/18
                            // we may want to move this functionality to the PedometerRecorder
                            // and having that signal to RecorderService to stop,
                            // since this StepLayout may be create/destroyed and miss this broadcast
                            WalkingTaskStepLayout.super.stop();
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(PedometerRecorder.BROADCAST_PEDOMETER_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(pedometerReceiver, intentFilter);
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(pedometerReceiver);
    }
}
