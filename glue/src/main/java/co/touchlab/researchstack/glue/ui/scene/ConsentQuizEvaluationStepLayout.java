package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.util.AttributeSet;

import co.touchlab.researchstack.core.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.core.utils.ResUtils;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.step.ConsentQuizEvaluationStep;

public class ConsentQuizEvaluationStepLayout extends StepLayoutImpl<Boolean>
{

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
    public void initializeScene()
    {
        super.initializeScene();

        ConsentQuizEvaluationStep step = (ConsentQuizEvaluationStep) getStep();

        // We have failed
        if(! step.isQuizPassed())
        {
            int iconResId = ResUtils.getDrawableResourceId(getContext(),
                    step.getQuestionProperties().incorrectIcon);

            setImage(iconResId);
            setTitle(R.string.rsc_quiz_evaluation_try_again);

            if(! step.isOverMaxAttempts())
            {
                setSummary(step.getQuestionProperties().quizFailure1Text);
                setNextButtonText(R.string.rsc_quiz_evaluation_retake);
            }
            else
            {
                setSummary(step.getQuestionProperties().quizFailure2Text);
                setNextButtonText(R.string.rsc_quiz_evaluation_review_consent);
            }
        }

        // We have passed
        else
        {
            int iconResId = ResUtils.getDrawableResourceId(getContext(),
                    step.getQuestionProperties().correctIcon);

            setImage(iconResId);
            setTitle(R.string.rsc_quiz_evaluation_great_job);

            if(step.getIncorrect() == 0)
            {
                setSummary(step.getQuestionProperties().quizAllCorrectText);
            }
            else
            {
                setSummary(step.getQuestionProperties().quizPassedText);
            }

            setNextButtonText(R.string.rsc_next);
        }
    }

}
