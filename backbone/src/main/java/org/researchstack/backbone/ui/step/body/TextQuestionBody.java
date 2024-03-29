package org.researchstack.backbone.ui.step.body;

import androidx.annotation.DimenRes;
import androidx.annotation.LayoutRes;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.backbone.utils.ViewUtils;

public class TextQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected QuestionStep step;
    protected StepResult<String> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    protected EditText editText;
    public EditText getEditText() {
        return editText;
    }

    public TextQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
    }

    public @LayoutRes int getBodyViewRes() {
        return R.layout.rsb_item_edit_text_compact;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        View body = inflater.inflate(getBodyViewRes(), parent, false);

        // Format EditText from TextAnswerFormat
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        editText = (EditText) body.findViewById(R.id.value);
        if (step.getPlaceholder() != null) {
            editText.setHint(step.getPlaceholder());
        } else if (format.getHintText() != null) {
            editText.setHint(format.getHintText());
        }
        editText.setEnabled(true);
        if (format.isDisabled()) {
            editText.setEnabled(false);
        }

        TextView title = (TextView) body.findViewById(R.id.label);

        // TODO: naming is confusing... compact means less, but this adds a view -MDP
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
        RxTextView.textChanges(editText).subscribe(text -> {
            result.setResult(text.toString());
        });

        if(format.isMultipleLines()) {
            editText.setSingleLine(false);
            editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setHorizontallyScrolling(false);
            editText.setLines(5);
        } else {
            editText.setSingleLine(false);
        }

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH) {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

        editText.setInputType(format.getInputType());

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
