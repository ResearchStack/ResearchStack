package co.touchlab.researchstack.core.ui.scene;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class TextQuestionBody implements StepBody
{
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
        // TODO add validation for length as well
        String result = stepResult.getResult();
        return result != null && result.length() > 0;
    }

    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult result)
    {
        if (result == null)
        {
            result = createStepResult(StepResult.DEFAULT_KEY);
        }

        stepResult = (StepResult<String>) result;

        // TODO set max/min length, multiline, etc. using answer format
        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text,
                parent,
                false);

        String stringResult = (String) result.getResult();
        if (!TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }

        RxTextView.textChanges(editText)
                .subscribe(s -> {
                    stepResult.setResult(s.toString());
                });

        return editText;
    }
}
