package org.researchstack.skin.ui.layout;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.skin.R;
import org.researchstack.skin.step.ConsentQuizEvaluationStep;

public class ConsentQuizEvaluationStepLayout extends RelativeLayout implements StepLayout
{

    private ConsentQuizEvaluationStep step;
    private StepResult<Boolean>       result;
    private StepCallbacks callbacks;

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
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        initializeStep();
    }

    private void initializeStep()
    {
        LayoutInflater.from(getContext())
                .inflate(R.layout.step_layout_consent_evaluation, this, true);

        ImageView image = (ImageView) findViewById(R.id.image);
        TextView title = (TextView) findViewById(R.id.title);
        TextView summary = (TextView) findViewById(R.id.summary);

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.getNegativeActionView().setVisibility(View.GONE);
        submitBar.setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                step,
                result));

        // We have failed
        if(! step.isQuizPassed())
        {
            int iconResId = ResUtils.getDrawableResourceId(getContext(),
                    step.getQuestionProperties().incorrectIcon);

            image.setImageResource(iconResId);
            title.setText(R.string.rsb_quiz_evaluation_try_again);

            if(! step.isOverMaxAttempts())
            {
                summary.setText(step.getQuestionProperties().quizFailure1Text);
                submitBar.setPositiveTitle(R.string.rsb_quiz_evaluation_retake);
            }
            else
            {
                summary.setText(step.getQuestionProperties().quizFailure2Text);
                submitBar.setPositiveTitle(R.string.rsb_quiz_evaluation_review_consent);
            }
        }

        // We have passed
        else
        {
            int iconResId = ResUtils.getDrawableResourceId(getContext(),
                    step.getQuestionProperties().correctIcon);

            image.setImageResource(iconResId);
            title.setText(R.string.rsb_quiz_evaluation_great_job);

            if(step.getIncorrect() == 0)
            {
                summary.setText(step.getQuestionProperties().quizAllCorrectText);
            }
            else
            {
                summary.setText(step.getQuestionProperties().quizPassedText);
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
}
