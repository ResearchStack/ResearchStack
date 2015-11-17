package co.touchlab.researchstack.ui.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import co.touchlab.researchstack.BuildConfig;
import co.touchlab.researchstack.R;

/**
 * Created by bradleymcdermott on 10/28/15.
 */
public class SettingsFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //TODO Implement
        debug_initItem(R.id.settings_reminders);

        //TODO Implement
        debug_initItem(R.id.settings_auto_lock);

        //TODO Implement
        debug_initItem(R.id.settings_passcode);

        //TODO Implement
        debug_initItem(R.id.settings_sharing);

        //TODO Implement
        debug_initItem(R.id.settings_permissions);

        //TODO Implement
        debug_initItem(R.id.settings_review_consent);

        //TODO Implement
        debug_initItem(R.id.settings_export_data);

        //TODO Implement
        debug_initItem(R.id.settings_privacy_policy);

        //TODO Implement
        debug_initItem(R.id.settings_license_info);

        String version = getString(R.string.settings_version, BuildConfig.VERSION_NAME,
                                   BuildConfig.VERSION_CODE);
        ((TextView) view.findViewById(R.id.settings_version)).setText(version);
    }

    /**
     * TODO Debug helper method to init settings items.
     */
    @Deprecated
    public void debug_initItem(@IdRes int id)
    {
        getView().findViewById(id).setOnClickListener(v -> Toast
                .makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT)
                .show());
    }

}
