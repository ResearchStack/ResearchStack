package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;

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
