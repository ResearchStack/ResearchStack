package co.touchlab.researchstack.core.ui.step.body;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class IntegerQuestionBody implements StepBody
{
    private QuestionStep        step;
    private IntegerAnswerFormat format;
    private NumberPicker numberPicker;
    private EditText     editText;

    public IntegerQuestionBody()
    {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        this.step = step;
        format = (IntegerAnswerFormat) step.getAnswerFormat();

        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) step.getAnswerFormat();

        numberPicker = (NumberPicker) inflater.inflate(R.layout.item_number_picker, parent, false);
        numberPicker.setMinValue(answerFormat.getMinValue());

        // if max and min are equal, don't set a max (it's 0/0 if they don't set min/max)
        if(answerFormat.getMaxValue() != answerFormat.getMinValue())
        {
            numberPicker.setMaxValue(answerFormat.getMaxValue());
        }

        return numberPicker;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        // TODO do we need both Step and AnswerFormat?
        this.step = step;
        format = (IntegerAnswerFormat) step.getAnswerFormat();

        View formItemView = inflater.inflate(R.layout.scene_form_item_editable, parent, false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        editText = (EditText) formItemView.findViewById(R.id.value);
        editText.setSingleLine(true);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        return formItemView;
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<Object> stepResult = new StepResult<>(step.getIdentifier());
        if(editText != null)
        {
            stepResult.setResult(Integer.valueOf(editText.getText().toString()));
        }
        else
        {
            stepResult.setResult(numberPicker.getValue());
        }
        return stepResult;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        if(result.getResult() == null)
        {
            return;
        }

        if(numberPicker != null)
        {
            numberPicker.setValue((Integer) result.getResult());
        }
        else
        {
            editText.setText(String.valueOf(result.getResult()));
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        Integer result = null;

        if(numberPicker != null)
        {
            result = numberPicker.getValue();
        }
        else if(editText != null && editText.getText().length() > 0)
        {
            result = Integer.valueOf(editText.getText().toString());
        }

        if(result == null)
        {
            return false;
        }

        return result >= format.getMinValue() && result <= format.getMaxValue();
    }

}
