package org.researchstack.backbone.onboarding;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/22/16.
 */

public enum OnboardingTaskType {
    @SerializedName("registration")
    REGISTRATION,
    @SerializedName("login")
    LOGIN,
    @SerializedName("reconsent")
    RECONSENT;
}
