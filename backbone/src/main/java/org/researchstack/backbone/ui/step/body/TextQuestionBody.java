package org.researchstack.backbone.ui.step.body;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LocalizationUtils;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ViewUtils;

public class TextQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<String> result;
    private Context context;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private TextInputLayout textEntry;

    public TextQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View body = inflater.inflate(R.layout.rsb_item_edit_text_compact, parent, false);
        context = body.getContext();
        textEntry = body.findViewById(R.id.value);
        if (viewType == VIEW_TYPE_COMPACT) {
            textEntry.setHint(step.getTitle());
        }

        // Restore previous result
        String stringResult = result.getResult();
        if (!TextUtils.isEmpty(stringResult)) {
            textEntry.getEditText().setText(stringResult);
        }

        // Set result on text change
        textEntry.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (textEntry.isErrorEnabled()) {
                    textEntry.setErrorEnabled(false);
                    textEntry.getEditText().setTextColor(ContextCompat.getColor(context, R.color.black_87));
                }
                result.setResult(s.toString());
            }
        });

        // Format EditText from TextAnswerFormat
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        textEntry.getEditText().setSingleLine(!format.isMultipleLines());

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH) {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(textEntry.getEditText().getFilters(), maxLengthFilter);
            textEntry.getEditText().setFilters(filters);
        }

        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
        if (!format.isAnswerValid(textEntry.getEditText().getText().toString())) {
            textEntry.setError(LocalizationUtils.getLocalizedString(context, R.string.rsb_consent_text_question_error));
            textEntry.getEditText().setTextColor(ContextCompat.getColor(context, R.color.red_scarlet));
            return BodyAnswer.INVALID;
        }

        return BodyAnswer.VALID;
    }
}