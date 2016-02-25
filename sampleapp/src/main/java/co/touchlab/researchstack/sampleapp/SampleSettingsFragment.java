package co.touchlab.researchstack.sampleapp;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;

import co.touchlab.researchstack.skin.ui.fragment.SettingsFragment;

public class SampleSettingsFragment extends SettingsFragment
{
    public static final String KEY_EXAMPLE = "Sample.EXAMPLE";

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
    {
        super.onCreatePreferences(bundle, s);

        // Get our screen which is created in Skin SettingsFragment
        PreferenceScreen screen = getPreferenceScreen();

        // Get profile preference
        PreferenceCategory category = (PreferenceCategory) screen.getPreference(0);

        // If category at 0 is profile, add mole-mapper specific pref items
        if(category.getKey().equals(SettingsFragment.KEY_PROFILE))
        {
            // Occupation Preference
            Preference checkBoxPref = new Preference(screen.getContext());
            checkBoxPref.setKey(KEY_EXAMPLE);
            checkBoxPref.setTitle("Example Title");
            checkBoxPref.setSummary("You need to extend your settings fragment from Skin's " +
                    "Settings fragment and then add / remove any preferences that you want");
            category.addPreference(checkBoxPref);
        }

    }

}
