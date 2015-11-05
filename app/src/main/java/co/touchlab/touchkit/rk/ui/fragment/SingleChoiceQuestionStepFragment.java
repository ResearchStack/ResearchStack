package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.TextChoiceAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;

public class SingleChoiceQuestionStepFragment<T> extends StepFragment
{

    public SingleChoiceQuestionStepFragment()
    {
        super();
    }

    public static <T> Fragment newInstance(QuestionStep step)
    {
        SingleChoiceQuestionStepFragment fragment = new SingleChoiceQuestionStepFragment<T>();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {

        TextChoiceAnswerFormat answerFormat = (TextChoiceAnswerFormat) ((QuestionStep) step).getAnswerFormat();
        RadioGroup radioGroup = new RadioGroup(getContext());
        final TextChoice<T>[] textChoices = answerFormat.getTextChoices();

        QuestionResult<Boolean> questionResult = (QuestionResult<Boolean>)
                stepResult.getResultForIdentifier(step.getIdentifier());

        for (int i = 0; i < textChoices.length; i++)
        {
            TextChoice textChoice = textChoices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio,
                                                                     radioGroup, false);
            radioButton.setText(textChoice.getText());
            radioButton.setId(i);
            radioGroup.addView(radioButton);

            if (questionResult != null)
            {
                radioButton.setChecked(questionResult.getAnswer() == textChoice.getValue());
            }
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            TextChoice<T> textChoice = textChoices[checkedId];
            QuestionResult<T> questionResult1 = new QuestionResult<T>(
                    step.getIdentifier());
            questionResult1.setAnswer(textChoice.getValue());
            setStepResult(questionResult1);
        });

        return radioGroup;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
