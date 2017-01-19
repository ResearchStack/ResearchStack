package org.researchstack.backbone.step;

import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.ui.step.layout.RegistrationStepLayout;
import org.researchstack.backbone.ui.step.layout.SurveyStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 */

public class RegistrationStep extends ProfileStep {

    /* Default constructor needed for serilization/deserialization of object */
    RegistrationStep() {
        super();
    }

    public RegistrationStep(String identifier, String title, String text, List<ProfileInfoOption> options, List<QuestionStep> steps) {
        super(identifier, title, text, options, steps);
    }

    @Override
    public Class getStepLayoutClass() {
        return RegistrationStepLayout.class;
    }
}
