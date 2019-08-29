package org.sagebionetworks.researchstack.backbone.onboarding;

import android.content.Context;

import org.sagebionetworks.researchstack.backbone.model.ConsentDocument;
import org.sagebionetworks.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.sagebionetworks.researchstack.backbone.model.survey.factory.SurveyFactory;

/**
 * Created by TheMDP on 1/5/17.
 */

public class ConsentOnboardingSection extends OnboardingSection {

    // Serialization must be done manually in OnboardingSectionAdapter
    protected ConsentDocument consentDocument;

    @Override
    public SurveyFactory getDefaultOnboardingSurveyFactory(
            Context context,
            SurveyFactory.CustomStepCreator customStepCreator)
    {
        if (surveyFactory != null) {
            return surveyFactory;
        }

        surveyFactory = new ConsentDocumentFactory(consentDocument, customStepCreator);
        return surveyFactory;
    }
}
