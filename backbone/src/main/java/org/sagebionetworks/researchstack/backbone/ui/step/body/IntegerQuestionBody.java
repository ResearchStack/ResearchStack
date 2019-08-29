package org.sagebionetworks.researchstack.backbone.ui.step.body;

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

import org.sagebionetworks.researchstack.backbone.R;
import org.sagebionetworks.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.sagebionetworks.researchstack.backbone.answerformat.TextAnswerFormat;
import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.step.QuestionStep;
import org.sagebionetworks.researchstack.backbone.step.Step;
import org.sagebionetworks.researchstack.backbone.utils.TextUtils;
import org.sagebionetworks.researchstack.backbone.utils.ViewUtils;

public class IntegerQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected QuestionStep step;
    protected StepResult<Integer> result;
    protected IntegerAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected int viewType;
    protected EditText editText;

    public IntegerQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (IntegerAnswerFormat) this.step.getAnswerFormat();
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

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH) {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

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

    protected View initViewDefault(LayoutInflater inflater, ViewGroup parent) {
        editText = (EditText) inflater.inflate(R.layout.rsb_item_edit_text, parent, false);
        setFilters(parent.getContext());

        return editText;
    }

    protected View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
        View formItemView = inflater.inflate(R.layout.rsb_item_edit_text_compact, parent, false);

        TextView title = (TextView) formItemView.findViewById(R.id.label);
        title.setText(step.getTitle());

        editText = (EditText) formItemView.findViewById(R.id.value);
        setFilters(parent.getContext());

        return formItemView;
    }

    protected void setFilters(Context context) {
        editText.setSingleLine(true);
        final int minValue = format.getMinValue();
        // allow any positive int if no max value is specified
        final int maxValue = format.getMaxValue() == 0 ? Integer.MAX_VALUE : format.getMaxValue();

        if (step.getPlaceholder() != null) {
            editText.setHint(step.getPlaceholder());
        } else if (maxValue == Integer.MAX_VALUE) {
            editText.setHint(context.getString(R.string.rsb_hint_step_body_int_no_max));
        } else {
            editText.setHint(context.getString(R.string.rsb_hint_step_body_int,
                    minValue,
                    maxValue));
        }

        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        if (result.getResult() != null) {
            editText.setText(String.valueOf(result.getResult()));
        }

        String minStr = Integer.toString(minValue);
        String maxStr = Integer.toString(maxValue);
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
                result.setResult(Integer.valueOf(editText.getText().toString()));
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
