package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.RequireSystemFeatureStepLayout;

/**
 * Created by TheMDP on 2/21/17.
 *
 * This step forces the user to turn on a system feature before proceeding in the task
 * for now it is just GPS, but this could be expanded to cover Wifi, Cellular, NFC, etc
 */

public class RequireSystemFeatureStep extends InstructionStep {

    private SystemFeature systemFeature;

    /* Default constructor needed for serilization/deserialization of object */
    RequireSystemFeatureStep() {
        super();
    }

    public RequireSystemFeatureStep(SystemFeature systemFeature, String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        this.systemFeature = systemFeature;
    }

    public SystemFeature getSystemFeature() {
        return systemFeature;
    }

    public void setSystemFeature(SystemFeature systemFeature) {
        this.systemFeature = systemFeature;
    }

    /**
     * System hardware feature that must be enabled
     */
    public enum SystemFeature {
        GPS
    }

    @Override
    public Class getStepLayoutClass() {
        return RequireSystemFeatureStepLayout.class;
    }
}
