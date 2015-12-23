package co.touchlab.researchstack.core.ui.scene;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class MultiChoiceQuestionBody<T> implements StepBody
{
    private List<T> results;

    private StepResult<T[]> stepResult;

    private RadioGroup radioGroup;

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult result)
    {

        results = new ArrayList<>();
        if (result == null)
        {
            result = createStepResult(step.getIdentifier());
        }
        else
        {
            T[] resultArray = (T[]) result.getResult();
            if (resultArray != null && resultArray.length > 0)
            {
                results.addAll(Arrays.asList(resultArray));
            }
        }
        stepResult = result;

        // TODO inflate this?
        radioGroup = new RadioGroup(inflater.getContext());

        ChoiceAnswerFormat answerFormat = (ChoiceAnswerFormat) step.getAnswerFormat();
        final Choice<T>[] choices = answerFormat.getChoices();

        for (int i = 0; i < choices.length; i++)
        {
            Choice<T> item = choices[i];

            // Create & add the View to our body-view
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(
                    R.layout.item_checkbox,
                    radioGroup,
                    false);
            checkBox.setText(item.getText());
            checkBox.setId(i);
            radioGroup.addView(checkBox);

            // Set initial state
            if (results.contains(item.getValue()))
            {
                checkBox.setChecked(true);
            }

            // Update result when value changes
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked)
                {
                    results.add(item.getValue());
                }
                else
                {
                    results.remove(item.getValue());
                }

                stepResult.setResult((T[]) results.toArray());
            });
        }

        return radioGroup;
    }

    private StepResult<T[]> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public StepResult getStepResult()
    {
        return stepResult;
    }

    @Override
    public boolean isAnswerValid()
    {
        return !getStepResult().isEmpty();
    }
}