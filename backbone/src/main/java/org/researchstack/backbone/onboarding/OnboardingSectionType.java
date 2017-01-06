package org.researchstack.backbone.onboarding;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/22/16.
 */

public enum OnboardingSectionType {
    @SerializedName("login")
    LOGIN("login"),
    @SerializedName("eligibility")
    ELIGIBILITY("eligibility"),
    @SerializedName("consent")
    CONSENT("consent"),
    @SerializedName("registration")
    REGISTRATION("registration"),
    @SerializedName("passcode")
    PASSCODE("passcode"),
    @SerializedName("emailVerification")
    EMAIL_VERIFICATION("emailVerification"),
    @SerializedName("permissions")
    PERMISSIONS("permissions"),
    @SerializedName("profile")
    PROFILE("profile"),
    @SerializedName("completion")
    COMPLETION("completion");

    OnboardingSectionType(String identifier) {
        this.identifier = identifier;
    }

    String identifier;
    public String getIdentifier() {
        return identifier;
    }
}
