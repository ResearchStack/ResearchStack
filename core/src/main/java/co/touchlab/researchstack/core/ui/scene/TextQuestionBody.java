package co.touchlab.researchstack.core.ui.scene;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class TextQuestionBody implements StepBody
{
    private QuestionStep       step;
    private StepResult<String> stepResult;

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
    public boolean isAnswerValid()
    {
        TextAnswerFormat answerFormat = (TextAnswerFormat) step.getAnswerFormat();
        String result = stepResult.getResult();
        return answerFormat.isAnswerValidWithString(result);
    }

    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult result)
    {
        if (result == null)
        {
            result = createStepResult(StepResult.DEFAULT_KEY);
        }

        this.step = step;
        stepResult = (StepResult<String>) result;

        TextAnswerFormat answerFormat = (TextAnswerFormat) step.getAnswerFormat();

        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text, parent, false);
        editText.setSingleLine(! answerFormat.isMultipleLines());

        InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(
                answerFormat.getMaximumLength());
        InputFilter filters[] = insertFilter(editText.getFilters(), maxLengthFilter);
        editText.setFilters(filters);

        String stringResult = (String) result.getResult();
        if (!TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }

        RxTextView.textChanges(editText).subscribe(s -> {
            stepResult.setResult(s.toString());
        });

        return editText;
    }

    private InputFilter[] insertFilter(InputFilter[] filters, InputFilter filter)
    {
        if(filters == null || filters.length == 0)
        {
            return new InputFilter[] {filter};
        }
        else
        {
            // Overwrite value if the filter to be inserted already exists in the filters array
            for(int i = 0, size = filters.length; i < size; i++)
            {
                if(filters[i].getClass().isInstance(filter))
                {
                    filters[i] = filter;
                    return filters;
                }
            }

            // If our loop fails to find filter class type, create a new array and insert that
            // filter at the end of the array.
            int newSize = filters.length + 1;
            InputFilter newFilters[] = new InputFilter[newSize];
            System.arraycopy(filters, 0, newFilters, 0, filters.length);
            newFilters[newSize - 1] = filter;

            return newFilters;
        }
    }
}
