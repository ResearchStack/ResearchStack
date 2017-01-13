package org.researchstack.backbone.onboarding;

/**
 * Created by TheMDP on 1/12/17.
 */

public class CustomOnboardingSection extends OnboardingSection {

    CustomOnboardingSection(String customOnboardingType) {
        this.customOnboardingType = customOnboardingType;
    }

    @Override
    public String getOnboardingSectionIdentifier() {
        if (onboardingType == OnboardingSectionType.CUSTOM) {
            return customOnboardingType;
        }
        return super.getOnboardingSectionIdentifier();
    }

    /**
     * Since Enums cannot have multiple instances of of an Enum with different member variables
     * We must store the custom identifier elsewhere
     */
    transient String customOnboardingType;
}
