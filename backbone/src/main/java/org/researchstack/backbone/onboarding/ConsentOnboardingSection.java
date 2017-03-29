package org.researchstack.backbone.onboarding;

import android.content.Context;

import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;

/**
 * Created by TheMDP on 1/5/17.
 */

public class ConsentOnboardingSection extends OnboardingSection {

    // Serialization must be done manually in OnboardingSectionAdapter
    ConsentDocument consentDocument;

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
