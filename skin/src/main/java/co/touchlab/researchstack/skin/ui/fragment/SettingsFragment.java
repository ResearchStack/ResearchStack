package co.touchlab.researchstack.skin.ui.fragment;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.answerformat.AnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.backbone.helpers.LogExt;
import co.touchlab.researchstack.backbone.model.Choice;
import co.touchlab.researchstack.backbone.model.ConsentSectionModel;
import co.touchlab.researchstack.backbone.result.TaskResult;
import co.touchlab.researchstack.backbone.step.ConsentSharingStep;
import co.touchlab.researchstack.backbone.task.OrderedTask;
import co.touchlab.researchstack.backbone.task.Task;
import co.touchlab.researchstack.backbone.ui.ViewTaskActivity;
import co.touchlab.researchstack.backbone.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.backbone.utils.FormatHelper;
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.glue.BuildConfig;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.AppPrefs;
import co.touchlab.researchstack.skin.DataProvider;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.model.User;
import co.touchlab.researchstack.skin.step.PassCodeCreationStep;
import co.touchlab.researchstack.skin.task.ConsentTask;
import co.touchlab.researchstack.skin.ui.OnboardingActivity;
import co.touchlab.researchstack.skin.ui.ViewLicensesActivity;
import co.touchlab.researchstack.skin.ui.layout.SignUpPinCodeCreationStepLayout;
import co.touchlab.researchstack.skin.utils.ConsentFormUtils;
import co.touchlab.researchstack.skin.utils.JsonUtils;
import rx.Observable;

/**
 * TODO Try and point to a single instance of a key instead of defining them in XML and in code.
 * TODO Version text currently points to ResearchStack, implement a way of getting version of SampleApp
 * TODO Implement screens for all items in {@link #onPreferenceTreeClick}
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final int REQUEST_CODE_SHARING_OPTIONS = 0;
    private static final int REQUEST_CODE_CHANGE_PASSCODE = 1;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Settings Keys.
    // If you are adding / changing settings, make sure they are unique / match in settings.xml
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Profile
    public static final String KEY_PROFILE           = "settings_profile";
    public static final String KEY_PROFILE_NAME      = "settings_profile_name";
    public static final String KEY_PROFILE_BIRTHDATE = "settings_profile_birthdate";
    // Reminders
    public static final String KEY_REMINDERS         = "settings_reminders";
    public static final String KEY_REMINDERS_TIME    = "settings_reminders_time";
    // Privacy
    public static final String KEY_PRIVACY           = "settings_privacy";
    public static final String KEY_PRIVACY_POLICY    = "settings_privacy_policy";
    public static final String KEY_REVIEW_CONSENT    = "settings_privacy_review_consent";
    public static final String KEY_SHARING_OPTIONS   = "settings_privacy_sharing_options";
    // Security
    public static final String KEY_AUTO_LOCK_ENABLED = "settings_auto_lock_on_exit";
    public static final String KEY_AUTO_LOCK_TIME    = "settings_auto_lock_time";
    public static final String KEY_CHANGE_PASSCODE = "settings_security_change_passcode";
    // General
    public static final String KEY_GENERAL = "settings_general";
    public static final String KEY_LICENSE_INFORMATION = "settings_general_license_information";
    public static final String KEY_LEAVE_STUDY = "settings_general_leave_study";
    public static final String KEY_JOIN_STUDY = "settings_general_join_study";
    // Other
    public static final String KEY_VERSION = "settings_version";

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Preference Items
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private PreferenceCategory profileCategory;
    private PreferenceCategory privacyCategory;
    private Preference sharingScope;
    private PreferenceCategory generalCategory;
    private Preference leaveStudy;
    private Preference joinStudy;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Field Vars
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private ConsentSectionModel data;
    private View progress;

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.addPreferencesFromResource(R.xml.settings);

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();
        screen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        profileCategory = (PreferenceCategory) screen.findPreference(KEY_PROFILE);

        privacyCategory = (PreferenceCategory) screen.findPreference(KEY_PRIVACY);
        sharingScope = privacyCategory.findPreference(KEY_SHARING_OPTIONS);

        generalCategory = (PreferenceCategory) screen.findPreference(KEY_GENERAL);
        leaveStudy = generalCategory.findPreference(KEY_LEAVE_STUDY);
        joinStudy = generalCategory.findPreference(KEY_JOIN_STUDY);

        initPreferenceForConsent();

        //TODO Crashes when changing 24hour setting in System-settings
        // Set default value for reminder time if one doesnt exist
        boolean is24hour = android.text.format.DateFormat.is24HourFormat(getActivity());
        SimpleDateFormat dateFormat = new SimpleDateFormat(is24hour ? "H:mm" : "h:mma");
        String defaultReminderFormatted = null;
        if (!screen.getSharedPreferences().contains(KEY_REMINDERS_TIME))
        {
            Calendar defaultReminderTime = Calendar.getInstance();
            defaultReminderTime.set(Calendar.HOUR_OF_DAY, 17);
            defaultReminderTime.set(Calendar.MINUTE, 0);
            defaultReminderTime.set(Calendar.SECOND, 0);
            defaultReminderFormatted = dateFormat.format(defaultReminderTime.getTime());
            screen.getSharedPreferences().edit()
                    .putString(KEY_REMINDERS_TIME, defaultReminderFormatted)
                    .apply();
        }

        // Set reminder time
        Preference reminderTimePref = screen.findPreference(KEY_REMINDERS_TIME);
        reminderTimePref.setSummary(screen.getSharedPreferences()
                .getString(KEY_REMINDERS_TIME, defaultReminderFormatted).toLowerCase());

        // Set version string
        screen.findPreference(KEY_VERSION).setSummary(getVersionString());
    }

    private void initPreferenceForConsent()
    {
        boolean isSignedInAndConsented =
                DataProvider.getInstance().isSignedIn(getActivity()) &&
                DataProvider.getInstance().isConsented(getActivity());

        PreferenceScreen screen = getPreferenceScreen();

        if(! isSignedInAndConsented)
        {
            screen.removePreference(profileCategory);
            privacyCategory.removePreference(sharingScope);
            generalCategory.removePreference(leaveStudy);

            // This method will be called if we leave the study. This means we need to add
            // "join study" back into the general-category as it was removed on the initial call of
            // this method
            if (generalCategory.findPreference(KEY_JOIN_STUDY) == null)
            {
                generalCategory.addPreference(joinStudy);
            }
        }
        else
        {
            generalCategory.removePreference(joinStudy);

            Observable.create(subscriber -> {
                User user = DataProvider.getInstance().getUser(getActivity());
                subscriber.onNext(user);
            }).compose(ObservableUtils.applyDefault()).map(o -> (User) o).subscribe(profile -> {
                profileCategory.findPreference(KEY_PROFILE_NAME).setSummary(profile.getName());

                Preference birthdatePref = profileCategory.findPreference(KEY_PROFILE_BIRTHDATE);
                try
                {
                    // The incoming date is formated in "yyyy-MM-dd", clean it up to "MMM dd, yyyy"
                    Date birthdate =FormatHelper.SIMPLE_FORMAT_DATE.parse(profile.getBirthDate());
                    DateFormat format = FormatHelper.getFormat(DateFormat.LONG, FormatHelper.NONE);
                    birthdatePref.setSummary(format.format(birthdate));
                }
                catch(ParseException e)
                {
                    LogExt.e(SettingsFragment.class, e);
                    birthdatePref.setSummary(profile.getBirthDate());
                }
            });

            // Load Consent Data and set sharing scope
            Observable.create(subscriber -> {
                ConsentSectionModel data = JsonUtils.loadClass(getContext(),
                        ConsentSectionModel.class,
                        ResourceManager.getInstance().getConsentSections());
                subscriber.onNext(data);
            })
                    .compose(ObservableUtils.applyDefault())
                    .map(o -> (ConsentSectionModel) o)
                    .subscribe(data -> {
                        this.data = data;

                        // Load and set sharing scope
                        Observable.create(subscriber -> {
                            subscriber.onNext(DataProvider.getInstance()
                                    .getUserSharingScope(getContext()));
                        })
                                .compose(ObservableUtils.applyDefault())
                                .map(o -> (String) o)
                                .subscribe(scope -> {
                                    sharingScope.setSummary(formatSharingOption(scope));
                                });
                    });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup v = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        progress = inflater.inflate(R.layout.progress, container, false);
        progress.setVisibility(View.GONE);
        v.addView(progress);
        return v;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        LogExt.i(getClass(), preference.getTitle().toString());

        if(preference.hasKey())
        {
            switch(preference.getKey())
            {
                case KEY_PRIVACY_POLICY:
                    showPrivacyPolicy();
                    return true;

                case KEY_LICENSE_INFORMATION:
                    showLicenseInformation();
                    return true;

                case KEY_REVIEW_CONSENT:
                    ConsentFormUtils.viewConsentForm(getContext());
                    return true;

                case KEY_CHANGE_PASSCODE:
                    PassCodeCreationStep step = new PassCodeCreationStep("passcode",
                            R.string.passcode_change_title);
                    step.setStateOrdinal(SignUpPinCodeCreationStepLayout.State.CHANGE.ordinal());
                    OrderedTask passcodeTask = new OrderedTask("task_settings_passcode", step);
                    Intent passcodeIntent = ViewTaskActivity.newIntent(getContext(), passcodeTask);
                    startActivityForResult(passcodeIntent, REQUEST_CODE_CHANGE_PASSCODE);
                    return true;

                case KEY_SHARING_OPTIONS:
                    String investigatorShortDesc = data.getDocumentProperties()
                            .getInvestigatorShortDescription();

                    String investigatorLongDesc = data.getDocumentProperties()
                            .getInvestigatorLongDescription();

                    String localizedLearnMoreHTMLContent = data.getDocumentProperties()
                            .getHtmlContent();

                    ConsentSharingStep sharingStep = new ConsentSharingStep(ConsentTask.ID_SHARING);
                    sharingStep.setOptional(false);
                    sharingStep.setStepTitle(R.string.settings_privacy_sharing_options);
                    sharingStep.setShowsProgress(false);
                    sharingStep.setUseSurveyMode(false);

                    String shareWidely = getString(R.string.rsc_consent_share_widely,
                            investigatorLongDesc);
                    Choice<String> shareWidelyChoice = new Choice<>(shareWidely,
                            "sponsors_and_partners",
                            null);

                    String shareRestricted = getString(R.string.rsc_consent_share_only,
                            investigatorShortDesc);
                    Choice<String> shareRestrictedChoice = new Choice<>(shareRestricted,
                            "all_qualified_researchers",
                            null);

                    sharingStep.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                            shareWidelyChoice,
                            shareRestrictedChoice));

                    sharingStep.setTitle(getString(R.string.rsc_consent_share_title));
                    sharingStep.setText(getString(R.string.rsc_consent_share_description,
                            investigatorLongDesc,
                            localizedLearnMoreHTMLContent));

                    Task task = new OrderedTask("SharingStepTask", sharingStep);
                    Intent intent = ViewTaskActivity.newIntent(getContext(), task);
                    startActivityForResult(intent, REQUEST_CODE_SHARING_OPTIONS);
                    return true;

                case KEY_LEAVE_STUDY:
                    DataProvider.getInstance()
                            .withdrawConsent(getActivity(), null)
                            .compose(ObservableUtils.applyDefault())
                            .subscribe(response -> {
                                if(response.isSuccess())
                                {
                                    Toast.makeText(getActivity(),
                                            R.string.consent_withdraw_success,
                                            Toast.LENGTH_SHORT).show();

                                    initPreferenceForConsent();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), R.string.consent_withdraw_failed,
                                            Toast.LENGTH_SHORT).show();
                                }

                            });
                    return true;

                case KEY_JOIN_STUDY:
                    startActivity(new Intent(getActivity(), OnboardingActivity.class));
                    getActivity().finish();
                    return true;

                case KEY_REMINDERS_TIME:
                    boolean is24hour = android.text.format.DateFormat.is24HourFormat(getActivity());
                    DateFormat format = new SimpleDateFormat(is24hour ? "H:mm" : "h:mma");
                    Calendar calendar = Calendar.getInstance();

                    //TODO Crashes when changing 24hour setting in System-settings
                    try
                    {
                        Date selectedTime = format.parse(preference.getSummary().toString());
                        calendar.setTime(selectedTime);
                    }
                    catch(ParseException e)
                    {
                        throw new RuntimeException(e);
                    }

                    new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        String formatted = format.format(calendar.getTime());
                        preference.getSharedPreferences().edit().putString(KEY_REMINDERS_TIME,
                                formatted).apply();
                        preference.setSummary(formatted.toLowerCase());
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24hour)
                            .show();
                    return true;
            }
        }

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_SHARING_OPTIONS && resultCode == Activity.RESULT_OK)
        {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String result = (String) taskResult.getStepResult(ConsentTask.ID_SHARING).getResult();

            getPreferenceScreen().findPreference(KEY_SHARING_OPTIONS)
                    .setSummary(formatSharingOption(result));

            Observable.create(subscriber -> {
                DataProvider.getInstance().setUserSharingScope(getContext(), result);
            }).compose(ObservableUtils.applyDefault()).subscribe();
        }
        else if (requestCode == REQUEST_CODE_CHANGE_PASSCODE && resultCode == Activity.RESULT_OK)
        {
            TaskResult result = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);

            String oldPinCode = (String) result.getStepResult("passcode")
                    .getResultForIdentifier(SignUpPinCodeCreationStepLayout.RESULT_OLD_PIN);

            String newPinCode = (String) result.getStepResult("passcode").getResult();

            progress.setVisibility(View.VISIBLE);

            Observable.create(subscriber -> {
                StorageAccess.getInstance().changePinCode(getActivity(), oldPinCode, newPinCode);
                subscriber.onNext(null);
            }).compose(ObservableUtils.applyDefault()).subscribe(o -> {
                Toast.makeText(getActivity(), "Passcode Changed", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            }, e -> {
                Toast.makeText(getActivity(), "Changing Passcode Failed", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
            });
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        switch(key)
        {
            case KEY_AUTO_LOCK_ENABLED:
            case KEY_AUTO_LOCK_TIME:
                long autoLockTime = AppPrefs.getInstance(getContext()).getAutoLockTime();
                StorageAccess.getInstance().getPinCodeConfig().setPinAutoLockTime(autoLockTime);
                break;
        }
    }

    public String formatSharingOption(String option)
    {
        if (option.equals("sponsors_and_partners"))
        {
            String investigatorLongDesc = data.getDocumentProperties()
                    .getInvestigatorLongDescription();

            return getString(R.string.rsc_consent_share_widely,
                    investigatorLongDesc);
        }
        else if (option.equals("all_qualified_researchers"))
        {
            String investigatorShortDesc = data.getDocumentProperties()
                    .getInvestigatorShortDescription();

            return getString(R.string.rsc_consent_share_only,
                    investigatorShortDesc);
        }
        else if (option.equals("no_sharing"))
        {
            return getString(R.string.rsc_consent_share_no);
        }
        else
        {
            // If you want to add another sharing option, feel free, you just need to override this
            // method in your SettingsFragment
            throw new RuntimeException("Sharing option " + option + " not supported");
        }
    }

    public String getVersionString()
    {
        return getString(R.string.settings_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE);
    }

    public void showPrivacyPolicy()
    {

        int resId = ResourceManager.getInstance().getPrivacyPolicy();
        String docName = getResources().getResourceEntryName(resId);
        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                getString(R.string.settings_privacy_policy),
                docName);
        startActivity(intent);
    }

    public void showLicenseInformation()
    {
        Intent intent = ViewLicensesActivity.newIntent(getContext());
        startActivity(intent);
    }
}
