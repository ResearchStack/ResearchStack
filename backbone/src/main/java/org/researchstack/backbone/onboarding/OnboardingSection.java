package org.researchstack.backbone.onboarding;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;

import java.util.List;

/**
 * Created by TheMDP on 12/22/16.
 */

public class OnboardingSection {

    /**
     * These are pre-defined onboarding section identifiers
     * that are used to create default helper enums for OnboardingSectionType
     */
    public static final String LOGIN_IDENTIFIER = "login";
    public static final String ELIGIBILITY_IDENTIFIER = "eligibility";
    public static final String CONSENT_IDENTIFIER = "consent";
    public static final String REGISTRATION_IDENTIFIER = "registration";
    public static final String PASSCODE_IDENTIFIER = "passcode";
    public static final String EMAIL_VERIFICATION_IDENTIFIER = "emailVerification";
    public static final String PERMISSIONS_IDENTIFIER = "permissions";
    public static final String PROFILE_IDENTIFIER = "profile";
    public static final String COMPLETION_IDENTIFIER = "completion";

    public OnboardingSection() {
        super();
    }

    static final String ONBOARDING_TYPE_GSON = "onboardingType";
    @SerializedName(ONBOARDING_TYPE_GSON)
    OnboardingSectionType onboardingType;
    public OnboardingSectionType getOnboardingSectionType() {
        return onboardingType;
    }

    public String getOnboardingSectionIdentifier() {
        return onboardingType.getIdentifier();
    }

    static final String ONBOARDING_SURVEY_ITEMS_GSON = "steps";
    @SerializedName(ONBOARDING_SURVEY_ITEMS_GSON)
    public List<SurveyItem> surveyItems;

    // Isnt deserialized into a field, but is used in the deserialization process
    static final String ONBOARDING_RESOURCE_NAME_GSON = "resourceName";

    transient SurveyFactory surveyFactory;
    public SurveyFactory getDefaultOnboardingSurveyFactory(
            Context context,
            SurveyFactory.CustomStepCreator customStepCreator)
    {
        if (surveyFactory != null) {
            return surveyFactory;
        }

        surveyFactory = new SurveyFactory();
        surveyFactory.setCustomStepCreator(customStepCreator);
        return surveyFactory;
    }
}
