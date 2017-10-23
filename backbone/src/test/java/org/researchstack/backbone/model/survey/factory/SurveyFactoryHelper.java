package org.researchstack.backbone.model.survey.factory;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSectionAdapter;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.onboarding.OnboardingManager;

/**
 * Created by TheMDP on 1/6/17.
 *
 * The SurveyFactorHelper can be used to help the SurveyFactory, ConsentDocumentFactory, etc
 * It provides a mock Context with resources in it, along with
 */

public class SurveyFactoryHelper {
    public Gson gson;
    @Mock public Context mockContext;
    @Mock private Resources mockResources;

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

        // All the strings that the TremorTask uses
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_FINISHED_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_1_DETAIL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DEFAULT_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_LEFT_HAND_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_RIGHT_HAND_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_IN_LAP_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_in_lap_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_EXTEND_ARM_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_extend_arm_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_BEND_ARM_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_bend_arm_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TOUCH_NOSE_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_touch_nose_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_TURN_WRIST_INTRO_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_active_step_turn_wrist_instruction_ld)).thenReturn("%1$d");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_COMPLETED_INSTRUCTION)).thenReturn("Activity Completed");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_SWITCH_HANDS_RIGHT_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_ACTIVE_STEP_SWITCH_HANDS_LEFT_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_1_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_2_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_3_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_4_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_INTRO_2_DETAIL_5_TASK)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_skip_question_both_hands)).thenReturn("%1$s");
        Mockito.when(mockContext.getString(R.string.rsb_tremor_test_intro_2_detail_default)).thenReturn("%1$s");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_SKIP_RIGHT_HAND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_SKIP_LEFT_HAND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_SKIP_NEITHER)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TREMOR_TEST_COMPLETED_INSTRUCTION)).thenReturn("");

        // All the strings that the Tapping Task uses
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_TASK_TITLE_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_TASK_TITLE_LEFT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_REST_PHONE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_RIGHT_FIRST)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_LEFT_FIRST)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_RIGHT_SECOND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_LEFT_SECOND)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_CALL_TO_ACTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_CALL_TO_ACTION_NEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INSTRUCTION_RIGHT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TAPPING_INTRO_TEXT_2_FORMAT)).thenReturn("Keep tapping for %1$s.");

        Mockito.when(mockContext.getString(R.string.rsb_time_minutes)).thenReturn("minutes");
        Mockito.when(mockContext.getString(R.string.rsb_time_seconds)).thenReturn("seconds");

        // All the strings that the Walking Task uses
        Mockito.when(mockContext.getString(R.string.rsb_WALK_TASK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_walk_intro_2_text_ld)).thenReturn("Find a place where you can safely walk unassisted for about %1$d steps in a straight line.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_DETAIL)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_OUTBOUND_INSTRUCTION_FORMAT)).thenReturn("Walk up to %1$d steps in a straight line.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_RETURN_INSTRUCTION_FORMAT)).thenReturn("Walk up to %1$d steps in a straight line.");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT)).thenReturn("Now stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_INSTRUCTION_FORMAT)).thenReturn("Stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_STAND_VOICE_INSTRUCTION_FORMAT)).thenReturn("Stand still for %1$s.");

        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TEXT)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_time_minutes)).thenReturn("minutes");
        Mockito.when(mockContext.getString(R.string.rsb_time_seconds)).thenReturn("seconds");

        Mockito.when(mockContext.getString(R.string.rsb_WALK_BACK_AND_FORTH_FINISHED_VOICE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_BACK_AND_FORTH_STAND_INSTRUCTION_FORMAT)).thenReturn("Turn in a full circle and then stand still for %1$s.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_BACK_AND_FORTH_INSTRUCTION_FORMAT)).thenReturn("Walk back and forth in a straight line for %1$s. Walk as you would normally.");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_TEXT_BACK_AND_FORTH_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_WALK_INTRO_2_DETAIL_BACK_AND_FORTH_INSTRUCTION)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_distance_meters)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_distance_feet)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_BOOL_YES)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_BOOL_NO)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_permission_location_title)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_permission_location_desc)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_system_feature_gps_title)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_system_feature_gps_text)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INTRO_DETAIL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_2)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_3)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_4)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_5)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_CHOICE_6)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_QUESTION_2_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_FORM_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_FORM_TEXT)).thenReturn("");

        Mockito.when(mockContext.getString(R.string.rsb_timed_walk_intro_2_text)).thenReturn("walk for about %1$s");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INTRO_2_DETAIL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_timed_walk_instruction)).thenReturn("Walk up to %1$s");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_TURN)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TIMED_WALK_INSTRUCTION_2)).thenReturn("");

        // All the strings that the Audio Task uses
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_TASK_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_INTENDED_USE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_INTRO_TEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_CALL_TO_ACTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_LEVEL_CHECK_LABEL)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_TOO_LOUD_MESSAGE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_TOO_LOUD_ACTION_NEXT)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_AUDIO_INSTRUCTION)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TITLE)).thenReturn("");
        Mockito.when(mockContext.getString(R.string.rsb_TASK_COMPLETE_TEXT)).thenReturn("");

        mockResources = Mockito.mock(Resources.class);
        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TaskItem.class, new TaskItemAdapter());
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
        builder.registerTypeAdapter(ConsentSection.class, new ConsentSectionAdapter(new OnboardingManager.AdapterContextProvider() {
            @Override
            public Context getContext() {
                return mockContext;
            }
        }));
        gson = builder.create();
    }
}
