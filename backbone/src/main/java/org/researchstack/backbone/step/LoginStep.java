package org.researchstack.backbone.step;


import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.ui.step.layout.LoginStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 */

public class LoginStep extends ProfileStep {

    /* Default constructor needed for serilization/deserialization of object */
    LoginStep() {
        super();
    }

    public LoginStep(String identifier, String title, String text, List<ProfileInfoOption> options, List<QuestionStep> steps) {
        super(identifier, title, text, options, steps);
    }

    @Override
    public Class getStepLayoutClass() {
        return LoginStepLayout.class;
    }
}
