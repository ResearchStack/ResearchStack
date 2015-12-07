package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.TextChoice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;

public class SingleChoiceQuestionScene<T> extends SceneImpl<T>
{

    private RadioGroup radioGroup;

    public SingleChoiceQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        radioGroup = new RadioGroup(getContext());

        TextChoiceAnswerFormat answerFormat = (TextChoiceAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();
        final TextChoice<T>[] textChoices = answerFormat.getTextChoices();
        StepResult<T> result = getStepResult();
        T resultValue = result.getResultForIdentifier(StepResult.DEFAULT_KEY);

        for (int i = 0; i < textChoices.length; i++)
        {
            TextChoice textChoice = textChoices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio,
                    radioGroup, false);
            radioButton.setText(textChoice.getText());
            radioButton.setId(i);
            radioGroup.addView(radioButton);

            if (resultValue != null)
            {
                radioButton.setChecked(resultValue.equals(textChoice.getValue()));
            }
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            TextChoice<T> textChoice = textChoices[checkedId];
            result.setResultForIdentifier(StepResult.DEFAULT_KEY, textChoice.getValue());
            setStepResult(result);
        });

        return radioGroup;
    }

    @Override
    public boolean isAnswerValid()
    {
        return radioGroup.getCheckedRadioButtonId() != -1;
    }

}
