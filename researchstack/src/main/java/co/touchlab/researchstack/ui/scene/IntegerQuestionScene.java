package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.QuestionStep;
import co.touchlab.researchstack.common.step.Step;

public class IntegerQuestionScene extends Scene
{

    public IntegerQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        QuestionResult<Integer> stringResult = (QuestionResult<Integer>)
                getStepResult().getResultForIdentifier(getStep().getIdentifier());;

        NumberPicker numberPicker = (NumberPicker) inflater.inflate(R.layout.item_number_picker,
                                                                    null);
        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();

        numberPicker.setMinValue(answerFormat.getMinValue());
        numberPicker.setMaxValue(answerFormat.getMaxValue());

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            QuestionResult<Integer> questionResult = new QuestionResult<Integer>(
                    getStep().getIdentifier());
            questionResult.setAnswer(newVal);
            setStepResult(questionResult);
        });

        if (stringResult != null)
        {
            numberPicker.setValue(stringResult.getAnswer());
        }

        return numberPicker;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<String>>(stepIdentifier);
    }
}
