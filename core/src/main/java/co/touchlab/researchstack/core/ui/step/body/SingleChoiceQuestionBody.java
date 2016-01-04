package co.touchlab.researchstack.core.ui.step.body;

import android.app.AlertDialog;
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

public class SingleChoiceQuestionBody <T> implements StepBody
{
    private RadioGroup         radioGroup;
    private ChoiceAnswerFormat format;
    private Choice<T>[]        choices;
    private String identifier = StepResult.DEFAULT_KEY;
    private T        currentSelection;
    private TextView formLabel;

    public SingleChoiceQuestionBody()
    {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        // TODO inflate this?
        radioGroup = new RadioGroup(inflater.getContext());

        for(int i = 0; i < choices.length; i++)
        {
            Choice choice = choices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio,
                    radioGroup,
                    false);
            radioButton.setText(choice.getText());
            radioButton.setId(i);
            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Choice<T> choice = choices[checkedId];
            currentSelection = choice.getValue();
        });

        return radioGroup;
    }

    @Override
    public View initViewCompact(LayoutInflater inflater, ViewGroup parent, QuestionStep step)
    {
        View formItemView = inflater.inflate(R.layout.scene_form_item, parent, false);

        TextView label = (TextView) formItemView.findViewById(R.id.text);

        label.setText(step.getTitle());

        formLabel = (TextView) formItemView.findViewById(R.id.value);

        format = (ChoiceAnswerFormat) step.getAnswerFormat();
        choices = format.getChoices();

        RxView.clicks(formLabel).subscribe(o -> {
            showDialog(formLabel, step.getTitle());
        });

        return formItemView;
    }

    @Override
    public StepResult getStepResult()
    {
        StepResult<T> result = new StepResult<>(identifier);
        result.setResult(currentSelection);
        return result;
    }

    @Override
    public void prefillResult(StepResult result)
    {
        T resultValue = (T) result.getResult();

        if(resultValue == null)
        {
            return;
        }


        if(radioGroup != null)
        {
            // Full body view
            // TODO precheck current choice
            //            radioButton.setChecked(resultValue.equals(choice.getValue()));
        }
        else
        {
            // Compact form view
            for(Choice<T> choice : choices)
            {
                if(choice.getValue().equals(resultValue))
                {
                    currentSelection = choice.getValue();
                    formLabel.setText(choice.getText());
                }
            }
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        return currentSelection != null;
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

    private void showDialog(TextView textView, String title)
    {
        // TODO use same view as initView() and just set the dialog's view to it?
        int[] checked = new int[1];
        new AlertDialog.Builder(textView.getContext()).setSingleChoiceItems(format.getTextChoiceNames(),
                0,
                (dialog, which) -> {
                    checked[0] = which;
                }).setTitle(title).setPositiveButton(R.string.src_ok, (dialog, which) -> {
            // TODO this array of one this is weird, revisit
            Choice<T> choice = choices[checked[0]];
            currentSelection = choice.getValue();
            textView.setText(choice.getText());
        }).setNegativeButton(R.string.src_cancel, null).show();
    }
}
