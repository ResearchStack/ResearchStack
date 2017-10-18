package org.researchstack.backbone.step;

import android.content.Context;
import android.support.annotation.MainThread;

import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
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

    private List<ProfileInfoOption> profileInfoOptions = new ArrayList<>();
    public List<ProfileInfoOption> getProfileInfoOptions() {
        return profileInfoOptions;
    }

    /* Default constructor needed for serilization/deserialization of object */
    ProfileStep() {
        super();
    }

    /**
     * @param identifier ProfileStep identifier
     * @param title ProfileStep title
     * @param text ProfileStep text
     * @param options ProfileInfoOption list that must match up with steps param
     * @param steps QuestionStep list that must match up with options param
     */
    public ProfileStep(
            String identifier, String title, String text,
            List<ProfileInfoOption> options,
            List<QuestionStep> steps)
    {
        super(identifier, title, text, steps);
        profileInfoOptions = options;
    }

    /**
     * @param context used by the surveyFactory to create QuestionSteps from ProfileInfoOptions
     * @param surveyFactory if null, the default one will be used
     * @param identifier ProfileStep identifier
     * @param title ProfileStep title
     * @param text ProfileStep text
     * @param options ProfileInfoOption list to convert into a QuestionStep list for this FormStep subclass
     * @param alsoAddConfirmPasswordOption if true, a confirm password step will be made with password step
     */
    public ProfileStep(
            Context context,
            SurveyFactory surveyFactory,
            String identifier, String title, String text,
            List<ProfileInfoOption> options,
            boolean alsoAddConfirmPasswordOption)
    {
        this(identifier, title, text, options,
            (surveyFactory == null ? new SurveyFactory() : surveyFactory)
                .createQuestionSteps(context, options, alsoAddConfirmPasswordOption));
    }

    @Override
    public Class getStepLayoutClass() {
        return ProfileStepLayout.class;
    }
}
