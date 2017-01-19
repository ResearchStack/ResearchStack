package org.researchstack.backbone.step;

import org.researchstack.backbone.model.ProfileInfoOption;

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
    public Class<?> getStepBodyClass() {
        // TODO: need custom RegistrationStepLayout
        // TODO: name, email, password, etc. and can make call to web to register account
        return super.getStepBodyClass();
    }
}
