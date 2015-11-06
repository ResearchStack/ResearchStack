package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.TextChoiceAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;

public class MultiChoiceQuestionStepFragment<T> extends StepFragment
{
    private List<T> results;

    public MultiChoiceQuestionStepFragment()
    {
        super();
    }

    public static <T> Fragment newInstance(QuestionStep step)
    {
        MultiChoiceQuestionStepFragment fragment = new MultiChoiceQuestionStepFragment<T>();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        // TODO this whole thing needs a lot of refactoring, plus it could probably just be combined
        // TODO with single choice questions
        TextChoiceAnswerFormat answerFormat = (TextChoiceAnswerFormat) ((QuestionStep) step).getAnswerFormat();
        RadioGroup radioGroup = new RadioGroup(getContext());
        final TextChoice<T>[] textChoices = answerFormat.getTextChoices();

        QuestionResult<T[]> questionResult = (QuestionResult<T[]>)
                stepResult.getResultForIdentifier(step.getIdentifier());

        results = new ArrayList<>();

        if (questionResult != null)
        {
            results.addAll(Arrays.asList(questionResult.getAnswer()));
        }

        for (int i = 0; i < textChoices.length; i++)
        {
            int position = i;
            TextChoice<T> textChoice = textChoices[position];
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(R.layout.item_checkbox,
                    radioGroup,
                    false);
            checkBox.setText(textChoice.getText());
            checkBox.setId(position);
            radioGroup.addView(checkBox);

            if (results.contains(textChoice.getValue()))
            {
                checkBox.setChecked(true);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                QuestionResult<T[]> questionResult1 = new QuestionResult<T[]>(
                        step.getIdentifier());
                if (isChecked)
                {
                    results.add(textChoice.getValue());
                }
                else
                {
                    results.remove(textChoice.getValue());
                }


                questionResult1.setAnswer((T[]) results.toArray());
                setStepResult(questionResult1);
            });
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

        });

        return radioGroup;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
