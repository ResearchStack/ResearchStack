package co.touchlab.researchstack.skin.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

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
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.glue.BuildConfig;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.AppPrefs;
import co.touchlab.researchstack.skin.DataProvider;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.task.ConsentTask;
import co.touchlab.researchstack.skin.ui.ViewLicensesActivity;
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

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Settings Keys. If you are adding / changing settings, make sure they are unique / match in
    // the settings.xml file
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Profile
    /*TODO*/ public static final String KEY_PROFILE           = "settings_profile";
    /*TODO*/ public static final String KEY_PROFILE_NAME      = "settings_profile_name";
    /*TODO*/ public static final String KEY_PROFILE_BIRTHDATE = "settings_profile_birthdate";
    // Reminders
    /*TODO*/ public static final String KEY_REMINDERS         = "settings_reminders";
    /*TODO*/ public static final String KEY_REMINDERS_TIME    = "settings_reminders_time";
    // Privacy
    public static final String KEY_PRIVACY           = "settings_privacy";
    public static final String KEY_PRIVACY_POLICY    = "settings_privacy_policy";
    public static final String KEY_REVIEW_CONSENT    = "settings_privacy_review_consent";
    public static final String KEY_SHARING_OPTIONS   = "settings_privacy_sharing_options";
    // Security
    public static final String KEY_AUTO_LOCK_ENABLED = "settings_auto_lock_on_exit";
    public static final String KEY_AUTO_LOCK_TIME    = "settings_auto_lock_time";
    /*TODO*/ public static final String KEY_CHANGE_PASSCODE = "settings_security_change_passcode";
    // General
    public static final String KEY_GENERAL = "settings_general";
    public static final String KEY_LICENSE_INFORMATION = "settings_general_license_information";
    /*TODO*/ public static final String KEY_LEAVE_STUDY = "settings_general_leave_study";
    /*TODO*/ public static final String KEY_JOIN_STUDY = "settings_general_join_study";
    // Other
    public static final String KEY_VERSION = "settings_version";
    private ConsentSectionModel data;

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.addPreferencesFromResource(R.xml.settings);

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();
        screen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        //TODO check if consented
        boolean isConsented = true;

        // Find profile profileCategory
        PreferenceCategory profileCategory = (PreferenceCategory) screen.findPreference(KEY_PROFILE);

        PreferenceCategory privacyCategory = (PreferenceCategory) screen.findPreference(KEY_PRIVACY);
        Preference sharingScope = privacyCategory.findPreference(KEY_SHARING_OPTIONS);

        PreferenceCategory generalCategory = (PreferenceCategory) screen.findPreference(KEY_GENERAL);
        Preference leaveStudy = generalCategory.findPreference(KEY_LEAVE_STUDY);
        Preference joinStudy = generalCategory.findPreference(KEY_JOIN_STUDY);

        if(! isConsented)
        {
            screen.removePreference(profileCategory);
            privacyCategory.removePreference(sharingScope);
            generalCategory.removePreference(leaveStudy);
        }
        else
        {
            generalCategory.removePreference(joinStudy);

            // TODO Set Name Preference
            Observable.create(subscriber -> {
                subscriber.onNext("PLACEHOLDER");
            }).compose(ObservableUtils.applyDefault()).subscribe(value -> {
                profileCategory.findPreference(KEY_PROFILE_NAME).setSummary((String) value);
            });

            // TODO Set Birthdate Preference
            Observable.create(subscriber -> {
                subscriber.onNext("PLACEHOLDER");
            }).compose(ObservableUtils.applyDefault()).subscribe(value -> {
                profileCategory.findPreference(KEY_PROFILE_BIRTHDATE).setSummary((String) value);
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

        // Set version string
        screen.findPreference(KEY_VERSION).setSummary(getVersionString());
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
        else
        {
            // If you want to add another sharing option, feel free, you just need to override this
            // method in your SettingsFragment
            throw new RuntimeException("Sharing option not supported");
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
