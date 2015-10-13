package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import co.touchlab.touchkit.rk.common.step.Step;

public class QuestionStepFragment extends StepFragment
{
    public static final String KEY_QUESTION_STEP = "KEY_STEP";
    public static final String KEY_STEP_RESULT = "KEY_STEP_RESULT";
    private QuestionStep step;
    private StepResult<QuestionResult<Boolean>> stepResult;

    public QuestionStepFragment()
    {
    }

    public static Fragment newInstance(QuestionStep step, StepResult<QuestionResult<Boolean>> result)
    {
        QuestionStepFragment fragment = new QuestionStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        args.putSerializable(KEY_STEP_RESULT,
                result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        step = (QuestionStep) getArguments().getSerializable(KEY_QUESTION_STEP);
        stepResult = (StepResult<QuestionResult<Boolean>>) getArguments().getSerializable(KEY_STEP_RESULT);

        if (stepResult == null)
        {
            stepResult = new StepResult<QuestionResult<Boolean>>(step.getIdentifier());
        }
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
    public Step getStep()
    {
        return step;
    }

    @Override
    protected StepResult getStepResult()
    {
        return stepResult;
    }
}
