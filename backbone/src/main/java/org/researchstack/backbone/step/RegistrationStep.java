package org.researchstack.backbone.step;

import android.content.Context;

import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.ui.step.layout.RegistrationStepLayout;

import java.util.Arrays;
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

    /**
     * @param context used by the surveyFactory to create QuestionSteps from ProfileInfoOptions
     * @param surveyFactory if null, the default one will be used
     * @param identifier ProfileStep identifier
     * @param title ProfileStep title
     * @param text ProfileStep text
     */
    public RegistrationStep(
            Context context,
            SurveyFactory surveyFactory,
            String identifier,
            String title,
            String text)
    {
        this(context, surveyFactory, identifier, title, text, Arrays.asList(
                ProfileInfoOption.EMAIL,
                ProfileInfoOption.PASSWORD), true); // also create a Confirm Password option after Password
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
    public RegistrationStep(
            Context context,
            SurveyFactory surveyFactory,
            String identifier, String title, String text,
            List<ProfileInfoOption> options,
            boolean alsoAddConfirmPasswordOption)
    {
        super (context, surveyFactory, identifier, title, text, options, alsoAddConfirmPasswordOption);
    }

    @Override
    public Class getStepLayoutClass() {
        return RegistrationStepLayout.class;
    }
}
