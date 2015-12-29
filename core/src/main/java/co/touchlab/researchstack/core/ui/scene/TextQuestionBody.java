package co.touchlab.researchstack.core.ui.scene;

import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class TextQuestionBody implements StepBody
{
    private QuestionStep step;
    private StepResult<String> stepResult;
    private TextAnswerFormat format;

    public TextQuestionBody()
    {
    }

    private StepResult<String> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public StepResult getStepResult()
    {
        return stepResult;
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text,
                parent,
                false);

        setUpEditText(result,
                identifier,
                editText,
                step);

        return editText;
    }

    @Override
    public View initializeCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item_editable,
                parent,
                false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        EditText editText = (EditText) formItemView.findViewById(R.id.value);

        setUpEditText(result,
                identifier,
                editText,
                step);

        return formItemView;
    }

    private void setUpEditText(@Nullable StepResult result, @Nullable String identifier, EditText editText, QuestionStep step)
    {
        if (result == null)
        {
            result = createStepResult(identifier);
        }

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        stepResult = (StepResult<String>) result;
        format = (TextAnswerFormat) step.getAnswerFormat();

        editText.setSingleLine(!format.isMultipleLines());

        if (format.getMaximumLength() > TextAnswerFormat.UNLIMITED_LENGTH)
        {
            InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(
                    format.getMaximumLength());
            InputFilter filters[] = insertFilter(editText.getFilters(), maxLengthFilter);
            editText.setFilters(filters);
        }

        String stringResult = stepResult.getResult();
        if (!TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }

        RxTextView.textChanges(editText)
                .subscribe(s -> {
                    stepResult.setResult(s.toString());
                });
    }

    @Override
    public boolean isAnswerValid()
    {
        String result = stepResult.getResult();
        return format.isAnswerValidWithString(result);
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
}
