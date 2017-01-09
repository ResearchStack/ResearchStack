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

/**
 * Created by TheMDP on 1/6/17.
 */

public class SurveyFactoryHelper {
    public Gson gson;
    public Context mockContext;

    public SurveyFactoryHelper() {
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
}
