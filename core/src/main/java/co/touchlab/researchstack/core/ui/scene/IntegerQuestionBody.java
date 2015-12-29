package co.touchlab.researchstack.core.ui.scene;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class IntegerQuestionBody implements StepBody
{
    private QuestionStep        step;
    private StepResult<Integer> stepResult;

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

    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult result)
    {
        if (result == null)
        {
            result = createStepResult(StepResult.DEFAULT_KEY);
        }

        this.step = step;
        stepResult = (StepResult<Integer>) result;

        NumberPicker numberPicker = (NumberPicker) inflater
                .inflate(R.layout.item_number_picker,
                        parent,
                        false);

        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) step.getAnswerFormat();

        numberPicker.setMinValue(answerFormat.getMinValue());

        // if max and min are equal, don't set a max (it's 0/0 if they don't set min/max)
        if (answerFormat.getMaxValue() != answerFormat.getMinValue())
        {
            numberPicker.setMaxValue(answerFormat.getMaxValue());
        }

        numberPicker.setOnValueChangedListener(
                (picker, oldVal, newVal) -> stepResult.setResult(newVal));

        return numberPicker;
    }

    @Override
    public boolean isAnswerValid()
    {
        Integer result = stepResult.getResult();

        if (result == null)
        {
            return false;
        }

        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) step.getAnswerFormat();
        return result >= answerFormat.getMinValue() && result <= answerFormat.getMaxValue();
    }
}
