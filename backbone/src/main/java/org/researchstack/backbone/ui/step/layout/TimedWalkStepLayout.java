package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TimedWalkResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.TimedWalkStep;

/**
 * Created by TheMDP on 2/22/17.
 */

public class TimedWalkStepLayout extends ActiveStepLayout {

    private static final double TimedWalkMinimumDistanceInMeters = 1.0;
    private static final double TimedWalkMaximumDistanceInMeters = 10000.0;
    private static final double TimedWalkMinimumDuration = 1.0;

    private long startTime;
    protected TimedWalkStep timedWalkStep;

    public TimedWalkStepLayout(Context context) {
        super(context);
    }

    public TimedWalkStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimedWalkStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimedWalkStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);

        startTime = System.currentTimeMillis();
    }

    @Override
    protected void stepResultFinished() {
        super.stepResultFinished();

        TimedWalkResult timedWalkResult = new TimedWalkResult(timedWalkStep.getIdentifier());
        timedWalkResult.setDistanceInMeters(timedWalkStep.getDistanceInMeters());
        int durationInSeconds = (int)((System.currentTimeMillis() - startTime) / DateUtils.SECOND_IN_MILLIS);
        timedWalkResult.setDuration(durationInSeconds);
        timedWalkResult.setTimeLimit(timedWalkStep.getStepDuration());
        stepResult.setResultForIdentifier(timedWalkResult.getIdentifier(), timedWalkResult);
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);

        if (!(step instanceof TimedWalkStep)) {
            throw new IllegalStateException("TimedWalkStepLayout must have an TimedWalkStep");
        }
        timedWalkStep = (TimedWalkStep) step;

        if (timedWalkStep.getDistanceInMeters() < TimedWalkMinimumDistanceInMeters ||
            timedWalkStep.getDistanceInMeters() > TimedWalkMaximumDistanceInMeters)
        {
            throw new IllegalStateException("timed walk distance must be greater than or equal to " +
                    TimedWalkMinimumDistanceInMeters + " meters and less than or equal to " +
                    TimedWalkMaximumDistanceInMeters + " meters");
        }

        if (timedWalkStep.getStepDuration() < TimedWalkMinimumDuration) {
            throw new IllegalStateException("duration cannot be shorter than " + TimedWalkMinimumDuration + " seconds.");
        }
    }
}
