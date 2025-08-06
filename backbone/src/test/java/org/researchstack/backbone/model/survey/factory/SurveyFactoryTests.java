package org.researchstack.backbone.model.survey.factory;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import android.text.InputType;

import com.google.gson.reflect.TypeToken;

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
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.EmailAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.answerformat.PasswordAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.onboarding.MockResourceManager;
import org.researchstack.backbone.onboarding.ReConsentInstructionStep;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentReviewSubstepListStep;
import org.researchstack.backbone.step.ConsentSharingStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.LoginStep;
import org.researchstack.backbone.step.NavigationFormStep;
import org.researchstack.backbone.step.NavigationSubtaskStep;
import org.researchstack.backbone.step.PasscodeStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.ProfileStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 1/5/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FingerprintManagerCompat.class, ResourcePathManager.class, ResourceManager.class})
public class SurveyFactoryTests {

    private SurveyFactoryHelper helper;
    @Mock FingerprintManagerCompat mockFingerprintManager;

    @Before
    public void setUp() throws Exception
    {
        helper = new SurveyFactoryHelper();

        // All of this, along with the @PrepareForTest and @RunWith above, is needed
        // to mock the resource manager to load resources from the directory src/test/resources
        PowerMockito.mockStatic(ResourcePathManager.class);
        PowerMockito.mockStatic(ResourceManager.class);
        MockResourceManager mockManager = new MockResourceManager();
        PowerMockito.when(ResourceManager.getInstance()).thenReturn(mockManager);
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_factory_eligibilityrequirements");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_factory_onboarding");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_factory_consent");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "consentdocument");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "custom_consentdocument");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_item_textfield");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_item_compound");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_item_email");
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "survey_item_integerrange");

        mockFingerprintManager = Mockito.mock(FingerprintManagerCompat.class);
        Mockito.when(mockFingerprintManager.isHardwareDetected()).thenReturn(true);
        PowerMockito.mockStatic(FingerprintManagerCompat.class);
        Mockito.when(FingerprintManagerCompat.from(helper.mockContext)).thenReturn(mockFingerprintManager);
    }

    private String getJsonResource(String resourceName) {
        ResourcePathManager.Resource resource = ResourceManager.getInstance().getResource(resourceName);
        return ResourceManager.getResourceAsString(helper.mockContext, resourceName);
    }

    @Test
    public void testEligibilitySurveyFactory() {
        Type listType = new TypeToken<List<SurveyItem>>() {}.getType();
        String eligibilityJson = getJsonResource("survey_factory_eligibilityrequirements");
        List<SurveyItem> surveyItemList = helper.gson.fromJson(eligibilityJson, listType);

        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, surveyItemList);

        assertNotNull(stepList);
        assertTrue(stepList.size() > 0);
        assertEquals(3, stepList.size());

        assertTrue(stepList.get(0) instanceof NavigationFormStep);
        NavigationFormStep quizStep = (NavigationFormStep) stepList.get(0);
        assertEquals("eligibleInstruction", quizStep.getSkipToStepIdentifier());
        assertTrue(quizStep.getSkipIfPassed());

        assertEquals(3, quizStep.getFormSteps().size());
        QuestionStep quizStep1 = quizStep.getFormSteps().get(0);
        assertEquals("Are you 18 or older?", quizStep1.getText());
        assertTrue(quizStep1.getAnswerFormat() instanceof BooleanAnswerFormat);
        BooleanAnswerFormat quizStep1Format = (BooleanAnswerFormat) quizStep1.getAnswerFormat();
        assertEquals(2, quizStep1Format.getChoices().length);
        assertEquals("Yes", (quizStep1Format.getChoices()[0]).getText());
        assertEquals(true, (quizStep1Format.getChoices()[0]).getValue());
        assertEquals("No", (quizStep1Format.getChoices()[1]).getText());
        assertEquals(false, (quizStep1Format.getChoices()[1]).getValue());

        assertTrue(stepList.get(1) instanceof InstructionStep);
        InstructionStep inEligibleStep = (InstructionStep) stepList.get(1);
        assertEquals("ineligibleInstruction", inEligibleStep.getIdentifier());
        assertEquals("logo", inEligibleStep.getIconImage());

        assertTrue(stepList.get(2) instanceof InstructionStep);
        InstructionStep eligibleStep = (InstructionStep) stepList.get(2);
        assertEquals("eligibleInstruction", eligibleStep.getIdentifier());
        assertEquals("You are eligible to join the study.", eligibleStep.getText());
    }

    @Test
    public void testSurveyFactory()
    {
        Type listType = new TypeToken<List<SurveyItem>>() {}.getType();
        String eligibilityJson = getJsonResource("survey_factory_onboarding");
        List<SurveyItem> surveyItemList = helper.gson.fromJson(eligibilityJson, listType);

        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, surveyItemList);

        assertNotNull(stepList);
        assertTrue(stepList.size() > 0);
        assertEquals(7, stepList.size());

        assertTrue(stepList.get(0) instanceof LoginStep);
        assertEquals("login", stepList.get(0).getIdentifier());
        LoginStep loginStep = (LoginStep) stepList.get(0);
        assertEquals(2, loginStep.getProfileInfoOptions().size());
        assertEquals(ProfileInfoOption.EMAIL,    loginStep.getProfileInfoOptions().get(0));
        assertEquals(ProfileInfoOption.PASSWORD, loginStep.getProfileInfoOptions().get(1));
        assertEquals(2, loginStep.getFormSteps().size());
        assertTrue(loginStep.getFormSteps().get(0).getAnswerFormat() instanceof EmailAnswerFormat);
        assertTrue(loginStep.getFormSteps().get(1).getAnswerFormat() instanceof PasswordAnswerFormat);

        assertTrue(stepList.get(1) instanceof RegistrationStep);
        assertEquals("registration", stepList.get(1).getIdentifier());
        RegistrationStep registrationStep = (RegistrationStep) stepList.get(1);
        assertEquals(3, registrationStep.getProfileInfoOptions().size());
        assertEquals(ProfileInfoOption.NAME, registrationStep.getProfileInfoOptions().get(0));
        assertEquals(ProfileInfoOption.EMAIL, registrationStep.getProfileInfoOptions().get(1));
        assertEquals(ProfileInfoOption.PASSWORD, registrationStep.getProfileInfoOptions().get(2));
        // Must have password and confirm password fields
        assertEquals(4, registrationStep.getFormSteps().size());
        assertTrue(registrationStep.getFormSteps().get(0).getAnswerFormat() instanceof TextAnswerFormat);
        assertTrue(registrationStep.getFormSteps().get(1).getAnswerFormat() instanceof EmailAnswerFormat);
        assertTrue(registrationStep.getFormSteps().get(2).getAnswerFormat() instanceof PasswordAnswerFormat);
        assertEquals(ProfileInfoOption.PASSWORD.getIdentifier(), registrationStep.getFormSteps().get(2).getIdentifier());
        assertTrue(registrationStep.getFormSteps().get(3).getAnswerFormat() instanceof PasswordAnswerFormat);
        assertEquals(SurveyFactory.PASSWORD_CONFIRMATION_IDENTIFIER, registrationStep.getFormSteps().get(3).getIdentifier());

        assertTrue(stepList.get(2) instanceof PasscodeStep);
        assertEquals("passcode", stepList.get(2).getIdentifier());

        assertTrue(stepList.get(3) instanceof EmailVerificationStep);
        assertEquals("emailVerification", stepList.get(3).getIdentifier());

        assertTrue(stepList.get(4) instanceof PermissionsStep);
        assertEquals("permissions", stepList.get(4).getIdentifier());

        assertTrue(stepList.get(5) instanceof InstructionStep);
        assertEquals("onboardingCompletion", stepList.get(5).getIdentifier());

        // This doesn't usually happen *after* onboarding completion (and never with login w/ email
        // and password), but for the sake of this test, we're adding it here.
        assertTrue(stepList.get(6) instanceof LoginStep);
        assertEquals("externalID", stepList.get(6).getIdentifier());
        LoginStep externalIdLoginStep = (LoginStep) stepList.get(6);
        assertEquals(1, externalIdLoginStep.getProfileInfoOptions().size());
        assertEquals(ProfileInfoOption.EXTERNAL_ID, externalIdLoginStep.getProfileInfoOptions()
                .get(0));
        assertEquals(1, externalIdLoginStep.getFormSteps().size());
        assertTrue(externalIdLoginStep.getFormSteps().get(0).getAnswerFormat()
                instanceof TextAnswerFormat);
        assertEquals(SurveyFactory.EXTERNAL_ID_MAX_LENGTH, ((TextAnswerFormat) externalIdLoginStep
                .getFormSteps().get(0).getAnswerFormat()).getMaximumLength());
    }

    @Test
    public void testConsentDocumentFactory()
    {
        String consentDocJson = getJsonResource("consentdocument");
        ConsentDocument consentDoc = helper.gson.fromJson(consentDocJson, ConsentDocument.class);

        Type listType = new TypeToken<List<SurveyItem>>() {}.getType();
        String consentItemsJson = getJsonResource("survey_factory_consent");
        List<SurveyItem> surveyItemList = helper.gson.fromJson(consentItemsJson, listType);

        ConsentDocumentFactory factory = new ConsentDocumentFactory(consentDoc);
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, surveyItemList);

        assertNotNull(stepList);
        // 19 consent visual steps, 8 other steps
        assertEquals(stepList.size(), 8);

        assertTrue(stepList.get(0) instanceof ReConsentInstructionStep);
        ReConsentInstructionStep customStep = (ReConsentInstructionStep)stepList.get(0);
        assertEquals("reconsentIntroduction", customStep.getIdentifier());

        // Steps 1 are Visual Consent Steps
        assertTrue(stepList.get(1) instanceof SubtaskStep);

        assertTrue(stepList.get(2) instanceof NavigationSubtaskStep);
        NavigationSubtaskStep quizStep = (NavigationSubtaskStep)stepList.get(2);
        assertEquals("consentPassedQuiz", quizStep.getSkipToStepIdentifier());
        assertTrue(quizStep.getSkipIfPassed());

        assertTrue(stepList.get(3) instanceof InstructionStep);
        assertEquals("consentFailedQuiz", stepList.get(3).getIdentifier());

        assertTrue(stepList.get(4) instanceof InstructionStep);
        assertEquals("consentPassedQuiz", stepList.get(4).getIdentifier());

        assertTrue(stepList.get(5) instanceof ConsentSharingStep);
        ConsentSharingStep sharingStep = (ConsentSharingStep)stepList.get(5);
        assertEquals("consentSharingOptions", sharingStep.getIdentifier());
        assertTrue(sharingStep.getAnswerFormat() instanceof ChoiceAnswerFormat);
        ChoiceAnswerFormat sharingFormat = (ChoiceAnswerFormat)sharingStep.getAnswerFormat();
        assertEquals("Yes. Share my coded study data with qualified researchers worldwide.", (sharingFormat.getChoices()[0]).getText());
        assertEquals(true, (sharingFormat.getChoices()[0]).getValue());

        assertTrue(stepList.get(6) instanceof ConsentReviewSubstepListStep);

        ConsentReviewSubstepListStep substepListStep = (ConsentReviewSubstepListStep) stepList.get(6);
        ProfileStep consentProfileStep = (ProfileStep)substepListStep.getStepList().get(0);
        assertEquals(ProfileInfoOption.NAME, consentProfileStep.getProfileInfoOptions().get(0));
        assertEquals(ProfileInfoOption.BIRTHDATE, consentProfileStep.getProfileInfoOptions().get(1));

        assertTrue(substepListStep.getStepList().get(1) instanceof ConsentSignatureStep);
        assertTrue(substepListStep.getStepList().get(2) instanceof ConsentDocumentStep);

        assertTrue(stepList.get(7) instanceof InstructionStep);
        assertEquals("consentCompletion", stepList.get(7).getIdentifier());
    }

    @Test
    public void testCustomConsentDocument()
    {
        String consentDocJson = getJsonResource("custom_consentdocument");
        ConsentDocument consentDoc = helper.gson.fromJson(consentDocJson, ConsentDocument.class);

        assertEquals(consentDoc.getSections().size(), 4);

        assertEquals(ConsentSection.Type.DataGathering, consentDoc.getSections().get(0).getType());
        assertEquals("Overridden Title", consentDoc.getSections().get(0).getTitle());
        assertEquals("Overridden More Title", consentDoc.getSections().get(0).getCustomLearnMoreButtonTitle());
        assertEquals("image1", consentDoc.getSections().get(0).getCustomImageName());

        assertEquals(ConsentSection.Type.Privacy, consentDoc.getSections().get(1).getType());
        assertEquals(SurveyFactoryHelper.PRIVACY_TITLE, consentDoc.getSections().get(1).getTitle());
        assertEquals(SurveyFactoryHelper.PRIVACY_LEARN_MORE, consentDoc.getSections().get(1).getCustomLearnMoreButtonTitle());
        assertEquals("rsb_consent_section_privacy", consentDoc.getSections().get(1).getCustomImageName());

        assertEquals(ConsentSection.Type.Custom, consentDoc.getSections().get(2).getType());
        assertEquals("custom_step_identifier", consentDoc.getSections().get(2).getTypeIdentifier());
        assertEquals("Overridden Title", consentDoc.getSections().get(2).getTitle());
        assertEquals("Overridden More Title", consentDoc.getSections().get(2).getCustomLearnMoreButtonTitle());
        assertEquals("image1", consentDoc.getSections().get(2).getCustomImageName());

        assertEquals(ConsentSection.Type.Custom, consentDoc.getSections().get(3).getType());
        assertEquals("custom_step_identifier2", consentDoc.getSections().get(3).getTypeIdentifier());
    }

    @Test
    public void testSurveyItem_textfield() {
        String textfieldJson = getJsonResource("survey_item_textfield");
        SurveyItem surveyItem = helper.gson.fromJson(textfieldJson, SurveyItem.class);
        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, Collections.singletonList(surveyItem));

        assertNotNull(stepList);
        assertEquals(1, stepList.size());

        assertTrue(stepList.get(0) instanceof QuestionStep);
        QuestionStep step = (QuestionStep)stepList.get(0);
        assertEquals("birthdate_month", step.getIdentifier());
        assertEquals("Month", step.getTitle());

        assertTrue(step.getAnswerFormat() instanceof TextAnswerFormat);
        TextAnswerFormat format = (TextAnswerFormat)step.getAnswerFormat();
        assertEquals("^[0-9]*$", format.validationRegex());
        assertEquals(3, format.getMaximumLength());
        assertEquals(InputType.TYPE_CLASS_NUMBER, format.getInputType());
    }

    @Test
    public void testSurveyItem_integerRange() {
        String textfieldJson = getJsonResource("survey_item_integerrange");
        SurveyItem surveyItem = helper.gson.fromJson(textfieldJson, SurveyItem.class);
        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, Collections.singletonList(surveyItem));

        assertNotNull(stepList);
        assertEquals(1, stepList.size());

        assertTrue(stepList.get(0) instanceof QuestionStep);
        QuestionStep step = (QuestionStep)stepList.get(0);
        assertEquals("weight", step.getIdentifier());

        assertTrue(step.getAnswerFormat() instanceof IntegerAnswerFormat);
        IntegerAnswerFormat format = (IntegerAnswerFormat)step.getAnswerFormat();
        assertEquals(50, format.getMinValue());
        assertEquals(500, format.getMaxValue());
        assertEquals(3, format.getMaximumLength());
        assertEquals(InputType.TYPE_CLASS_NUMBER, format.getInputType());
    }

    @Test
    public void testSurveyItem_form() {
        String json = getJsonResource("survey_item_compound");
        SurveyItem surveyItem = helper.gson.fromJson(json, SurveyItem.class);
        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, Collections.singletonList(surveyItem));

        assertNotNull(stepList);
        assertEquals(1, stepList.size());

        String expectedStepId = "walking_q1";
        assertTrue(stepList.get(0) instanceof NavigationFormStep);
        NavigationFormStep step = (NavigationFormStep)stepList.get(0);
        assertEquals(expectedStepId, step.getIdentifier());
        assertEquals(1, step.getFormSteps().size());
        assertTrue(step.getFormSteps().get(0).getAnswerFormat() instanceof IntegerAnswerFormat);

        assertEquals("No walking", step.getSkipTitle());
        assertEquals("sitting_instruction", step.getSkipToStepIdentifier());

        TaskResult taskResult = new TaskResult("task_id");
        assertEquals("sitting_instruction", step.nextStepIdentifier(taskResult, null));
        StepResult<String> stepResult = new StepResult<>(new Step(expectedStepId));
        taskResult.getResults().put(expectedStepId, stepResult);
        assertEquals("sitting_instruction", step.nextStepIdentifier(taskResult, null));
        stepResult.setResult("AnyResult");
        taskResult.getResults().put(expectedStepId, stepResult);
        assertNull(step.nextStepIdentifier(taskResult, null));
    }

    @Test
    public void testSurveyItemEmail() {
        String json = getJsonResource("survey_item_email");
        SurveyItem surveyItem = helper.gson.fromJson(json, SurveyItem.class);
        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, Collections.singletonList(surveyItem));

        assertNotNull(stepList);
        assertEquals(1, stepList.size());

        String expectedStepId = "email";
        assertTrue(stepList.get(0) instanceof QuestionStep);
        QuestionStep step = (QuestionStep)stepList.get(0);
        assertEquals(expectedStepId, step.getIdentifier());
        assertTrue(step.getAnswerFormat() instanceof EmailAnswerFormat);
        assertEquals("Personal Email", step.getTitle());
    }
}
