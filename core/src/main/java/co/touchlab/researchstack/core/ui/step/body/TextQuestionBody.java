package co.touchlab.researchstack.core.ui.step.body;

import android.app.Activity;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.utils.ViewUtils;

public class TextQuestionBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep       step;
    private StepResult<String> result;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private EditText     editText;

    public TextQuestionBody(Step step, StepResult result)
    {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        InputMethodManager imm = (InputMethodManager) parent.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);

        View body = inflater.inflate(R.layout.item_edit_text, parent, false);

        TextView label = (TextView) body.findViewById(R.id.text);

        editText = (EditText) body.findViewById(R.id.value);

        if(viewType == VIEW_TYPE_COMPACT)
        {
            label.setVisibility(View.VISIBLE);
            label.setText(step.getTitle());

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus)
                {
                    ViewUtils.showSoftInputMethod(editText);
                }
            });
        }

        // Restore previous result
        String stringResult = result.getResult();
        if(! TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }

        // Set result on text change
        RxTextView.textChanges(editText).subscribe(text -> {
            result.setResult(text.toString());
        });

        // Format EditText from TextAnswerFormat
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        editText.setSingleLine(! format.isMultipleLines());

        if(format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH)
        {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsc_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsc_margin_right);
        body.setLayoutParams(layoutParams);

        return body;
    }


    @Override
    public StepResult getStepResult()
    {
        return result;
    }

    @Override
    public boolean isAnswerValid()
    {
        return ((TextAnswerFormat) step.getAnswerFormat()).isAnswerValid(editText.getText()
                .toString());
    }

}
