package org.researchstack.backbone.onboarding;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/22/16.
 */

public enum OnboardingSectionType {
    // TODO: move passcode back to where it needs to be, after registration
    // TODO: for now, a passcode is required to save any data locally, so keep it first, here, until
    // TODO: we figure out how to fix the root cause in the flow.
    @SerializedName(OnboardingSection.PASSCODE_IDENTIFIER)
    PASSCODE(OnboardingSection.PASSCODE_IDENTIFIER),

    @SerializedName(OnboardingSection.LOGIN_IDENTIFIER)
    LOGIN(OnboardingSection.LOGIN_IDENTIFIER),
    @SerializedName(OnboardingSection.ELIGIBILITY_IDENTIFIER)
    ELIGIBILITY(OnboardingSection.ELIGIBILITY_IDENTIFIER),
    @SerializedName(OnboardingSection.CONSENT_IDENTIFIER)
    CONSENT(OnboardingSection.CONSENT_IDENTIFIER),
    @SerializedName(OnboardingSection.REGISTRATION_IDENTIFIER)
    REGISTRATION(OnboardingSection.REGISTRATION_IDENTIFIER),
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

    OnboardingSectionType(String identifier) {
        this.identifier = identifier;
    }

    private String identifier;

    /**
     * @return identifier for OnboardingSectionStep, only to be used for comparison
     *         outside of the OnboardingManager
     */
    public String getIdentifier() {
        return identifier;
    }
}
