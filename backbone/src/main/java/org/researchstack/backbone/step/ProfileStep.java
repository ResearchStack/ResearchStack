package org.researchstack.backbone.step;

import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.ui.step.layout.ProfileStepLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/4/17.
 *
 * Registration step will collect a user's information that is needed to sign up with the server
 * This usually includes name, email, password, etc
 */

public class ProfileStep extends FormStep {

    List<ProfileInfoOption> profileInfoOptions = new ArrayList<>();
    public List<ProfileInfoOption> getProfileInfoOptions() {
        return profileInfoOptions;
    }

    /* Default constructor needed for serilization/deserialization of object */
    ProfileStep() {
        super();
    }

    public ProfileStep(
            String identifier, String title, String text,
            List<ProfileInfoOption> options,
            List<QuestionStep> steps)
    {
        super(identifier, title, text, steps);
        profileInfoOptions = options;
    }

    @Override
    public Class getStepLayoutClass() {
        return ProfileStepLayout.class;
    }
}
