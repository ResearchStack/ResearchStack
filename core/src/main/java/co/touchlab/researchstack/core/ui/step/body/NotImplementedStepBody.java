package co.touchlab.researchstack.core.ui.step.body;

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
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        TextView textView = new TextView(inflater.getContext());
        textView.setText("Not implemented: " + step.getQuestionType().toString());
        return textView;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
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
    public void prefillResult(StepResult result)
    {

    }

    @Override
    public boolean isAnswerValid()
    {
        return true;
    }

}
