package org.researchstack.skin.ui.layout;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.skin.R;
import org.researchstack.skin.step.ConsentQuizEvaluationStep;

public class ConsentQuizEvaluationStepLayout extends FixedSubmitBarLayout implements StepLayout
{

    private ConsentQuizEvaluationStep step;
    private StepResult<Boolean>       result;
    private StepCallbacks             callbacks;

    public ConsentQuizEvaluationStepLayout(Context context)
    {
        super(context);
    }

    public ConsentQuizEvaluationStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentQuizEvaluationStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentQuizEvaluationStep) step;
        this.result = result == null ? new StepResult<>(step) : result;

        initializeStep();
    }

    @Override
    public int getContentResourceId()
    {
        return R.layout.rss_layout_consent_evaluation;
    }

    private void initializeStep()
    {
        ImageView image = (ImageView) findViewById(R.id.rss_quiz_eval_image);
        TextView title = (TextView) findViewById(R.id.rss_quiz_eval_title);
        TextView summary = (TextView) findViewById(R.id.rss_quiz_eval_summary);

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        submitBar.getNegativeActionView().setVisibility(View.GONE);
        submitBar.setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                step,
                result));

        // We have failed
        if(! step.isQuizPassed())
        {
            int iconResId = ResUtils.getDrawableResourceId(getContext(),
                    step.getQuizModel().getIncorrectIcon());

            image.setImageResource(iconResId);
            title.setText(R.string.rsb_quiz_evaluation_try_again);

            if(! step.isOverMaxAttempts())
            {
                summary.setText(step.getQuizModel().getFailureMessage());
                submitBar.setPositiveTitle(R.string.rsb_quiz_evaluation_retake);
            }
            else
            {
                summary.setText(step.getQuizModel().getFailureMessage());
                submitBar.setPositiveTitle(R.string.rsb_quiz_evaluation_review_consent);
            }
        }

        // We have passed
        else
        {
            int iconResId = ResUtils.getDrawableResourceId(getContext(),
                    step.getQuizModel().getCorrectIcon());

            image.setImageResource(iconResId);
            title.setText(R.string.rsb_quiz_evaluation_great_job);

            if(step.getIncorrect() == 0)
            {
                summary.setText(step.getQuizModel().getSuccessMessage());
            }
            else
            {
                summary.setText(step.getQuizModel().getSuccessMessage());
            }

            submitBar.setPositiveTitle(R.string.rsb_next);
        }
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    public void receiveIntentExtraOnResult(int requestCode, Intent intent) {

    }
}
