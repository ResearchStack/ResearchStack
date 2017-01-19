package org.researchstack.backbone.step;

import org.researchstack.backbone.model.survey.NavigationStep;

import java.util.List;

/**
 * Created by TheMDP on 1/3/17.
 */

public class NavigationFormStep extends FormStep implements NavigationStep {

    // MARK: Stuff you can't extend on a protocol
    String skipToStepIdentifier;
    boolean skipIfPassed;

    /* Default constructor needed for serilization/deserialization of object */
    NavigationFormStep() {
        super();
    }

    public NavigationFormStep(String identifier, String title, String text) {
        super(identifier, title, text);
    }

    public NavigationFormStep(String identifier, String title, String text, List<QuestionStep> steps) {
        super(identifier, title, text, steps);
    }

    @Override
    public String getSkipToStepIdentifier() {
        return skipToStepIdentifier;
    }

    @Override
    public void setSkipToStepIdentifier(String identifier) {
        skipToStepIdentifier = identifier;
    }

    @Override
    public boolean getSkipIfPassed() {
        return skipIfPassed;
    }

    @Override
    public void setSkipIfPassed(boolean skipIfPassed) {
        this.skipIfPassed = skipIfPassed;
    }
}
