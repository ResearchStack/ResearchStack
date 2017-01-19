package org.researchstack.backbone.ui.step.body;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

public class SingleChoiceQuestionBody<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<T> result;
    private ChoiceAnswerFormat format;
    private Choice<T>[] choices;
    private T currentSelected;

    public SingleChoiceQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (ChoiceAnswerFormat) this.step.getAnswerFormat();
        this.choices = format.getChoices();

        // Restore results
        T resultValue = this.result.getResult();
        if (resultValue != null) {
            for (Choice<T> choice : choices) {
                if (choice.getValue().equals(resultValue)) {
                    currentSelected = choice.getValue();
                }
            }
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            return initViewDefault(inflater, parent);
        } else if (viewType == VIEW_TYPE_COMPACT) {
            return initViewCompact(inflater, parent);
        } else {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initViewDefault(LayoutInflater inflater, ViewGroup parent) {
        RadioGroup radioGroup = new RadioGroup(parent.getContext());
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        radioGroup.setDividerDrawable(ContextCompat.getDrawable(parent.getContext(),
                R.drawable.rsb_divider_empty_8dp));

        for (int i = 0; i < choices.length; i++) {
            Choice choice = choices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.rsb_item_radio,
                    radioGroup,
                    false);
            radioButton.setText(choice.getText());
            radioButton.setId(i);

            if (currentSelected != null) {
                radioButton.setChecked(currentSelected.equals(choice.getValue()));
            }

            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Choice<T> choice = choices[checkedId];
            currentSelected = choice.getValue();
        });

        return radioGroup;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

        TextView label = (TextView) inflater.inflate(R.layout.rsb_item_text_view_title_compact,
                compactView,
                false);
        label.setText(step.getTitle());

        compactView.addView(label, 0);

        return compactView;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            result.setResult(currentSelected);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (currentSelected == null) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
        } else {
            return BodyAnswer.VALID;
        }
    }

}
