package org.researchstack.foundation.components.survey.ui.body;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.researchstack.foundation.components.survey.step.QuestionStep;
import org.researchstack.foundation.core.models.result.StepResult;


@Deprecated
public class NotImplementedStepBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;

    public NotImplementedStepBody(QuestionStep step, StepResult result) {
        this.step = step;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        Context ctx = parent.getContext();

        TextView textView = new TextView(ctx);
        textView.setText((viewType == VIEW_TYPE_COMPACT ? "form " : "") + "not implemented: " +
                step.getAnswerFormat().getQuestionType().toString());

        return textView;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        return null;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        return BodyAnswer.VALID;
    }

}
