package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 12/31/16.
 */

public interface NavigationStep {
    // Step identifier to go to if the quiz passed
    String getSkipToStepIdentifier();
    void setSkipToStepIdentifier(String identifier);

    // Should the rule skip if results match expected
    boolean getSkipIfPassed();
    void setSkipIfPassed(boolean skipIfPassed);
}
