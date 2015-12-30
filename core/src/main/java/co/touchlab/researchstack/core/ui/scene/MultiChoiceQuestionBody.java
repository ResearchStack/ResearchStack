package co.touchlab.researchstack.core.ui.scene;

import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class MultiChoiceQuestionBody<T> implements StepBody
{
    private Set<T> results;

    private RadioGroup radioGroup;
    private ChoiceAnswerFormat format;
    private Choice<T>[] choices;
    private String identifier = StepResult.DEFAULT_KEY;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        results = new HashSet<>();
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
            });
        }

        return radioGroup;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        results = new HashSet<>();
        View formItemView = inflater.inflate(R.layout.scene_form_item,
                parent,
                false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        TextView textView = (TextView) formItemView.findViewById(R.id.value);

        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        RxView.clicks(textView)
                .subscribe(o -> {
                    showDialog(textView,
                            step.getTitle());
                });

        return formItemView;
    }

    private void showDialog(TextView textView, String title)
    {
        // TODO use same view as initView() and just set the dialog's view to it?
        // TODO use current result to precheck items
        // TODO improve this whole result/dialog logic
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
                            results.clear();
                            for (int i = 0; i < checkedItems.length; i++)
                            {
                                if (checkedItems[i])
                                {
                                    Choice<T> choice = choices[i];
                                    results.add(choice.getValue());
                                }
                            }
                            textView.setText("chosen");
                        })
                .setNegativeButton(R.string.src_cancel,
                        null)
                .show();
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<T[]> result = new StepResult<>(identifier);
        result.setResult((T[]) results.toArray());
        return result;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        T[] resultArray = (T[]) result.getResult();
        if (resultArray != null && resultArray.length > 0)
        {
            results.addAll(Arrays.asList(resultArray));
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        return !results.isEmpty();
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
}