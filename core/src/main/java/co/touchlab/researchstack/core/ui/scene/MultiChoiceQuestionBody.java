package co.touchlab.researchstack.core.ui.scene;

import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

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
    private ChoiceAnswerFormat format;
    private Choice<T>[] choices;

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        results = new ArrayList<>();
        if (result == null)
        {
            result = createStepResult(identifier);
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
        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        // TODO inflate this?
        radioGroup = new RadioGroup(inflater.getContext());

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

    @Override
    public View initializeCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item,
                parent,
                false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        TextView textView = (TextView) formItemView.findViewById(R.id.value);

        if (result == null)
        {
            result = createStepResult(identifier);
        }

        stepResult = (StepResult<T[]>) result;
        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        if (stepResult.getResult() != null)
        {
            // TODO what should placeholder be?
//            textView.setText(choiceText);
        }

        RxView.clicks(textView)
                .subscribe(o -> {
                    showDialog(textView, step.getTitle());
                });

        return formItemView;
    }

    private void showDialog(TextView textView, String title)
    {
        boolean[] checkedItems = new boolean[format.getChoices().length];
        new AlertDialog.Builder(textView.getContext())
                .setMultiChoiceItems(format.getTextChoiceNames(),
                        checkedItems,
                        (dialog, which, isChecked) -> {
                            checkedItems[which] = isChecked;
                        })
                .setTitle(title)
                .setPositiveButton(R.string.src_ok,
                        (dialog, which) -> {
                            ArrayList<Integer> checkedValues = new ArrayList<Integer>();
                            for (int i = 0; i < checkedItems.length; i++)
                            {
                                if (checkedItems[i])
                                {
                                    checkedValues.add(i);
                                }
                            }
                            stepResult.setResult((T[]) checkedValues.toArray());
                            textView.setText("chosen");
                        })
                .setNegativeButton(R.string.src_cancel,
                        null)
                .show();
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
        // TODO revisit validation here
        return !getStepResult().isEmpty();
    }
}