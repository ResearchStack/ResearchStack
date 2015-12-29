package co.touchlab.researchstack.core.ui.scene;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

@Deprecated
public class NotImplementedStepBody implements StepBody
{
    public NotImplementedStepBody()
    {
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        TextView textView = new TextView(inflater.getContext());
        textView.setText("Not implemented: " + step.getQuestionType().toString());
        return textView;
    }

    @Override
    public View initializeCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        TextView textView = new TextView(inflater.getContext());
        textView.setText("Form not implemented: " + step.getQuestionType().toString());
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
