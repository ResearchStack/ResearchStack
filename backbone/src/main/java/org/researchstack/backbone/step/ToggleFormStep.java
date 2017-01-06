package org.researchstack.backbone.step;

import org.researchstack.backbone.step.navigation.NavigationFormStep;

import java.util.List;

/**
 * Created by TheMDP on 1/3/17.
 */

public class ToggleFormStep extends NavigationFormStep {

    public ToggleFormStep(String identifier, String title, String text) {
        super(identifier, title, text);
    }

    public ToggleFormStep(String identifier, String title, String text, List<QuestionStep> steps) {
        super(identifier, title, text, steps);
    }
}
