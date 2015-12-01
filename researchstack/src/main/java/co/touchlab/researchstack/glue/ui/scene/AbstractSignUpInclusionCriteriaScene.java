package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.researchstack.core.result.QuestionResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.Scene;

public abstract class AbstractSignUpInclusionCriteriaScene extends Scene
{

    public AbstractSignUpInclusionCriteriaScene(Context context, Step step)
    {
        super(context,
                step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(getLayoutId(),
                parent,
                false);
    }

    @Override
    public void onNextClicked()
    {
        QuestionResult<Boolean> questionResult = new QuestionResult<>(getStep().getIdentifier());
        questionResult.setAnswer(isEligible());
        setStepResult(questionResult);
        super.onNextClicked();
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }

    public abstract int getLayoutId();

    public abstract boolean isEligible();

    public abstract boolean isAnswerValid();

    public abstract void onBodyCreated(View body);
}
