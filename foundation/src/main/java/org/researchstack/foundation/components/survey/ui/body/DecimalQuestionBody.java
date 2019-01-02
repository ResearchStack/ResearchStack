package org.researchstack.backbone.ui.step.body;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.DecimalAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ViewUtils;

public class DecimalQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<Float> result;
    private DecimalAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int viewType;
    private EditText editText;

    public DecimalQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (DecimalAnswerFormat) this.step.getAnswerFormat();
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        this.viewType = viewType;

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
        editText = (EditText) inflater.inflate(R.layout.rsb_item_edit_text, parent, false);
        setFilters(parent.getContext());

        return editText;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        View formItemView = inflater.inflate(R.layout.rsb_item_edit_text_compact, parent, false);

        TextView title = (TextView) formItemView.findViewById(R.id.label);
        title.setText(step.getTitle());

        editText = (EditText) formItemView.findViewById(R.id.value);
        setFilters(parent.getContext());

        return formItemView;
    }

    private void setFilters(Context context) {
        editText.setSingleLine(true);
        final float minValue = format.getMinValue();
        // allow any positive int if no max value is specified
        final float maxValue = format.getMaxValue() == 0 ? Float.MAX_VALUE : format.getMaxValue();

        if (step.getPlaceholder() != null) {
            editText.setHint(step.getPlaceholder());
        } else if (maxValue == Integer.MAX_VALUE) {
            editText.setHint(context.getString(R.string.rsb_hint_step_body_int_no_max));
        } else {
            editText.setHint(context.getString(R.string.rsb_hint_step_body_dec,
                    minValue,
                    maxValue));
        }

        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);

        if (result.getResult() != null) {
            editText.setText(String.valueOf(result.getResult()));
        }

        String minStr = Float.toString(minValue);
        String maxStr = Float.toString(maxValue);
        int maxLength = maxStr.length() >= minStr.length() ? maxStr.length() : minStr.length();
        InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(maxLength);
        InputFilter[] newFilters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
        editText.setFilters(newFilters);
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            String numString = editText.getText().toString();
            if (!TextUtils.isEmpty(numString)) {
                result.setResult(Float.valueOf(editText.getText().toString()));
            }
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (editText == null) {
            return BodyAnswer.INVALID;
        }

        return format.validateAnswer(editText.getText().toString());
    }

}
