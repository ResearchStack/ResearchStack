package co.touchlab.researchstack.core.ui.step.body;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

@Deprecated
public class NotImplementedStepBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;

    public NotImplementedStepBody(QuestionStep step, StepResult result)
    {
        this.step = step;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        Context ctx = parent.getContext();

        TextView textView = new TextView(ctx);
        textView.setText((viewType == VIEW_TYPE_COMPACT ? "form " : "") + "not implemented: " +
                step.getQuestionType().toString());

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
