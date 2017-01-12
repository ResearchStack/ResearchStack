package org.researchstack.backbone.step;

/**
 * Created by TheMDP on 1/5/17.
 *
 * This is simply used to keep track of if a Step is a CustomStep
 */

public class CustomStep extends Step {
    String customTypeIdentifier;

    /* Default constructor needed for serilization/deserialization of object */
    CustomStep() {
        super();
    }

    /**
     * Returns a new step initialized with the specified identifier and title.
     * @param identifier The unique identifier of the step.
     * @param title      The primary text to display for this step.
     * @param customTypeIdentifier the value of deserialized "type" field
     */
    public CustomStep(String identifier, String title, String customTypeIdentifier) {
        super(identifier, title);
        this.customTypeIdentifier = customTypeIdentifier;
    }

    public String getCustomTypeIdentifier() {
        return customTypeIdentifier;
    }
}
