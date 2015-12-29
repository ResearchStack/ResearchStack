package co.touchlab.researchstack.core.ui.scene;

import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class SingleChoiceQuestionBody<T> implements StepBody
{
    private StepResult<T> stepResult;

    private RadioGroup radioGroup;
    private ChoiceAnswerFormat format;
    private Choice<T>[] choices;

    public SingleChoiceQuestionBody()
    {
    }

    private StepResult<T> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, @Nullable StepResult result, @Nullable String identifier)
    {
        if (result == null)
        {
            result = createStepResult(identifier);
        }

        stepResult = (StepResult<T>) result;
        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        // TODO inflate this?
        radioGroup = new RadioGroup(inflater.getContext());

        T resultValue = stepResult.getResult();

        for (int i = 0; i < choices.length; i++)
        {
            Choice choice = choices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio,
                    radioGroup,
                    false);
            radioButton.setText(choice.getText());
            radioButton.setId(i);
            radioGroup.addView(radioButton);

            if (resultValue != null)
            {
                radioButton.setChecked(resultValue.equals(choice.getValue()));
            }
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Choice<T> choice = choices[checkedId];
            stepResult.setResult(choice.getValue());
        });

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

        stepResult = (StepResult<T>) result;
        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        if (stepResult.getResult() != null)
        {
            String choiceText = "";
            for (Choice<T> choice : choices)
            {
                if (choice.getValue()
                        .equals(stepResult.getResult()))
                {
                    choiceText = choice.getText();
                }
            }
            textView.setText(choiceText);
        }

        RxView.clicks(textView)
                .subscribe(o -> {
                    showDialog(textView,
                            step.getTitle());
                });

        return formItemView;
    }

    private void showDialog(TextView textView, String title)
    {
        int[] checked = new int[1];
        new AlertDialog.Builder(textView.getContext())
                .setSingleChoiceItems(format.getTextChoiceNames(),
                        0,
                        (dialog, which) -> {
                            checked[0] = which;
                        })
                .setTitle(title)
                .setPositiveButton(R.string.src_ok,
                        (dialog, which) -> {
                            // TODO this array of one this is weird, revisit
                            Choice<T> choice = choices[checked[0]];
                            stepResult.setResult(choice.getValue());
                            textView.setText(choice.getText());
                        })
                .setNegativeButton(R.string.src_cancel,
                        null)
                .show();
    }


    @Override
    public StepResult getStepResult()
    {
        return stepResult;
    }

    @Override
    public boolean isAnswerValid()
    {
        // TODO relook at validation here
        return stepResult.getResult() != null;
    }
}
