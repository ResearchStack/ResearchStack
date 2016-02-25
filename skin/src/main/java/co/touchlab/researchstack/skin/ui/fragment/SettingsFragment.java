package co.touchlab.researchstack.skin.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import co.touchlab.researchstack.backbone.helpers.LogExt;
import co.touchlab.researchstack.backbone.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.backbone.utils.ObservableUtils;
import co.touchlab.researchstack.glue.BuildConfig;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.ui.ViewLicensesActivity;
import rx.Observable;

/**
 * TODO Try and point to a single instance of a key instead of defining them in XML and in code.
 * TODO Version text currently points to ResearchStack, implement a way of getting version of SampleApp
 * TODO Implement screens for all items in {@link #onPreferenceTreeClick}
 */
public class SettingsFragment extends PreferenceFragmentCompat
{
    public static final String KEY_PROFILE = "Skin.PROFILE";
    public static final String KEY_PROFILE_NAME = "Skin.PROFILE_NAME";
    public static final String KEY_PROFILE_BIRTHDATE = "Skin.PROFILE_BIRTHDATE";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.addPreferencesFromResource(R.xml.settings);

        Preference version = getPreferenceManager().findPreference("version");
        if(version != null)
        {
            String versionText = getString(R.string.settings_version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE);
            version.setSummary(versionText);
        }

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();

        // Create a category w/ a negative order to get it to 0 position
        PreferenceCategory category = new PreferenceCategory(screen.getContext());
        category.setKey(KEY_PROFILE);
        category.setOrder(- 1); // Its a hack, but it works
        category.setTitle("Profile");
        screen.addPreference(category);

        // Add Name Preference
        Preference namePref = new Preference(screen.getContext());
        namePref.setKey(KEY_PROFILE_NAME);
        namePref.setTitle(R.string.name);
        namePref.setSummary(" "); // Set to prevent a "jump" when first entering the screen
        Observable.create(subscriber -> {
            subscriber.onNext("PLACEHOLDER");
        }).compose(ObservableUtils.applyDefault()).subscribe(value -> {
            namePref.setSummary((String) value);
        });
        category.addPreference(namePref);

        // Add Birthdate Preference
        Preference birthdatePref = new Preference(screen.getContext());
        birthdatePref.setKey(KEY_PROFILE_BIRTHDATE);
        birthdatePref.setTitle(R.string.birthdate);
        birthdatePref.setSummary(" "); // Set to prevent a "jump" when first entering the screen
        Observable.create(subscriber -> {
            subscriber.onNext("PLACEHOLDER");
        }).compose(ObservableUtils.applyDefault()).subscribe(value -> {
            birthdatePref.setSummary((String) value);
        });
        category.addPreference(birthdatePref);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        LogExt.i(getClass(), preference.getTitle().toString());

        if(preference.hasKey())
        {
            switch(preference.getKey())
            {
                case "privacy_policy":
                    showPrivacyPolicy();
                    return true;

                case "license_information":
                    showLicenseInformation();
                    return true;
            }
        }

        Toast.makeText(getActivity(),
                "TODO: " + preference.getTitle().toString(),
                Toast.LENGTH_SHORT).show();

        return super.onPreferenceTreeClick(preference);
    }

    private void showPrivacyPolicy()
    {

        int resId = ResourceManager.getInstance().getPrivacyPolicy();
        String docName = getResources().getResourceEntryName(resId);
        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                getString(R.string.settings_privacy_policy),
                docName);
        startActivity(intent);
    }

    private void showLicenseInformation()
    {
        Intent intent = ViewLicensesActivity.newIntent(getContext());
        startActivity(intent);
    }
}
