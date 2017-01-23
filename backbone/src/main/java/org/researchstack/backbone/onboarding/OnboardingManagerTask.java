package org.researchstack.backbone.onboarding;

import android.content.Context;
import android.support.annotation.StringRes;

import org.researchstack.backbone.R;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.NavigableOrderedTask;

import java.util.List;

/**
 * Created by TheMDP on 1/20/17.
 */

public class OnboardingManagerTask extends NavigableOrderedTask {

    public OnboardingManagerTask(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    public OnboardingManagerTask(String identifier, Step... steps) {
        super(identifier, steps);
    }

    @Override
    public String getTitleForStep(Context context, Step step) {
        String title = null;

        @StringRes int stepTitleRes = -1;

        // All these are Subtasks, so identifier will be in the form of id.[question_id]
        if (step.getStepTitle() != 0) {
            stepTitleRes = step.getStepTitle();
        } else if (step.getIdentifier().contains(SurveyFactory.CONSENT_QUIZ_IDENTIFIER)) {
            stepTitleRes = R.string.rsb_consent_quiz_step_title;
        } else if (step.getIdentifier().contains(ConsentDocumentFactory.CONSENT_REVIEW_IDENTIFIER)) {
            stepTitleRes = R.string.rsb_consent_review_step_title;
        } else if (step.getIdentifier().contains(ConsentDocumentFactory.CONSENT_SHARING_IDENTIFIER)) {
            stepTitleRes = R.string.rsb_consent_review_step_title;
        }

        if (stepTitleRes > 0) {
            return context.getString(stepTitleRes);
        }

        return title;
    }
}
