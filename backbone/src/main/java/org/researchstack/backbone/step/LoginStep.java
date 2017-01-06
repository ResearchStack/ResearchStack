package org.researchstack.backbone.step;


import org.researchstack.backbone.model.ProfileInfoOption;

import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 */

public class LoginStep extends ProfileStep {

    public LoginStep(String identifier, String title, String text, List<ProfileInfoOption> options, List<QuestionStep> steps) {
        super(identifier, title, text, options, steps);
    }

    @Override
    public Class<?> getStepBodyClass()
    {
        // TODO: need custom LoginStepLayout, one exists as SignInStepLayout, but is in Skin module
        return super.getStepBodyClass();
    }
}
