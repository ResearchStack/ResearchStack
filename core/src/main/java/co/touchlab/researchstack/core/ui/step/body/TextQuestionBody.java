package co.touchlab.researchstack.core.ui.step.body;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.utils.ViewUtils;

public class TextQuestionBody implements StepBody
{
    private QuestionStep step;
    private EditText     editText;

    public TextQuestionBody()
    {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        editText = (EditText) inflater.inflate(R.layout.item_edit_text, parent, false);

        setUpEditText(step);

        return editText;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item_editable, parent, false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        editText = (EditText) formItemView.findViewById(R.id.value);

        setUpEditText(step);

        return formItemView;
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<String> result = new StepResult<>(step.getIdentifier());
        result.setResult(editText.getText().toString());
        return result;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        String stringResult = (String) result.getResult();
        if(! TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        return ((TextAnswerFormat) step.getAnswerFormat()).isAnswerValid(editText.getText()
                .toString());
    }

    private void setUpEditText(QuestionStep step)
    {
        this.step = step;
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        editText.setSingleLine(! format.isMultipleLines());

        if(format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH)
        {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(format.getMaximumLength());
            InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }
    }
}
