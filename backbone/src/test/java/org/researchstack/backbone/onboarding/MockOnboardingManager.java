package org.researchstack.backbone.onboarding;

import android.content.Context;

import org.researchstack.backbone.onboarding.OnboardingManager;
import org.researchstack.backbone.onboarding.ResourceNameToStringConverter;

/**
 * Created by TheMDP on 1/12/17.
 */

public class MockOnboardingManager extends OnboardingManager {

    private boolean isLoginVerified = false;
    private boolean isRegistered = false;
    private boolean hasPasscode = false;

    MockOnboardingManager(Context context, String onboardingResourceName, ResourceNameToStringConverter jsonProvider) {
        super(context, onboardingResourceName, jsonProvider);
    }

    MockOnboardingManager(String onboardingResourceName, ResourceNameToStringConverter jsonProvider) {
        super(null, onboardingResourceName, jsonProvider);
    }

    void setIsLoginVerified(boolean isLoginVerified) {
        this.isLoginVerified = isLoginVerified;
    }

    void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    void setHasPasscode(boolean hasPasscode) {
        this.hasPasscode = hasPasscode;
    }

    @Override
    boolean isLoginVerified(Context context) {
        return isLoginVerified;
    }

    @Override
    boolean isRegistered(Context context) {
        return isRegistered;
    }

    @Override
    boolean hasPasscode(Context context) {
        return hasPasscode;
    }
}
