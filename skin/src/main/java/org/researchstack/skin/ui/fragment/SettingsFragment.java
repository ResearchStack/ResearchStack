package org.researchstack.skin.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentSectionModel;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.ConsentSharingStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.R;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.User;
import org.researchstack.skin.notification.TaskAlertReceiver;
import org.researchstack.skin.step.PassCodeCreationStep;
import org.researchstack.skin.task.ConsentTask;
import org.researchstack.skin.ui.OnboardingActivity;
import org.researchstack.skin.ui.layout.SignUpPinCodeCreationStepLayout;
import org.researchstack.skin.utils.ConsentFormUtils;
import org.researchstack.skin.utils.JsonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import rx.Observable;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final int REQUEST_CODE_SHARING_OPTIONS = 0;
    private static final int REQUEST_CODE_CHANGE_PASSCODE = 1;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Settings Keys.
    // If you are adding / changing settings, make sure they are unique / match in settings.xml
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Profile
    public static final String KEY_PROFILE           = "rss_settings_profile";
    public static final String KEY_PROFILE_NAME      = "rss_settings_profile_name";
    public static final String KEY_PROFILE_BIRTHDATE = "rss_settings_profile_birthdate";
    // Reminders
    public static final String KEY_REMINDERS         = "rss_settings_reminders";
    // Privacy
    public static final String KEY_PRIVACY           = "rss_settings_privacy";
    public static final String KEY_PRIVACY_POLICY    = "rss_settings_privacy_policy";
    public static final String KEY_REVIEW_CONSENT    = "rss_settings_privacy_review_consent";
    public static final String KEY_SHARING_OPTIONS   = "rss_settings_privacy_sharing_options";
    // Security
    public static final String KEY_AUTO_LOCK_ENABLED = "rss_settings_auto_lock_on_exit";
    public static final String KEY_AUTO_LOCK_TIME    = "rss_settings_auto_lock_time";
    public static final String KEY_CHANGE_PASSCODE   = "rss_settings_security_change_passcode";
    // General
    public static final String KEY_GENERAL           = "rss_settings_general";
    public static final String KEY_SOFTWARE_NOTICES  = "rss_settings_general_software_notices";
    public static final String KEY_LEAVE_STUDY       = "rss_settings_general_leave_study";
    public static final String KEY_JOIN_STUDY        = "rss_settings_general_join_study";
    // Other
    public static final String KEY_VERSION           = "rss_settings_version";

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Preference Items
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private PreferenceCategory profileCategory;
    private PreferenceCategory privacyCategory;
    private Preference         sharingScope;
    private PreferenceCategory generalCategory;
    private Preference         leaveStudy;
    private Preference         joinStudy;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Field Vars
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private ConsentSectionModel data;
    private View                progress;

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

        // Set version string
        screen.findPreference(KEY_VERSION).setSummary(getVersionString());
    }

    private void initPreferenceForConsent()
    {
        boolean isSignedInAndConsented = DataProvider.getInstance().isSignedIn(getActivity()) &&
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
            if(generalCategory.findPreference(KEY_JOIN_STUDY) == null)
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
                    Date birthdate = FormatHelper.SIMPLE_FORMAT_DATE.parse(profile.getBirthDate());
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
        LogExt.i(getClass(), String.valueOf(preference.getTitle()));

        if(preference.hasKey())
        {
            switch(preference.getKey())
            {
                case KEY_PRIVACY_POLICY:
                    showPrivacyPolicy();
                    return true;

                case KEY_SOFTWARE_NOTICES:
                    showSoftwareNotices();
                    return true;

                case KEY_REVIEW_CONSENT:
                    ConsentFormUtils.viewConsentForm(getContext());
                    return true;

                case KEY_CHANGE_PASSCODE:
                    PassCodeCreationStep step = new PassCodeCreationStep("passcode",
                            R.string.rss_passcode_change_title);
                    step.setStateOrdinal(SignUpPinCodeCreationStepLayout.State.CHANGE.ordinal());
                    OrderedTask passcodeTask = new OrderedTask("task_rss_settings_passcode", step);
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
                    sharingStep.setStepTitle(R.string.rss_settings_privacy_sharing_options);
                    sharingStep.setShowsProgress(false);
                    sharingStep.setUseSurveyMode(false);

                    String shareWidely = getString(R.string.rsb_consent_share_widely,
                            investigatorLongDesc);
                    Choice<String> shareWidelyChoice = new Choice<>(shareWidely,
                            "sponsors_and_partners",
                            null);

                    String shareRestricted = getString(R.string.rsb_consent_share_only,
                            investigatorShortDesc);
                    Choice<String> shareRestrictedChoice = new Choice<>(shareRestricted,
                            "all_qualified_researchers",
                            null);

                    sharingStep.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                            shareWidelyChoice,
                            shareRestrictedChoice));

                    sharingStep.setTitle(getString(R.string.rsb_consent_share_title));
                    sharingStep.setText(getString(R.string.rsb_consent_share_description,
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
                                            R.string.rss_consent_withdraw_success,
                                            Toast.LENGTH_SHORT).show();

                                    initPreferenceForConsent();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(),
                                            R.string.rss_consent_withdraw_failed,
                                            Toast.LENGTH_SHORT).show();
                                }

                            });
                    return true;

                case KEY_JOIN_STUDY:
                    startActivity(new Intent(getActivity(), OnboardingActivity.class));
                    getActivity().finish();
                    return true;

                case KEY_REMINDERS:
                    SharedPreferences preferences = preference.getSharedPreferences();
                    boolean isRemindersEnabled = preferences.getBoolean(KEY_REMINDERS, true);
                    if (!isRemindersEnabled)
                    {
                        getActivity().sendBroadcast(new Intent(TaskAlertReceiver.ALERT_DELETE_ALL));
                    }
                    return true;
            }
        }

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_SHARING_OPTIONS && resultCode == Activity.RESULT_OK)
        {
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            String result = (String) taskResult.getStepResult(ConsentTask.ID_SHARING).getResult();

            getPreferenceScreen().findPreference(KEY_SHARING_OPTIONS)
                    .setSummary(formatSharingOption(result));

            Observable.create(subscriber -> {
                DataProvider.getInstance().setUserSharingScope(getContext(), result);
            }).compose(ObservableUtils.applyDefault()).subscribe();
        }
        else if(requestCode == REQUEST_CODE_CHANGE_PASSCODE && resultCode == Activity.RESULT_OK)
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
                Toast.makeText(getActivity(), "Changing Passcode Failed", Toast.LENGTH_SHORT)
                        .show();
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
        if(option.equals("sponsors_and_partners"))
        {
            String investigatorLongDesc = data.getDocumentProperties()
                    .getInvestigatorLongDescription();

            return getString(R.string.rsb_consent_share_widely, investigatorLongDesc);
        }
        else if(option.equals("all_qualified_researchers"))
        {
            String investigatorShortDesc = data.getDocumentProperties()
                    .getInvestigatorShortDescription();

            return getString(R.string.rsb_consent_share_only, investigatorShortDesc);
        }
        else if(option.equals("no_sharing"))
        {
            return getString(R.string.rsb_consent_share_no);
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
        int versionCode = 0;
        String versionName = "";
        PackageManager manager = getActivity().getPackageManager();

        try
        {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = info.versionCode;
            versionName = info.versionName;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            // TODO Auto-generated catch block
        }
        return getString(R.string.rss_settings_version, versionName, versionCode);
    }

    public void showPrivacyPolicy()
    {

        int resId = ResourceManager.getInstance().getPrivacyPolicy();
        String docName = getResources().getResourceEntryName(resId);
        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                getString(R.string.rss_settings_privacy_policy),
                docName);
        startActivity(intent);
    }

    public void showSoftwareNotices()
    {
        int resId = ResourceManager.getInstance().getSoftwareNotices();
        String documentName = getResources().getResourceEntryName(resId);
        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                getString(R.string.rss_settings_general_software_notices),
                documentName);
        startActivity(intent);
    }
}
