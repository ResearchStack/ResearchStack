package co.touchlab.researchstack.sampleapp;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.skin.ActionItem;
import co.touchlab.researchstack.skin.UiManager;
import co.touchlab.researchstack.skin.ui.fragment.ActivitiesFragment;
import co.touchlab.researchstack.skin.ui.fragment.DashboardFragment;
import co.touchlab.researchstack.skin.ui.fragment.LearnFragment;
import co.touchlab.researchstack.skin.ui.fragment.ProfileFragment;
import co.touchlab.researchstack.skin.ui.fragment.SettingsFragment;

public class SampleUiManager extends UiManager
{
    /**
     * TODO Clean up implementation *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
     * Not too happy about returning a list of objects. Could we / should we use XML impl?
     *
     * @return List of NavigationItems
     */
    @Override
    public List<ActionItem> getMainTabBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem().setId(R.id.nav_activities)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.activities)
                .setIcon(R.drawable.ic_nav_activities)
                .setClass(ActivitiesFragment.class));

        navItems.add(new ActionItem().setId(R.id.nav_dashboard)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.dashboard)
                .setIcon(R.drawable.ic_nav_dashboard)
                .setClass(DashboardFragment.class));

        navItems.add(new ActionItem().setId(R.id.nav_debug)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.debug)
                .setIcon(R.mipmap.ic_launcher)
                .setClass(SampleDebugFragment.class));

        return navItems;
    }

    @Override
    public List<ActionItem> getMainActionBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem().setId(R.id.nav_learn)
                .setTitle(R.string.learn)
                .setIcon(R.drawable.ic_nav_learn)
                .setClass(LearnFragment.class));

        navItems.add(new ActionItem().setId(R.id.nav_profile)
                .setTitle(R.string.profile)
                .setIcon(R.drawable.ic_nav_profile)
                .setClass(ProfileFragment.class));

        navItems.add(new ActionItem().setId(R.id.nav_settings)
                .setTitle(R.string.settings)
                .setIcon(R.drawable.ic_nav_settings)
                .setAction(MenuItem.SHOW_AS_ACTION_NEVER)
                .setClass(SettingsFragment.class));

        return navItems;
    }

    @Override
    public Class getInclusionCriteriaStepLayoutClass()
    {
        return SignUpInclusionCriteriaStepLayout.class;
    }

    @Override
    public boolean isSignatureEnabledInConsent()
    {
        return true;
    }
}
