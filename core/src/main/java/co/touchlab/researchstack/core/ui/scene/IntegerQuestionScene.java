package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.step.QuestionStep;

public class IntegerQuestionScene extends SceneImpl<Integer>
{

    public IntegerQuestionScene(Context context)
    {
        super(context);
    }

    public IntegerQuestionScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public IntegerQuestionScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        NumberPicker numberPicker = (NumberPicker) inflater
                .inflate(R.layout.item_number_picker, parent, false);

        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) ((QuestionStep) getStep())
                .getAnswerFormat();

        numberPicker.setMinValue(answerFormat.getMinValue());

        // if max and min are equal, don't set a max (it's 0/0 if they don't set min/max)
        if (answerFormat.getMaxValue() != answerFormat.getMinValue())
        {
            numberPicker.setMaxValue(answerFormat.getMaxValue());
        }

        numberPicker.setOnValueChangedListener(
                (picker, oldVal, newVal) -> getStepResult().setResult(newVal));

        Integer answer = getStepResult().getResult();
        if (answer != null)
        {
            numberPicker.setValue(answer);
        }
        else
        {
            getStepResult().setResult(numberPicker.getValue());
        }

        return numberPicker;
    }

    @Override
    public boolean isAnswerValid()
    {
        // max/min already ensures a valid number, this may change if we start with null
        return true;
    }
}
