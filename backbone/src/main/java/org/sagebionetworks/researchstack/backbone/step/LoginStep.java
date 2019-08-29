package org.sagebionetworks.researchstack.backbone.step;


import org.sagebionetworks.researchstack.backbone.model.ProfileInfoOption;
import org.sagebionetworks.researchstack.backbone.ui.step.layout.LoginStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 */

public class LoginStep extends ProfileStep {

    /* Default constructor needed for serilization/deserialization of object */
    protected LoginStep() {
        super();
        setAutoFocusFirstEditText(true);
    }

    public LoginStep(String identifier, String title, String text, List<ProfileInfoOption> options, List<QuestionStep> steps) {
        super(identifier, title, text, options, steps);
        setAutoFocusFirstEditText(true);
    }

    @Override
    public Class getStepLayoutClass() {
        return LoginStepLayout.class;
    }
}
