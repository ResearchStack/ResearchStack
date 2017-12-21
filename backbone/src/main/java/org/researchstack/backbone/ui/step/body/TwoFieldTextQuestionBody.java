package org.researchstack.backbone.ui.step.body;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ViewUtils;

public class TwoFieldTextQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private StepResult<String> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText firstNameEditText;
    private EditText lastNameEditText;


    public TwoFieldTextQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View body = inflater.inflate(R.layout.rsb_item_edit_text_two_fields_compact, parent, false);

        firstNameEditText = (EditText) body.findViewById(R.id.value_name_first);
        if (step.getPlaceholder() != null) {
            firstNameEditText.setHint(step.getPlaceholder());
        } else {
            firstNameEditText.setHint(R.string.rsb_hint_step_body_text);
        }
        lastNameEditText = (EditText) body.findViewById(R.id.value_name_last);
        if (step.getPlaceholder() != null) {
            lastNameEditText.setHint(step.getPlaceholder());
        } else {
            lastNameEditText.setHint(R.string.rsb_hint_step_body_text);
        }

        // Restore previous result
        String stringResult = result.getResult();
        if (!TextUtils.isEmpty(stringResult)) {
            String[] names = stringResult.split(" ", 2);
            firstNameEditText.setText(names[0]);
            lastNameEditText.setText(names[1]);
        }

        // Set result on text change
        RxTextView.textChanges(firstNameEditText).subscribe(text -> {
            updateFirstName(text.toString());
        });
        RxTextView.textChanges(lastNameEditText).subscribe(text -> {
            updateLastName(text.toString());
        });

        // Format EditText from TextAnswerFormat
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH) {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(firstNameEditText.getFilters(), maxLengthFilter);
            firstNameEditText.setFilters(filters);
            lastNameEditText.setFilters(filters);
        }

        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        body.setLayoutParams(layoutParams);

        return body;
    }

    private void updateFirstName(String firstName) {
        result.setResult(firstName + " " + lastNameEditText.getText().toString());
    }

    private void updateLastName(String lastName) {
        result.setResult(firstNameEditText.getText().toString() + " " + lastName);
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
        if (!format.isAnswerValid(firstNameEditText.getText().toString())
                || !format.isAnswerValid(lastNameEditText.getText().toString())) {
            return BodyAnswer.INVALID;
        }
        return BodyAnswer.VALID;
    }
}
