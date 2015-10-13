package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.Map;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.BooleanAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;

public class BooleanQuestionStepFragment extends QuestionStepFragment
{

    public BooleanQuestionStepFragment()
    {
        super();
    }

    public static Fragment newInstance(QuestionStep step, StepResult result)
    {
        BooleanQuestionStepFragment fragment = new BooleanQuestionStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        args.putSerializable(KEY_STEP_RESULT,
                result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        BooleanAnswerFormat answerFormat = (BooleanAnswerFormat) step.getAnswerFormat();
        RadioGroup radioGroup = new RadioGroup(getContext());
        final TextChoice[] textChoices = answerFormat.getTextChoices();

        for (int i = 0; i < textChoices.length; i++)
        {
            TextChoice textChoice = textChoices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_checkbox,
                    radioGroup,
                    false);
            radioButton.setText(textChoice.getText());
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                TextChoice textChoice = textChoices[checkedId];
                QuestionResult<Boolean> questionResult = new QuestionResult<Boolean>(step.getIdentifier());
                questionResult.setAnswer(textChoice.getValue());

                // TODO this is bad and we should feel bad
                Map<String, QuestionResult<Boolean>> results = new HashMap<>();
                results.put(questionResult.getIdentifier(),
                        questionResult);

                stepResult.setResults(results);
            }
        });

        return radioGroup;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
