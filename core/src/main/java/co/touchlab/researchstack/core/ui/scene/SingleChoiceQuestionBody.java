package co.touchlab.researchstack.core.ui.scene;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class SingleChoiceQuestionBody<T> implements StepBody
{
    private StepResult<T> stepResult;

    private RadioGroup radioGroup;

    public SingleChoiceQuestionBody()
    {
    }

    private StepResult<T> createStepResult(String identifier)
    {
        return new StepResult<>(identifier);
    }

    @Override
    public View initialize(LayoutInflater inflater, ViewGroup parent, QuestionStep step, StepResult result)
    {

        if (result == null)
        {
            result = createStepResult(step.getIdentifier());
        }

        stepResult = result;

        // TODO inflate this?
        radioGroup = new RadioGroup(inflater.getContext());

        ChoiceAnswerFormat answerFormat = (ChoiceAnswerFormat) step.getAnswerFormat();
        final Choice<T>[] choices = answerFormat.getChoices();
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
    public StepResult getStepResult()
    {
        return stepResult;
    }

    @Override
    public boolean isAnswerValid()
    {
        return radioGroup.getCheckedRadioButtonId() != -1;
    }
}
