package org.researchstack.backbone.onboarding;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.utils.ConsentDocumentFactory;
import org.researchstack.backbone.utils.SurveyFactory;

import java.util.List;

/**
 * Created by TheMDP on 12/22/16.
 */

public class OnboardingSection {

    public OnboardingSection() {}

    static final String ONBOARDING_TYPE_GSON = "onboardingType";
    @SerializedName(ONBOARDING_TYPE_GSON)
    public OnboardingSectionType onboardingType;

    static final String ONBOARDING_SURVEY_ITEMS_GSON = "steps";
    @SerializedName(ONBOARDING_SURVEY_ITEMS_GSON)
    public List<SurveyItem> surveyItems;

    // Isnt deserialized into a field, but is used in the deserialization process
    static final String ONBOARDING_RESOURCE_NAME_GSON = "resourceName";

    transient private SurveyFactory surveyFactory;
    public SurveyFactory getDefaultOnboardingSurveyFactory() {
        if (surveyFactory != null) {
            return surveyFactory;
        }

        if (onboardingType == OnboardingSectionType.CONSENT) {
            surveyFactory = new ConsentDocumentFactory(surveyItems);
        } else {
            surveyFactory = new SurveyFactory(surveyItems);
        }
        return surveyFactory;
    }
}
