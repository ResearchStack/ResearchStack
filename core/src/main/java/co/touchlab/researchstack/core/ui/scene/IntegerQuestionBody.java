package co.touchlab.researchstack.core.ui.scene;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class IntegerQuestionBody implements StepBody
{
    private QuestionStep step;
    private StepResult<Integer> stepResult;
    private IntegerAnswerFormat format;

    public IntegerQuestionBody()
    {
    }

    private StepResult<Integer> createStepResult(String identifier)
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

        stepResult = (StepResult<Integer>) result;

        NumberPicker numberPicker = (NumberPicker) inflater
                .inflate(R.layout.item_number_picker,
                        parent,
                        false);

        if (result == null)
        {
            result = createStepResult(identifier);
        }

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        stepResult = (StepResult<Integer>) result;
        format = (IntegerAnswerFormat) step.getAnswerFormat();

        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) step.getAnswerFormat();

        numberPicker.setMinValue(answerFormat.getMinValue());

        // if max and min are equal, don't set a max (it's 0/0 if they don't set min/max)
        if (answerFormat.getMaxValue() != answerFormat.getMinValue())
        {
            numberPicker.setMaxValue(answerFormat.getMaxValue());
        }

        if (stepResult.getResult() != null)
        {
            numberPicker.setValue(stepResult.getResult());
        }

        numberPicker.setOnValueChangedListener(
                (picker, oldVal, newVal) -> stepResult.setResult(newVal));

        return numberPicker;
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

        if (result == null)
        {
            result = createStepResult(identifier);
        }

        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        stepResult = (StepResult<Integer>) result;
        format = (IntegerAnswerFormat) step.getAnswerFormat();

        Integer stringResult = stepResult.getResult();
        if (stringResult != null)
        {
            editText.setText(String.valueOf(stringResult));
        }

        editText.setSingleLine(true);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        RxTextView.textChanges(editText)
                .filter(charSequence -> charSequence.length() > 0)
                .subscribe(s -> {
                    stepResult.setResult(Integer.valueOf(s.toString()));
                });

        return formItemView;
    }

    @Override
    public boolean isAnswerValid()
    {
        Integer result = stepResult.getResult();

        if (result == null)
        {
            return false;
        }

        return result >= format.getMinValue() && result <= format.getMaxValue();
    }
}
