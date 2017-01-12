package org.researchstack.backbone.step;

/**
 * Created by TheMDP on 1/5/17.
 *
 * This is simply used to keep track of if a Step is a CustomStep
 */

public class CustomStep extends InstructionStep {
    String customTypeIdentifier;

    /* Default constructor needed for serilization/deserialization of object */
    CustomStep() {
        super();
    }

    public CustomStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    public void setCustomTypeIdentifier(String identifier) {
        customTypeIdentifier = identifier;
    }
    public String getCustomTypeIdentifier() {
        return customTypeIdentifier;
    }
}
