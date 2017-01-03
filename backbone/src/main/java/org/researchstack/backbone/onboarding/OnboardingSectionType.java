package org.researchstack.backbone.onboarding;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/22/16.
 */

public enum OnboardingSectionType {
    @SerializedName("login")
    LOGIN,
    @SerializedName("eligibility")
    ELIGIBILITY,
    @SerializedName("consent")
    CONSENT,
    @SerializedName("registration")
    REGISTRATION,
    @SerializedName("passcode")
    PASSCODE,
    @SerializedName("emailVerification")
    EMAIL_VERIFICATION,
    @SerializedName("permissions")
    PERMISSIONS,
    @SerializedName("profile")
    PROFILE,
    @SerializedName("completion")
    COMPLETION;
}
