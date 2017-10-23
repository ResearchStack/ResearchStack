package org.researchstack.backbone.onboarding;

import android.content.Context;

/**
 * Created by TheMDP on 1/12/17.
 */

public class MockOnboardingManager extends OnboardingManager {

    private boolean isLoginVerified = false;
    private boolean isRegistered = false;
    private boolean hasPasscode = false;

    MockOnboardingManager(Context context) {
        super(context);
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
