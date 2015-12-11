package co.touchlab.researchstack.glue.ui.scene;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;

public class ConsentQuizEvaluationScene extends SceneImpl
{

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
}
