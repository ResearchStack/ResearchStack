package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

@Deprecated
public class NotImplementedStepBody implements StepBody
{
    public NotImplementedStepBody()
    {
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult stepResult)
    {
        TextView textView = new TextView(inflater.getContext());
        textView.setText("Not implemented: " + step.getQuestionType().toString());
        return textView;
    }

    @Override
    public StepResult getStepResult()
    {
        return null;
    }

    @Override
    public boolean isAnswerValid()
    {
        return true;
    }
}
