package co.touchlab.researchstack.skin.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import co.touchlab.researchstack.backbone.StorageAccess;
import co.touchlab.researchstack.backbone.helpers.LogExt;
import co.touchlab.researchstack.backbone.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.glue.BuildConfig;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.AppPrefs;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.ui.ViewLicensesActivity;
import rx.Observable;

/**
 * TODO Try and point to a single instance of a key instead of defining them in XML and in code.
 * TODO Version text currently points to ResearchStack, implement a way of getting version of SampleApp
 * TODO Implement screens for all items in {@link #onPreferenceTreeClick}
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    // Profile
    /*TODO*/ public static final String KEY_PROFILE              = "settings_profile";
    /*TODO*/ public static final String KEY_PROFILE_NAME         = "settings_profile_name";
    /*TODO*/ public static final String KEY_PROFILE_BIRTHDATE    = "settings_profile_birthdate";
    // Reminders
    /*TODO*/ public static final String KEY_REMINDERS            = "settings_reminders";
    /*TODO*/ public static final String KEY_REMINDERS_TIME       = "settings_reminders_time";
    // Privacy
    public static final String KEY_PRIVACY_POLICY       = "settings_privacy_policy";
    /*TODO*/ public static final String KEY_REVIEW_CONSENT       = "settings_privacy_review_consent";
    /*TODO*/ public static final String KEY_SHARING_OPTIONS      = "settings_privacy_sharing_options";
    // Security
    public static final String KEY_AUTO_LOCK_ENABLED    = "settings_auto_lock_on_exit";
    public static final String KEY_AUTO_LOCK_TIME       = "settings_auto_lock_time";
    /*TODO*/ public static final String KEY_AUTO_CHANGE_PASSCODE = "settings_security_change_passcode";
    // General
    public static final String KEY_LICENSE_INFORMATION  = "settings_general_license_information";
    /*TODO*/ public static final String KEY_LEAVE_STUDY          = "settings_general_leave_study";
    // Other
    public static final String KEY_VERSION              = "settings_version";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.addPreferencesFromResource(R.xml.settings);

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();

        screen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Set version string
        screen.findPreference(KEY_VERSION).setSummary(getVersionString());

        // TODO Remove category if user is not consented
        // Find profile category
        PreferenceCategory category = (PreferenceCategory) screen.findPreference(KEY_PROFILE);

        // TODO Set Name Preference
        Observable.create(subscriber -> {
            subscriber.onNext("PLACEHOLDER");
        }).compose(ObservableUtils.applyDefault()).subscribe(value -> {
            category.findPreference(KEY_PROFILE_NAME).setSummary((String) value);
        });

        // TODO Set Birthdate Preference
        Observable.create(subscriber -> {
            subscriber.onNext("PLACEHOLDER");
        }).compose(ObservableUtils.applyDefault()).subscribe(value -> {
            category.findPreference(KEY_PROFILE_BIRTHDATE).setSummary((String) value);
        });
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
            }
        }

        return super.onPreferenceTreeClick(preference);
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
