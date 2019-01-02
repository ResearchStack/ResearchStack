package org.researchstack.foundation.components.survey.ui.body;

import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.foundation.R;
import org.researchstack.foundation.components.survey.answerformat.TextAnswerFormat;
import org.researchstack.foundation.components.survey.step.QuestionStep;
import org.researchstack.foundation.components.utils.TextUtils;
import org.researchstack.foundation.components.utils.ViewUtils;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;


public class TextQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<String> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText editText;

    public TextQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View body = inflater.inflate(R.layout.rsf_item_edit_text_compact, parent, false);

        editText = (EditText) body.findViewById(R.id.value);
        if (step.getPlaceholder() != null) {
            editText.setHint(step.getPlaceholder());
        } else {
            editText.setHint(R.string.rsf_hint_step_body_text);
        }

        TextView title = (TextView) body.findViewById(R.id.label);

        if (viewType == VIEW_TYPE_COMPACT) {
            title.setText(step.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        // Restore previous result
        String stringResult = result.getResult();
        if (!TextUtils.isEmpty(stringResult)) {
            editText.setText(stringResult);
        }

        // Set result on text change
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                result.setResult(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Format EditText from TextAnswerFormat
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        editText.setSingleLine(!format.isMultipleLines());

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH) {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsf_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsf_margin_right);
        body.setLayoutParams(layoutParams);

        return body;
    }


    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();
        if (!format.isAnswerValid(editText.getText().toString())) {
            return BodyAnswer.INVALID;
        }

        return BodyAnswer.VALID;
    }

}
