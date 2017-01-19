package org.researchstack.backbone.ui.step.body;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MultiChoiceQuestionBody<T> implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<T[]> result;
    private ChoiceAnswerFormat format;
    private Choice<T>[] choices;
    private Set<T> currentSelected;

    public MultiChoiceQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (ChoiceAnswerFormat) this.step.getAnswerFormat();
        this.choices = format.getChoices();

        // Restore results
        currentSelected = new HashSet<>();

        T[] resultArray = this.result.getResult();
        if (resultArray != null && resultArray.length > 0) {
            currentSelected.addAll(Arrays.asList(resultArray));
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
        RadioGroup radioGroup = new RadioGroup(inflater.getContext());
        radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        radioGroup.setDividerDrawable(ContextCompat.getDrawable(parent.getContext(),
                R.drawable.rsb_divider_empty_8dp));

        for (int i = 0; i < choices.length; i++) {
            Choice<T> item = choices[i];

            // Create & add the View to our body-view
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(R.layout.rsb_item_checkbox,
                    radioGroup,
                    false);
            checkBox.setText(item.getText());
            checkBox.setId(i);
            radioGroup.addView(checkBox);

            // Set initial state
            if (currentSelected.contains(item.getValue())) {
                checkBox.setChecked(true);
            }

            // Update result when value changes
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    currentSelected.add(item.getValue());
                } else {
                    currentSelected.remove(item.getValue());
                }
            });
        }

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
            currentSelected.clear();
            result.setResult((T[]) currentSelected.toArray());
        } else {
            result.setResult((T[]) currentSelected.toArray());
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (currentSelected.isEmpty()) {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
        } else {
            return BodyAnswer.VALID;
        }
    }

}