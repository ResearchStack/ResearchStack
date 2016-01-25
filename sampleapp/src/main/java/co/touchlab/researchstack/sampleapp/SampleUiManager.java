package co.touchlab.researchstack.sampleapp;
import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.glue.NavigationItem;
import co.touchlab.researchstack.glue.UiManager;
import co.touchlab.researchstack.glue.ui.fragment.ActivitiesFragment;
import co.touchlab.researchstack.glue.ui.fragment.DashboardFragment;
import co.touchlab.researchstack.glue.ui.fragment.LearnFragment;
import co.touchlab.researchstack.glue.ui.fragment.ProfileFragment;
import co.touchlab.researchstack.glue.ui.fragment.SettingsFragment;

public class SampleUiManager extends UiManager
{
    /**
     * TODO Clean up implementation *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
     * Not too happy about returning a list of objects. Could we / should we use XML impl?
     *
     * @return List of NavigationItems
     */
    @Override
    public List<NavigationItem> getNavigationItems()
    {
        List<NavigationItem> navItems = new ArrayList<>();

        navItems.add(new NavigationItem().setId(R.id.nav_activities)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.activities)
                .setIcon(R.drawable.ic_nav_activities)
                .setClass(ActivitiesFragment.class));

        navItems.add(new NavigationItem().setId(R.id.nav_dashboard)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.dashboard)
                .setIcon(R.drawable.ic_nav_dashboard)
                .setClass(DashboardFragment.class));

        navItems.add(new NavigationItem().setId(R.id.nav_learn)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.learn)
                .setIcon(R.drawable.ic_nav_learn)
                .setClass(LearnFragment.class));

        navItems.add(new NavigationItem().setId(R.id.nav_profile)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.profile)
                .setIcon(R.drawable.ic_nav_profile)
                .setClass(ProfileFragment.class));

        navItems.add(new NavigationItem().setId(R.id.nav_settings)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.settings)
                .setIcon(R.drawable.ic_nav_settings)
                .setClass(SettingsFragment.class));

        navItems.add(new NavigationItem().setId(R.id.nav_custom)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.custom)
                .setIcon(R.drawable.ic_nav_custom)
                .setClass(SampleCustomFragment.class));

        return navItems;
    }

    @Override
    public Class getInclusionCriteriaSceneClass()
    {
        return SignUpInclusionCriteriaStepLayout.class;
    }

    @Override
    public boolean isSignatureEnabledInConsent()
    {
        return true;
    }
}
