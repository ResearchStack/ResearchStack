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

public class TextQuestionBody implements StepBody
{
    private QuestionStep step;
    private EditText editText;

    // TODO why does the result need to know its own identifier, is there a better way
    private String identifier = StepResult.DEFAULT_KEY;

    public TextQuestionBody()
    {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        editText = (EditText) inflater.inflate(R.layout.item_edit_text,
                parent,
                false);

        setUpEditText(step);

        return editText;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item_editable,
                parent,
                false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        editText = (EditText) formItemView.findViewById(R.id.value);

        setUpEditText(step);

        return formItemView;
    }

    private void setUpEditText(QuestionStep step)
    {
        this.step = step;
        TextAnswerFormat format = (TextAnswerFormat) step.getAnswerFormat();

        editText.setSingleLine(!format.isMultipleLines());

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH)
        {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(
                    format.getMaximumLength());
            InputFilter[] filters = insertFilter(editText.getFilters(),
                    maxLengthFilter);
            editText.setFilters(filters);
        }
    }

    private InputFilter[] insertFilter(InputFilter[] filters, InputFilter filter)
    {
        if (filters == null || filters.length == 0)
        {
            return new InputFilter[]{filter};
        }
        else
        {
            // Overwrite value if the filter to be inserted already exists in the filters array
            for (int i = 0, size = filters.length; i < size; i++)
            {
                if (filters[i].getClass()
                        .isInstance(filter))
                {
                    filters[i] = filter;
                    return filters;
                }
            }

            // If our loop fails to find filter class type, create a new array and insert that
            // filter at the end of the array.
            int newSize = filters.length + 1;
            InputFilter newFilters[] = new InputFilter[newSize];
            System.arraycopy(filters,
                    0,
                    newFilters,
                    0,
                    filters.length);
            newFilters[newSize - 1] = filter;

            return newFilters;
        }
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<String> result = new StepResult<>(identifier);
        result.setResult(editText.getText()
                .toString());
        return result;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        String stringResult = (String) result.getResult();
        if (!TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        return ((TextAnswerFormat) step.getAnswerFormat()).isAnswerValid(editText.getText().toString());
    }

    @Override
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }
}
