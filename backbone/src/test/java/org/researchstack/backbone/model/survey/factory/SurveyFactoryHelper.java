package org.researchstack.backbone.model.survey.factory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSectionAdapter;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.onboarding.ResourceNameToStringConverter;

/**
 * Created by TheMDP on 1/6/17.
 */

public class SurveyFactoryHelper {
    public Gson gson;
    public Context mockContext;
    public MockResourceNameConverter converter;

    static final String PRIVACY_TITLE = "Privacy";
    static final String PRIVACY_LEARN_MORE = "Learn more about how your privacy and identity are protected";

    public SurveyFactoryHelper() {
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

        Mockito.when(mockContext.getString(R.string.rsb_consent))                 .thenReturn("Consent");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_welcome)) .thenReturn("Welcome");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_data_gathering))  .thenReturn("Data Gathering");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_privacy))  .thenReturn(PRIVACY_TITLE);
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_data_use)) .thenReturn("Data Use");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_time_commitment))   .thenReturn("Time Commitment");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_study_survey))      .thenReturn("Study Survey");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_study_tasks)) .thenReturn("Study Tasks");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_withdrawing)) .thenReturn("Withdrawing");

        Mockito.when(mockContext.getString(R.string.rsb_consent_learn_more)) .thenReturn("Learn More");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info)) .thenReturn("Learn more");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_data_gathering)) .thenReturn("Learn more about how data is gathered");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_data_use)) .thenReturn("Learn more about how data is gathered");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_privacy)) .thenReturn(PRIVACY_LEARN_MORE);

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_welcome)) .thenReturn("Learn more about the study first");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_study_survey)) .thenReturn("Learn more about the study survey");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_time_commitment)) .thenReturn("Learn more about the study\'s impact on your time");

        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_study_tasks)) .thenReturn("Learn more about the tasks involved");
        Mockito.when(mockContext.getString(R.string.rsb_consent_section_more_info_withdrawing)) .thenReturn("Learn more about withdrawing");

        converter = new MockResourceNameConverter();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
        builder.registerTypeAdapter(ConsentSection.class, new ConsentSectionAdapter(mockContext, converter));
        gson = builder.create();
    }

    class MockResourceNameConverter implements ResourceNameToStringConverter {

        @Override
        public String getJsonStringForResourceName(String resourceName) {
            return resourceName;
        }

        @Override
        public String getHtmlStringForResourceName(String resourceName) {
            return resourceName;
        }
    }
}
