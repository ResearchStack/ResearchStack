package org.researchstack.backbone.onboarding;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/22/16.
 */

public enum OnboardingSectionType {
    @SerializedName(OnboardingSection.LOGIN_IDENTIFIER)
    LOGIN(OnboardingSection.LOGIN_IDENTIFIER),
    @SerializedName(OnboardingSection.ELIGIBILITY_IDENTIFIER)
    ELIGIBILITY(OnboardingSection.ELIGIBILITY_IDENTIFIER),
    @SerializedName(OnboardingSection.CONSENT_IDENTIFIER)
    CONSENT(OnboardingSection.CONSENT_IDENTIFIER),
    @SerializedName(OnboardingSection.REGISTRATION_IDENTIFIER)
    REGISTRATION(OnboardingSection.REGISTRATION_IDENTIFIER),
    @SerializedName(OnboardingSection.PASSCODE_IDENTIFIER)
    PASSCODE(OnboardingSection.PASSCODE_IDENTIFIER),
    @SerializedName(OnboardingSection.EMAIL_VERIFICATION_IDENTIFIER)
    EMAIL_VERIFICATION(OnboardingSection.EMAIL_VERIFICATION_IDENTIFIER),
    @SerializedName(OnboardingSection.PERMISSIONS_IDENTIFIER)
    PERMISSIONS(OnboardingSection.PERMISSIONS_IDENTIFIER),
    @SerializedName(OnboardingSection.PROFILE_IDENTIFIER)
    PROFILE(OnboardingSection.PROFILE_IDENTIFIER),
    @SerializedName(OnboardingSection.COMPLETION_IDENTIFIER)
    COMPLETION(OnboardingSection.COMPLETION_IDENTIFIER),
    // Custom onboarding section, identifier should be set in OnboardingSection class
    CUSTOM(null);

    private String identifier;

    OnboardingSectionType(String identifier) {
        this.identifier = identifier;
    }

    String getIdentifier() {
        return identifier;
    }
}
