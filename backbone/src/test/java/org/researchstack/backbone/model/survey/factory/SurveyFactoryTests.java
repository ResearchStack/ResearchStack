package org.researchstack.backbone.model.survey.factory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.EmailAnswerFormat;
import org.researchstack.backbone.answerformat.PasswordAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.ConsentDocument;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSectionAdapter;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSharingStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.CustomStep;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.LoginStep;
import org.researchstack.backbone.step.PasscodeStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.ProfileStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.ToggleFormStep;
import org.researchstack.backbone.step.navigation.NavigationSubtaskStep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by TheMDP on 1/5/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SurveyFactoryTests {

    Gson gson;
    Context mockContext;

    @Before
    public void setUp() throws Exception
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
        builder.registerTypeAdapter(ConsentSection.class, new ConsentSectionAdapter());
        gson = builder.create();

        mockContext = Mockito.mock(Context.class);
        Mockito.when(mockContext.getString(R.string.rsb_yes))       .thenReturn("Yes");
        Mockito.when(mockContext.getString(R.string.rsb_no))        .thenReturn("No");
        Mockito.when(mockContext.getString(R.string.rsb_not_sure))  .thenReturn("Not sure");

        Mockito.when(mockContext.getString(R.string.rsb_name))              .thenReturn("Name");
        Mockito.when(mockContext.getString(R.string.rsb_name_placeholder))  .thenReturn("Enter full name");

        Mockito.when(mockContext.getString(R.string.rsb_email))             .thenReturn("Email");
        Mockito.when(mockContext.getString(R.string.rsb_email_placeholder)) .thenReturn("jappleseed@example.com");

        Mockito.when(mockContext.getString(R.string.rsb_password))              .thenReturn("Password");
        Mockito.when(mockContext.getString(R.string.rsb_password_placeholder))  .thenReturn("Enter password");

        Mockito.when(mockContext.getString(R.string.rsb_confirm_password))              .thenReturn("Confirm");
        Mockito.when(mockContext.getString(R.string.rsb_confirm_password_placeholder))  .thenReturn("Enter password again");

        Mockito.when(mockContext.getString(R.string.rsb_birthdate))             .thenReturn("Date of Birth");
        Mockito.when(mockContext.getString(R.string.rsb_birthdate_placeholder)) .thenReturn("Pick a date");

        Mockito.when(mockContext.getString(R.string.rsb_birthdate))             .thenReturn("Gender");
        Mockito.when(mockContext.getString(R.string.rsb_birthdate_placeholder)) .thenReturn("Pick a gender");

        Mockito.when(mockContext.getString(R.string.rsb_gender_male))   .thenReturn("Male");
        Mockito.when(mockContext.getString(R.string.rsb_gender_female)) .thenReturn("Female");
        Mockito.when(mockContext.getString(R.string.rsb_gender_other))  .thenReturn("Other");
    }

    @Test
    public void testEligibilitySurveyFactory() {
        Type listType = new TypeToken<List<SurveyItem>>() {
        }.getType();
        String eligibilityJson = getJsonStringForResourceName("eligibilityrequirements");
        List<SurveyItem> surveyItemList = gson.fromJson(eligibilityJson, listType);

        SurveyFactory factory = new SurveyFactory(mockContext, surveyItemList);

        assertNotNull(factory.getSteps());
        assertTrue(factory.getSteps().size() > 0);
        assertEquals(3, factory.getSteps().size());

        assertTrue(factory.getSteps().get(0) instanceof ToggleFormStep);
        ToggleFormStep quizStep = (ToggleFormStep) factory.getSteps().get(0);
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

        assertTrue(factory.getSteps().get(1) instanceof InstructionStep);
        InstructionStep inEligibleStep = (InstructionStep) factory.getSteps().get(1);
        assertEquals("ineligibleInstruction", inEligibleStep.getIdentifier());
        assertEquals("logo", inEligibleStep.getIconImage());

        assertTrue(factory.getSteps().get(2) instanceof InstructionStep);
        InstructionStep eligibleStep = (InstructionStep) factory.getSteps().get(2);
        assertEquals("eligibleInstruction", eligibleStep.getIdentifier());
        assertEquals("You are eligible to join the study.", eligibleStep.getText());
    }

    @Test
    public void testSurveyFactory()
    {
        Type listType = new TypeToken<List<SurveyItem>>() {
        }.getType();
        String eligibilityJson = getJsonStringForResourceName("onboarding");
        List<SurveyItem> surveyItemList = gson.fromJson(eligibilityJson, listType);

        SurveyFactory factory = new SurveyFactory(mockContext, surveyItemList);

        assertNotNull(factory.getSteps());
        assertTrue(factory.getSteps().size() > 0);
        assertEquals(6, factory.getSteps().size());

        assertTrue(factory.getSteps().get(0) instanceof LoginStep);
        assertEquals("login", factory.getSteps().get(0).getIdentifier());
        LoginStep loginStep = (LoginStep) factory.getSteps().get(0);
        assertEquals(2, loginStep.getProfileInfoOptions().size());
        assertEquals(ProfileInfoOption.EMAIL,    loginStep.getProfileInfoOptions().get(0));
        assertEquals(ProfileInfoOption.PASSWORD, loginStep.getProfileInfoOptions().get(1));
        assertEquals(2, loginStep.getFormSteps().size());
        assertTrue(loginStep.getFormSteps().get(0).getAnswerFormat() instanceof EmailAnswerFormat);
        assertTrue(loginStep.getFormSteps().get(1).getAnswerFormat() instanceof PasswordAnswerFormat);

        assertTrue(factory.getSteps().get(1) instanceof RegistrationStep);
        assertEquals("registration", factory.getSteps().get(1).getIdentifier());
        RegistrationStep registrationStep = (RegistrationStep) factory.getSteps().get(1);
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

        assertTrue(factory.getSteps().get(2) instanceof PasscodeStep);
        assertEquals("passcode", factory.getSteps().get(2).getIdentifier());

        assertTrue(factory.getSteps().get(3) instanceof EmailVerificationStep);
        assertEquals("emailVerification", factory.getSteps().get(3).getIdentifier());

        assertTrue(factory.getSteps().get(4) instanceof PermissionsStep);
        assertEquals("permissions", factory.getSteps().get(4).getIdentifier());

        assertTrue(factory.getSteps().get(5) instanceof InstructionStep);
        assertEquals("onboardingCompletion", factory.getSteps().get(5).getIdentifier());
    }

    @Test
    public void testConsentDocumentFactory()
    {
        String consentDocJson = getJsonStringForResourceName("consentdocument");
        ConsentDocument consentDoc = gson.fromJson(consentDocJson, ConsentDocument.class);

        Type listType = new TypeToken<List<SurveyItem>>() {
        }.getType();
        String consentItemsJson = getJsonStringForResourceName("consent");
        List<SurveyItem> surveyItemList = gson.fromJson(consentItemsJson, listType);

        ConsentDocumentFactory factory = new ConsentDocumentFactory(mockContext, surveyItemList, consentDoc);

        assertNotNull(factory.getSteps());
        assertTrue(factory.getSteps().size() > 0);

        // 19 consent visual steps, 8 other steps
        assertEquals(27, factory.getSteps().size());

        assertTrue(factory.getSteps().get(0) instanceof CustomStep);
        CustomStep customStep = (CustomStep)factory.getSteps().get(0);
        assertEquals("reconsentIntroduction", customStep.getIdentifier());
        assertEquals("reconsent.instruction", customStep.getCustomTypeIdentifier());

        // Steps 1-18 are Visual Consent Steps
        for (int i = 1; i <= 18; i++) {
            assertTrue(factory.getSteps().get(i) instanceof ConsentVisualStep);
        }

        assertTrue(factory.getSteps().get(19) instanceof NavigationSubtaskStep);
        NavigationSubtaskStep quizStep = (NavigationSubtaskStep)factory.getSteps().get(19);
        assertEquals("consentPassedQuiz", quizStep.getSkipToStepIdentifier());
        assertTrue(quizStep.getSkipIfPassed());

        assertTrue(factory.getSteps().get(20) instanceof InstructionStep);
        assertEquals("consentFailedQuiz", factory.getSteps().get(20).getIdentifier());

        assertTrue(factory.getSteps().get(21) instanceof InstructionStep);
        assertEquals("consentPassedQuiz", factory.getSteps().get(21).getIdentifier());

        assertTrue(factory.getSteps().get(22) instanceof ConsentSharingStep);
        ConsentSharingStep sharingStep = (ConsentSharingStep)factory.getSteps().get(22);
        assertEquals("consentSharingOptions", sharingStep.getIdentifier());
        assertTrue(sharingStep.getAnswerFormat() instanceof ChoiceAnswerFormat);
        ChoiceAnswerFormat sharingFormat = (ChoiceAnswerFormat)sharingStep.getAnswerFormat();
        assertEquals("Yes. Share my coded study data with qualified researchers worldwide.", (sharingFormat.getChoices()[0]).getText());
        assertEquals(true, (sharingFormat.getChoices()[0]).getValue());

        assertTrue(factory.getSteps().get(23) instanceof ProfileStep);
        ProfileStep consentProfileStep = (ProfileStep) factory.getSteps().get(23);
        assertEquals(ProfileInfoOption.NAME, consentProfileStep.getProfileInfoOptions().get(0));
        assertEquals(ProfileInfoOption.BIRTHDATE, consentProfileStep.getProfileInfoOptions().get(1));

        assertTrue(factory.getSteps().get(24) instanceof ConsentSignatureStep);

        assertTrue(factory.getSteps().get(25) instanceof ConsentDocumentStep);
        ConsentDocumentStep documentStep = (ConsentDocumentStep)factory.getSteps().get(25);
        assertEquals("consent_full", documentStep.getConsentHTML());

        assertTrue(factory.getSteps().get(26) instanceof InstructionStep);
        assertEquals("consentCompletion", factory.getSteps().get(26).getIdentifier());
    }

    String getJsonStringForResourceName(String resourceName) {
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
