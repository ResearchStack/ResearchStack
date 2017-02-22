package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;

/**
 * Created by TheMDP on 2/22/17.
 */

public class TimedWalkStep extends ActiveStep {

    private double distanceInMeters;

    /* Default constructor needed for serilization/deserialization of object */
    TimedWalkStep() {
        super();
    }

    public TimedWalkStep(String identifier, double distanceInMeters) {
        super(identifier);
        commonInit(distanceInMeters);
    }

    public TimedWalkStep(String identifier, String title, String detailText, double distanceInMeters) {
        super(identifier, title, detailText);
        commonInit(distanceInMeters);
    }

    private void commonInit(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldPlaySoundOnStart(true);
        setShouldPlaySoundOnFinish(true);
        setShouldVibrateOnStart(true);
        setShouldVibrateOnFinish(true);
    }

    @Override
    public Class getStepLayoutClass() {
        return ActiveStepLayout.class;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }
}
