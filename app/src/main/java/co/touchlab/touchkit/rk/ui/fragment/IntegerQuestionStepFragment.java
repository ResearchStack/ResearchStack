package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.IntegerAnswerFormat;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;

public class IntegerQuestionStepFragment extends StepFragment
{

    public IntegerQuestionStepFragment()
    {
        super();
    }

    public static Fragment newInstance(QuestionStep step)
    {
        IntegerQuestionStepFragment fragment = new IntegerQuestionStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {

        QuestionResult<Integer> stringResult = (QuestionResult<Integer>)
                stepResult.getResultForIdentifier(step.getIdentifier());;

        NumberPicker numberPicker = (NumberPicker) inflater.inflate(R.layout.item_number_picker,
                null);
        IntegerAnswerFormat answerFormat = (IntegerAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();

        numberPicker.setMinValue(answerFormat.getMinValue());
        numberPicker.setMaxValue(answerFormat.getMaxValue());

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            QuestionResult<Integer> questionResult = new QuestionResult<Integer>(
                    step.getIdentifier());
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
