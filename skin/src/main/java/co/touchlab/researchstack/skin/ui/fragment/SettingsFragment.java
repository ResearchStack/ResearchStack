package co.touchlab.researchstack.skin.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.glue.BuildConfig;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.skin.ResourceManager;
import co.touchlab.researchstack.skin.ui.ViewLicensesActivity;

/**
 * TODO Try and point to a single instance of a key instead of defining them in XML and in code.
 * TODO Version text currently points to ResearchStack, implement a way of getting version of SampleApp
 * TODO Implement screens for all items in {@link #onPreferenceTreeClick}
 */
public class SettingsFragment extends PreferenceFragmentCompat
{

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
