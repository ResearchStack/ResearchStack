package org.researchstack.backbone.onboarding;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.survey.factory.SurveyFactoryHelper;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.ToggleFormStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by TheMDP on 1/3/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FingerprintManagerCompat.class, ResourcePathManager.class, ResourceManager.class})
public class OnboardingManagerTest {

    OnboardingManager mOnboardingManager;
    @Mock MockOnboardingManager mMockOnboardingManager;
    SurveyFactoryHelper mSurveyFactoryHelper;

    private MockResourceManager resourceManager;

    @Before
    public void setUp() throws Exception
    {
        ResourcePathManager.init(new MockResourceManager());
        mSurveyFactoryHelper = new SurveyFactoryHelper();

        // All of this, along with the @PrepareForTest and @RunWith above, is needed
        // to mock the resource manager to load resources from the directory src/test/resources
        PowerMockito.mockStatic(ResourcePathManager.class);
        PowerMockito.mockStatic(ResourceManager.class);
        resourceManager = new MockResourceManager();
        PowerMockito.when(ResourceManager.getInstance()).thenReturn(resourceManager);
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "eligibilityrequirements");
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "consent");
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "onboarding");
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "consentdocument");
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "custom_consentdocument");
        resourceManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "section_sort_order_test");

        mOnboardingManager = new OnboardingManager(mSurveyFactoryHelper.mockContext);
        mMockOnboardingManager = new MockOnboardingManager(mSurveyFactoryHelper.mockContext);
    }

    @Test
    public void testTestValidEmailAnswerFormat() {
        // TODO: un-comment this test when PASSCODE is after REGISTRATION again
//        assertNotNull(mOnboardingManager.getSections());
//        assertFalse(mOnboardingManager.getSections().isEmpty());
//        assertEquals(8, mOnboardingManager.getSections().size());
//
//        // Sections assertions
//        assertEquals(OnboardingSectionType.LOGIN,               mOnboardingManager.getSections().get(0).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.ELIGIBILITY,         mOnboardingManager.getSections().get(1).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.CONSENT,             mOnboardingManager.getSections().get(2).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.REGISTRATION,        mOnboardingManager.getSections().get(3).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.PASSCODE,            mOnboardingManager.getSections().get(4).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.EMAIL_VERIFICATION,  mOnboardingManager.getSections().get(5).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.PERMISSIONS,         mOnboardingManager.getSections().get(6).getOnboardingSectionType());
//        assertEquals(OnboardingSectionType.COMPLETION,          mOnboardingManager.getSections().get(7).getOnboardingSectionType());
//
//        // Eligibility assertions
//        OnboardingSection eligibilty = mOnboardingManager.getSections().get(1);
//        assertEquals(3, eligibilty.surveyItems.size());
//        assertEquals(SurveyItemType.QUESTION_TOGGLE, eligibilty.surveyItems.get(0).type);
//        assertTrue(eligibilty.surveyItems.get(0) instanceof ToggleQuestionSurveyItem);
//        ToggleQuestionSurveyItem toggleItem = (ToggleQuestionSurveyItem)eligibilty.surveyItems.get(0);
//        assertEquals("eligibleInstruction", toggleItem.skipIdentifier);
//        assertTrue(toggleItem.skipIfPassed);
//        assertEquals(3, eligibilty.surveyItems.get(0).items.size());
//        assertTrue(eligibilty.surveyItems.get(0) instanceof ToggleQuestionSurveyItem);
//        ToggleQuestionSurveyItem toggle = (ToggleQuestionSurveyItem) eligibilty.surveyItems.get(0);
//        assertEquals(SurveyItemType.QUESTION_BOOLEAN, toggle.items.get(0).type);
//        assertEquals(SurveyItemType.INSTRUCTION,     eligibilty.surveyItems.get(1).type);
//        assertEquals(SurveyItemType.INSTRUCTION,     eligibilty.surveyItems.get(1).type);
//
//        // Consent assertions
//        OnboardingSection consent = mOnboardingManager.getSections().get(2);
//        assertEquals(8, consent.surveyItems.size());
//        assertEquals(SurveyItemType.CUSTOM,         consent.surveyItems.get(0).type);
//        assertEquals("reconsent.instruction",       consent.surveyItems.get(0).getTypeIdentifier());
//        assertEquals(SurveyItemType.CONSENT_VISUAL, consent.surveyItems.get(1).type);
//        assertEquals(SurveyItemType.SUBTASK,        consent.surveyItems.get(2).type);
//        assertEquals(5,                             consent.surveyItems.get(2).items.size());
//
//        assertTrue(consent.surveyItems.get(2) instanceof SubtaskQuestionSurveyItem);
//        SubtaskQuestionSurveyItem consentQuiz = (SubtaskQuestionSurveyItem)consent.surveyItems.get(2);
//
//        assertEquals(consentQuiz.items.get(1).type, SurveyItemType.QUESTION_SINGLE_CHOICE);
//        ChoiceQuestionSurveyItem singleChoice = (ChoiceQuestionSurveyItem)consentQuiz.items.get(1);
//        assertEquals(2, singleChoice.items.size());
//        assertTrue(singleChoice.items.get(0) instanceof Choice);
//        assertEquals(true, singleChoice.expectedAnswer);
//
//        assertTrue(consent.surveyItems.get(3) instanceof InstructionSurveyItem);
//        InstructionSurveyItem consentFailedQuiz = (InstructionSurveyItem)consent.surveyItems.get(3);
//        assertEquals("consent_2quiz_headsup",   consentFailedQuiz.learnMoreHTMLContentURL);
//        assertEquals("icon_retry",              consentFailedQuiz.image);
//
//        assertEquals(SurveyItemType.INSTRUCTION_COMPLETION, consent.surveyItems.get(4).type);
//        assertTrue(consent.surveyItems.get(4) instanceof InstructionSurveyItem);
//        InstructionSurveyItem consentPassed = (InstructionSurveyItem)consent.surveyItems.get(4);
//        assertEquals("You answered all of the questions correctly.", consentPassed.text);
//        assertEquals("Great Job!", consentPassed.title);
//        assertEquals("Tap Next to continue.", consentPassed.detailText);
//
//        assertEquals(SurveyItemType.CONSENT_SHARING_OPTIONS, consent.surveyItems.get(5).type);
//        assertTrue(consent.surveyItems.get(5) instanceof ConsentSharingOptionsSurveyItem);
//
//        assertEquals(SurveyItemType.CONSENT_REVIEW, consent.surveyItems.get(6).type);
//        assertTrue(consent.surveyItems.get(6) instanceof ConsentReviewSurveyItem);
    }

    @Test
    public void testShouldInclude() {
        resetMockToDefaults();

        ShouldIncludeData[] shouldIncludeData = new ShouldIncludeData[] {
                new ShouldIncludeData(
                        OnboardingSectionType.LOGIN,
                        OnboardingTaskType.LOGIN),

                new ShouldIncludeData(
                        OnboardingSectionType.ELIGIBILITY,
                        OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.CONSENT,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.REGISTRATION, OnboardingTaskType.RECONSENT),

                new ShouldIncludeData(
                        OnboardingSectionType.REGISTRATION,
                        OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.PASSCODE,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.REGISTRATION, OnboardingTaskType.RECONSENT),

                new ShouldIncludeData(
                        OnboardingSectionType.EMAIL_VERIFICATION,
                        OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.PERMISSIONS,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.PROFILE,
                        OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.COMPLETION,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.REGISTRATION)
        };

        for (OnboardingSectionType sectionType : OnboardingSectionType.values()) {
            ShouldIncludeData includeData = findType(sectionType, shouldIncludeData);
            if (includeData != null) {  // null for CUSTOM
                for (OnboardingTaskType taskType : OnboardingTaskType.values()) {
                    boolean expectedShouldInclude = includeData.taskTypesIncluded.contains(taskType);
                    boolean actualShouldInclude = mMockOnboardingManager.shouldInclude(null, sectionType, taskType);
                    assertEquals(expectedShouldInclude, actualShouldInclude);
                }
            }
        }
    }

    @Test
    public void testShouldInclude_HasPasscode() {
        resetMockToDefaults();
        mMockOnboardingManager.setHasPasscode(true);

        // Check that if the passcode has been set that it is not included
        for (OnboardingTaskType taskType : OnboardingTaskType.values()) {
            boolean shouldInclude = mMockOnboardingManager.shouldInclude(
                    null, OnboardingSectionType.PASSCODE, taskType);
            assertFalse(shouldInclude);
        }
    }

    @Test
    public void testShouldInclude_HasRegistered() {
        resetMockToDefaults();

        // If the user has registered and this is a completion of the registration
        // then only include email verification and those sections AFTER verification
        // However, if the user is being reconsented then include the reconsent section

        mMockOnboardingManager.setHasPasscode(true);
        mMockOnboardingManager.setIsRegistered(true);

        OnboardingTaskType[] taskTypes = new OnboardingTaskType[] {
                OnboardingTaskType.REGISTRATION, OnboardingTaskType.RECONSENT
        };

        ShouldIncludeData[] shouldIncludeData = new ShouldIncludeData[] {
                new ShouldIncludeData(
                        OnboardingSectionType.LOGIN,
                        OnboardingTaskType.LOGIN),

                // eligibility should *not* be included in registration if the user is at the email verification step
                new ShouldIncludeData(
                        OnboardingSectionType.ELIGIBILITY),

                // consent should *not* be included in registration if the user is at the email verification step
                new ShouldIncludeData(
                        OnboardingSectionType.CONSENT,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.RECONSENT),

                // registration should *not* be included in registration if the user is at the email verification step
                new ShouldIncludeData(
                        OnboardingSectionType.REGISTRATION),

                // passcode should *not* be included in registration if the user is at the email verification step and has already set the passcode
                new ShouldIncludeData(
                        OnboardingSectionType.PASSCODE),

                new ShouldIncludeData(
                        OnboardingSectionType.EMAIL_VERIFICATION,
                        OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.PERMISSIONS,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.PROFILE,
                        OnboardingTaskType.REGISTRATION),

                new ShouldIncludeData(
                        OnboardingSectionType.COMPLETION,
                        OnboardingTaskType.LOGIN, OnboardingTaskType.REGISTRATION)
        };

        for (OnboardingSectionType sectionType : OnboardingSectionType.values()) {
            ShouldIncludeData includeData = findType(sectionType, shouldIncludeData);
            if (includeData != null) { // null for custom
                for (OnboardingTaskType taskType : taskTypes) {
                    boolean expectedShouldInclude = includeData.taskTypesIncluded.contains(taskType);
                    boolean actualShouldInclude = mMockOnboardingManager.shouldInclude(null, sectionType, taskType);
                    assertEquals(expectedShouldInclude, actualShouldInclude);
                }
            }
        }
    }

    @Test
    public void testSortOrder() {
        String oldOnboardingName = resourceManager.getOnboardingJsonName();
        resourceManager.setOnboardingJsonName("section_sort_order_test");

        OnboardingManager manager = new OnboardingManager(mSurveyFactoryHelper.mockContext);
        // See file for section order
        List<String> expectedOrder = new ArrayList<>();

        expectedOrder.add("customWelcome");

        expectedOrder.add("passcode");
        expectedOrder.add("login");
        expectedOrder.add("eligibility");
        expectedOrder.add("consent");
        expectedOrder.add("registration");

        expectedOrder.add("emailVerification");
        expectedOrder.add("permissions");
        expectedOrder.add("profile");
        expectedOrder.add("completion");
        expectedOrder.add("customEnd");

        for (int i = 0; i < manager.getSections().size(); i++) {
            String actualSectionId = manager.getSections().get(i).getOnboardingSectionIdentifier();
            String expectedSectionId = expectedOrder.get(i);
            assertEquals(actualSectionId, expectedSectionId);
        }

        resourceManager.setOnboardingJsonName(oldOnboardingName);
    }

    @Test
    public void testEligibilitySection() {
        List<Step> steps = checkOnboardingSteps(OnboardingSectionType.ELIGIBILITY, OnboardingTaskType.REGISTRATION);
        List<Step> expectedSteps = new ArrayList<>();
        expectedSteps.add(new ToggleFormStep("inclusionCriteria", null, null));
        expectedSteps.add(new InstructionStep("ineligibleInstruction", null, "Unfortunately, you are ineligible to join this study."));
        expectedSteps.add(new InstructionStep("eligibleInstruction", null, "You are eligible to join the study."));

        assertEquals(steps.size(), expectedSteps.size());
        for (int i = 0; i < expectedSteps.size(); i++) {
            assertEquals(steps.get(i).getIdentifier(), expectedSteps.get(i).getIdentifier());
            assertEquals(steps.get(i).getClass(), expectedSteps.get(i).getClass());
        }
    }

    @Test
    public void testLoginSection() {
        List<Step> steps = checkOnboardingSteps(OnboardingSectionType.LOGIN, OnboardingTaskType.LOGIN);
        assertEquals(steps.size(), 1);

        assertEquals(steps.get(0).getIdentifier(), "login");
    }

    List<Step> checkOnboardingSteps(OnboardingSectionType sectionType, OnboardingTaskType taskType) {
        OnboardingSection section = getSection(sectionType);
        assertNotNull(section);
        assertNotNull(mMockOnboardingManager);
        assertNotNull(mSurveyFactoryHelper);
        List<Step> steps = mMockOnboardingManager.steps(mSurveyFactoryHelper.mockContext, section, taskType);
        assertNotNull(steps);
        return steps;
    }

    OnboardingSection getSection(OnboardingSectionType sectionType) {
        if (mMockOnboardingManager.getSections() == null) {
            return null;
        }
        for (OnboardingSection section : mMockOnboardingManager.getSections()) {
            if (section.getOnboardingSectionType() == sectionType) {
                return section;
            }
        }
        return null;
    }

    void resetMockToDefaults() {
        mMockOnboardingManager.setHasPasscode(false);
        mMockOnboardingManager.setIsRegistered(false);
        mMockOnboardingManager.setIsLoginVerified(false);
    }

    class ShouldIncludeData {
        OnboardingSectionType sectionType;
        List<OnboardingTaskType> taskTypesIncluded;
        ShouldIncludeData(OnboardingSectionType type, OnboardingTaskType... taskTypes) {
            sectionType = type;
            taskTypesIncluded = Arrays.asList(taskTypes);
        }
        ShouldIncludeData(OnboardingSectionType type) {
            sectionType = type;
            taskTypesIncluded = new ArrayList<>();
        }
    }

    ShouldIncludeData findType(OnboardingSectionType type, ShouldIncludeData[] data) {
        for (ShouldIncludeData shouldIncludeData : data) {
            if (shouldIncludeData.sectionType == type) {
                return shouldIncludeData;
            }
        }
        return null;
    }
}
