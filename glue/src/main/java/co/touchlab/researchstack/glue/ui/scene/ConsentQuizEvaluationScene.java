package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.glue.step.ConsentQuizEvaluationStep;

public class ConsentQuizEvaluationScene extends SceneImpl<Boolean>
{

    public static final String KEY_RESULT_PASS = "KEY_RESULT_PASS";
    public static final String KEY_RESULT_EXCEED_ATTEMPS = "KEY_RESULT_EXCEED_ATTEMPS";

    private ConsentQuizModel.EvaluationProperties properties;
    private int attempt;
    private int incorrect;

    public ConsentQuizEvaluationScene(Context context)
    {
        super(context);
    }

    public ConsentQuizEvaluationScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentQuizEvaluationScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.properties = ((ConsentQuizEvaluationStep)step).getQuestionProperties();
        this.attempt = ((ConsentQuizEvaluationStep)step).getAttempt();
        this.incorrect = ((ConsentQuizEvaluationStep)step).getIncorrect();

        super.initialize(step, result);
    }

    @Deprecated
    public void initialize(Step step, ConsentQuizModel.EvaluationProperties properties, int attempt, int incorrect)
    {
        this.properties = properties;
        this.attempt = attempt;
        this.incorrect = incorrect;
        super.initialize(step);
    }

    @Override
    public void initializeScene()
    {
        super.initializeScene();

        Resources r = getContext().getResources();

        // We have failed
        if(incorrect >= properties.maxIncorrect)
        {
            int iconResId = r.getIdentifier(properties.incorrectIcon, "drawable",
                                            getContext().getPackageName());
            setImage(iconResId);
            setTitle(R.string.quiz_evaluation_try_again);

            if(attempt == 0)
            {
                setSummary(properties.quizFailure1Text);
                setNextButtonText(R.string.quiz_evaluation_retake);
            }
            else
            {
                setSummary(properties.quizFailure2Text);
                setNextButtonText(R.string.quiz_evaluation_review_consent);
            }
        }

        // We have passed
        else
        {

            int iconResId = r.getIdentifier(properties.correctIcon, "drawable",
                                            getContext().getPackageName());
            setImage(iconResId);
            setTitle(R.string.quiz_evaluation_great_job);

            if(incorrect == 0)
            {
                setSummary(properties.quizAllCorrectText);
            }
            else
            {
                setSummary(properties.quizPassedText);
            }

            setNextButtonText(R.string.next);
        }
    }

    @Override
    public StepResult<Boolean> getStepResult()
    {
        StepResult<Boolean> result = super.getStepResult();
        result.setResultForIdentifier(KEY_RESULT_PASS, incorrect < properties.maxIncorrect);
        result.setResultForIdentifier(KEY_RESULT_EXCEED_ATTEMPS, attempt >= 1);
        return result;
    }

}
