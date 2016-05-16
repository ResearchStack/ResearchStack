package org.researchstack.sampleapp;
import android.content.Context;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.skin.ActionItem;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.task.OnboardingTask;
import org.researchstack.skin.ui.LearnActivity;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;

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
        BooleanAnswerFormat booleanAnswerFormat = new BooleanAnswerFormat("true","false");

        QuestionStep ageStep = new QuestionStep("signupInclusionAgeStep",
                "Are you over 18 years of age?",
                booleanAnswerFormat);

        QuestionStep diagnosisStep = new QuestionStep("signupInclusionDiagnosisStep",
                "Have you been diagnosed with pre-diabetes or diabetes?",
                booleanAnswerFormat);

        QuestionStep englishStep = new QuestionStep("signupInclusionEnglishStep",
                "Can you read and understand English in order to provide informed consent and to follow the instructions?",
                booleanAnswerFormat);

        QuestionStep usaStep = new QuestionStep("signupInclusionUsaStep",
                "Do you live in the United States of America?",
                booleanAnswerFormat);



        FormStep eligibilityFormStep = new FormStep(OnboardingTask.SignUpInclusionCriteriaStepIdentifier, "", "");
        // Set items on FormStep
        eligibilityFormStep.setStepTitle(R.string.rss_eligibility);
        eligibilityFormStep.setOptional(false);
        eligibilityFormStep.setFormSteps(ageStep, diagnosisStep, englishStep, usaStep);

        return eligibilityFormStep;

    }

    private Boolean getBooleanAnswer(Map mapStepResult, String id){
        StepResult stepResult = (StepResult)mapStepResult.get(id);
        if (stepResult == null) return false;
        Map mapResult = stepResult.getResults();
        if (mapResult == null) return false;
        Boolean answer = (Boolean)mapResult.get("answer");
        System.out.println("id = : " + id + ", answer = " + answer);
        if (answer == null || answer == false) return false;
        else return true;

    }

    //If all answers are true, result is true
    //If any answer is false, result is false
    @Override
    public boolean isInclusionCriteriaValid(StepResult stepResult)
    {
        if(stepResult != null)
        {
            Map mapStepResult = stepResult.getResults();
            Boolean answer = getBooleanAnswer(mapStepResult, "signupInclusionAgeStep");
            if (answer == false) return false;
            answer = getBooleanAnswer(mapStepResult, "signupInclusionDiagnosisStep");
            if (answer == false) return false;
            answer = getBooleanAnswer(mapStepResult, "signupInclusionEnglishStep");
            if (answer == false) return false;
            answer = getBooleanAnswer(mapStepResult, "signupInclusionUsaStep");
            if (answer == false) return false;
            return true;
        }
        return false;
    }

    @Override
    public boolean isConsentSkippable()
    {
        return true;
    }

}
