package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;

public class IntegerQuestionScene extends SceneImpl<Integer>
{

    public IntegerQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {

        NumberPicker numberPicker = (NumberPicker) inflater.inflate(R.layout.item_number_picker,
                null);
        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) ((QuestionStep) getStep())
                .getAnswerFormat();

        numberPicker.setMinValue(answerFormat.getMinValue());

        // if max and min are equal, don't set a max (it's 0/0 if they don't set min/max)
        if (answerFormat.getMaxValue() != answerFormat.getMinValue())
        {
            numberPicker.setMaxValue(answerFormat.getMaxValue());
        }

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            StepResult<Integer> result = getStepResult();
            result.setResultForIdentifier(StepResult.DEFAULT_KEY, newVal);
            setStepResult(result);
        });

        StepResult<Integer> result = getStepResult();
        if (result != null)
        {
            Integer answer = result.getResultForIdentifier(StepResult.DEFAULT_KEY);
            if (answer != null)
            {
                numberPicker.setValue(answer);
            }
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
