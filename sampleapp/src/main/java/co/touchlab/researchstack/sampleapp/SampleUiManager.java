package co.touchlab.researchstack.sampleapp;
import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.backbone.answerformat.AnswerFormat;
import co.touchlab.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.backbone.model.Choice;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.skin.ActionItem;
import co.touchlab.researchstack.skin.UiManager;
import co.touchlab.researchstack.skin.task.OnboardingTask;
import co.touchlab.researchstack.skin.ui.fragment.ActivitiesFragment;
import co.touchlab.researchstack.skin.ui.fragment.DashboardFragment;
import co.touchlab.researchstack.skin.ui.fragment.LearnFragment;
import co.touchlab.researchstack.skin.ui.fragment.ProfileFragment;

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

        navItems.add(new ActionItem.ActionItemBuilder()
                .setId(R.id.nav_activities)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.activities)
                .setIcon(R.drawable.ic_nav_activities)
                .setClass(ActivitiesFragment.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder()
                .setId(R.id.nav_dashboard)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.dashboard)
                .setIcon(R.drawable.ic_nav_dashboard)
                .setClass(DashboardFragment.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder()
                .setId(R.id.nav_debug)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.debug)
                .setIcon(R.mipmap.ic_launcher)
                .setClass(SampleDebugFragment.class)
                .build());

        return navItems;
    }

    @Override
    public List<ActionItem> getMainActionBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem.ActionItemBuilder()
                .setId(R.id.nav_learn)
                .setTitle(R.string.learn)
                .setIcon(R.drawable.ic_action_info)
                .setClass(LearnFragment.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder()
                .setId(R.id.nav_profile)
                .setTitle(R.string.profile)
                .setIcon(R.drawable.ic_action_profile)
                .setClass(ProfileFragment.class)
                .build());

        return navItems;
    }

    /**
     * TODO Refactor into a framework step builder *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * This needs to change, strange that UIManager is handling step creation of OnboardingTask.
     * We should have a place where all the steps are created and returned if dev wants to edit any
     * step within said task.
     *
     * @return InclusionCriteria Step
     */
    @Override
    public Step getInclusionCriteriaStep()
    {
        Choice<Boolean> human = new Choice<>("Yes, I am a human.", true, null);
        Choice<Boolean> robot = new Choice<>("No, I am a robot but I am sentient and concerned about my health.", true, null);
        Choice<Boolean> alien = new Choice<>("No, I’m an alien.", false, null);

        QuestionStep step = new QuestionStep(OnboardingTask.SignUpInclusionCriteriaStepIdentifier);
        step.setStepTitle(R.string.eligibility);
        step.setTitle("Were you born somewhere on planet earth and are you a human-ish?");
        step.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                human,
                robot,
                alien));

        return step;
    }

    @Override
    public boolean isSignatureEnabledInConsent()
    {
        return true;
    }

    @Override
    public boolean isConsentSkippable()
    {
        return true;
    }
}
