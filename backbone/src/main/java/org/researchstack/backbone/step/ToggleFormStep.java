package org.researchstack.backbone.step;

import java.util.List;

/**
 * Created by TheMDP on 1/3/17.
 */

public class ToggleFormStep extends NavigationFormStep {

    /* Default constructor needed for serilization/deserialization of object */
    ToggleFormStep() {
        super();
    }

    public ToggleFormStep(String identifier, String title, String text) {
        super(identifier, title, text);
    }

    public ToggleFormStep(String identifier, String title, String text, List<QuestionStep> steps) {
        super(identifier, title, text, steps);
    }
}
