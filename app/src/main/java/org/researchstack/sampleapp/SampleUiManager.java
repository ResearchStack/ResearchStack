package org.researchstack.sampleapp;
import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.skin.ActionItem;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.ui.LearnActivity;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;

import java.util.ArrayList;
import java.util.List;

public class SampleUiManager extends UiManager
{
    /**
     * @return List of ActionItems w/ Fragment class items
     */
    @Override
    public List<ActionItem> getMainTabBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_activities)
                .setTitle(R.string.rss_activities)
                .setIcon(R.drawable.rss_ic_tab_activities)
                .setClass(ActivitiesFragment.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_dashboard)
                .setTitle(R.string.rss_dashboard)
                .setIcon(R.drawable.rss_ic_tab_dashboard)
                .setClass(DashboardFragment.class)
                .build());

        return navItems;
    }

    /**
     * @return List of ActionItems w/ Activity class items. The class items are then used to
     * construct an intent for a MenuItem when {@link org.researchstack.skin.ui.MainActivity#onCreateOptionsMenu}
     * is called
     */
    @Override
    public List<ActionItem> getMainActionBarItems()
    {
        List<ActionItem> navItems = new ArrayList<>();

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_learn)
                .setTitle(R.string.rss_learn)
                .setIcon(R.drawable.rss_ic_action_learn)
                .setClass(LearnActivity.class)
                .build());

        navItems.add(new ActionItem.ActionItemBuilder().setId(R.id.nav_settings)
                .setTitle(R.string.rss_settings)
                .setIcon(R.drawable.rss_ic_action_settings)
                .setClass(SampleSettingsActivity.class)
                .build());

        return navItems;
    }

    /**
     * This needs to change, strange that UIManager is handling step creation of OnboardingTask.
     * We should have a place where all the steps are created and returned if dev wants to edit any
     * step within said task.
     *
     * @return InclusionCriteria Step
     * @param context
     */
    @Override
    public Step getInclusionCriteriaStep(Context context)
    {
        Choice<Boolean> human = new Choice<>(context.getString(R.string.yes_human), true, null);
        Choice<Boolean> robot = new Choice<>(context.getString(R.string.yes_robot),
                true,
                null);
        Choice<Boolean> alien = new Choice<>(context.getString(R.string.no_alien), false, null);

        QuestionStep step = new QuestionStep(OnboardingTask.SignUpInclusionCriteriaStepIdentifier);
        step.setOptional(false);
        step.setStepTitle(R.string.rss_eligibility);
        step.setTitle(context.getString(R.string.inclusion_question));
        step.setAnswerFormat(new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                human,
                robot,
                alien));

        return step;
    }

    @Override
    public boolean isInclusionCriteriaValid(StepResult stepResult)
    {
        if(stepResult != null)
        {
            return ((StepResult<Boolean>) stepResult).getResult();
        }

        return false;
    }

    @Override
    public boolean isConsentSkippable()
    {
        return true;
    }

}
