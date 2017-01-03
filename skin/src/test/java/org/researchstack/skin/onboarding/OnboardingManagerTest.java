package org.researchstack.skin.onboarding;

import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.survey.ConsentReviewSurveyItem;
import org.researchstack.backbone.model.survey.ConsentSharingOptionsSurveyItem;
import org.researchstack.backbone.model.survey.InstructionSurveyItem;
import org.researchstack.backbone.model.survey.SingleChoiceTextQuestionSurveyItem;
import org.researchstack.backbone.model.survey.SubtaskQuestionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemType;
import org.researchstack.backbone.model.survey.ToggleQuestionSurveyItem;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.onboarding.OnboardingSectionType;
import org.researchstack.backbone.onboarding.ResourceNameJsonProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 1/3/17.
 */

public class OnboardingManagerTest {

    ResourceNameJsonProvider mFullResourceProvider;

    @Before
    public void setUp() throws Exception
    {
        mFullResourceProvider = new FullTestResourceProvider();
    }

    @Test
    public void testTestValidEmailAnswerFormat() throws Exception {
        OnboardingManager manager = OnboardingManager.createOnboardingManager("onboarding", mFullResourceProvider);

        assertNotNull(manager.sections);
        assertFalse(manager.sections.isEmpty());
        assertEquals(8, manager.sections.size());

        // Sections assertions
        assertEquals(OnboardingSectionType.LOGIN,               manager.sections.get(0).onboardingType);
        assertEquals(OnboardingSectionType.ELIGIBILITY,         manager.sections.get(1).onboardingType);
        assertEquals(OnboardingSectionType.CONSENT,             manager.sections.get(2).onboardingType);
        assertEquals(OnboardingSectionType.REGISTRATION,        manager.sections.get(3).onboardingType);
        assertEquals(OnboardingSectionType.PASSCODE,            manager.sections.get(4).onboardingType);
        assertEquals(OnboardingSectionType.EMAIL_VERIFICATION,  manager.sections.get(5).onboardingType);
        assertEquals(OnboardingSectionType.PERMISSIONS,         manager.sections.get(6).onboardingType);
        assertEquals(OnboardingSectionType.COMPLETION,          manager.sections.get(7).onboardingType);

        // Eligibility assertions
        OnboardingSection eligibilty = manager.sections.get(1);
        assertEquals(3, eligibilty.surveyItems.size());
        assertEquals(SurveyItemType.QUESTION_TOGGLE, eligibilty.surveyItems.get(0).type);
        assertEquals("eligibleInstruction",          eligibilty.surveyItems.get(0).skipIdentifier);
        assertTrue(eligibilty.surveyItems.get(0).skipIfPassed);
        assertEquals(3, eligibilty.surveyItems.get(0).items.size());
        assertTrue(eligibilty.surveyItems.get(0) instanceof ToggleQuestionSurveyItem);
        ToggleQuestionSurveyItem toggle = (ToggleQuestionSurveyItem) eligibilty.surveyItems.get(0);
        assertEquals(SurveyItemType.QUESTION_BOOLEAN, toggle.items.get(0).type);
        assertEquals(SurveyItemType.INSTRUCTION,     eligibilty.surveyItems.get(1).type);
        assertEquals(SurveyItemType.INSTRUCTION,     eligibilty.surveyItems.get(1).type);

        // Consent assertions
        OnboardingSection consent = manager.sections.get(2);
        assertEquals(8, consent.surveyItems.size());
        assertEquals(SurveyItemType.CUSTOM,         consent.surveyItems.get(0).type);
        assertEquals("reconsent.instruction",       consent.surveyItems.get(0).type.getValue());
        assertEquals(SurveyItemType.CONSENT_VISUAL, consent.surveyItems.get(1).type);
        assertEquals(SurveyItemType.SUBTASK,        consent.surveyItems.get(2).type);
        assertEquals(5,                             consent.surveyItems.get(2).items.size());

        assertTrue(consent.surveyItems.get(2) instanceof SubtaskQuestionSurveyItem);
        SubtaskQuestionSurveyItem consentQuiz = (SubtaskQuestionSurveyItem)consent.surveyItems.get(2);

        assertEquals(consentQuiz.items.get(1).type, SurveyItemType.QUESTION_SINGLE_CHOICE);
        SingleChoiceTextQuestionSurveyItem singleChoice = (SingleChoiceTextQuestionSurveyItem)consentQuiz.items.get(1);
        assertEquals(2, singleChoice.items.size());
        assertTrue(singleChoice.items.get(0) instanceof Choice);
        assertTrue(singleChoice.expectedAnswer);

        assertTrue(consent.surveyItems.get(3) instanceof InstructionSurveyItem);
        InstructionSurveyItem consentFailedQuiz = (InstructionSurveyItem)consent.surveyItems.get(3);
        assertEquals("consent_2quiz_headsup",   consentFailedQuiz.learnMoreHTMLContentURL);
        assertEquals("icon_retry",              consentFailedQuiz.image);

        assertEquals(SurveyItemType.INSTRUCTION_COMPLETION, consent.surveyItems.get(4).type);
        assertTrue(consent.surveyItems.get(4) instanceof InstructionSurveyItem);
        InstructionSurveyItem consentPassed = (InstructionSurveyItem)consent.surveyItems.get(4);
        assertEquals("You answered all of the questions correctly.", consentPassed.text);
        assertEquals("Great Job!", consentPassed.title);
        assertEquals("Tap Next to continue.", consentPassed.detailText);

        assertEquals(SurveyItemType.CONSENT_SHARING_OPTIONS, consent.surveyItems.get(5).type);
        assertTrue(consent.surveyItems.get(5) instanceof ConsentSharingOptionsSurveyItem);

        assertEquals(SurveyItemType.CONSENT_REVIEW, consent.surveyItems.get(6).type);
        assertTrue(consent.surveyItems.get(6) instanceof ConsentReviewSurveyItem);
    }

    class FullTestResourceProvider implements ResourceNameJsonProvider {

        @Override
        public String getJsonStringForResourceName(String resourceName) {
            // Resources are in src/test/resources
            InputStream jsonStream = getClass().getClassLoader().getResourceAsStream(resourceName+".json");
            String json = convertStreamToString(jsonStream);
            return json;
        }

        String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                assertTrue("Failed to read stream", false);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    assertTrue("Failed to read stream", false);
                }
            }
            return sb.toString();
        }
    }
}
