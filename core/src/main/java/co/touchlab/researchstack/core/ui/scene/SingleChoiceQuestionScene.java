package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
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

public class SingleChoiceQuestionScene<T> extends SceneImpl<T>
{

    private RadioGroup radioGroup;

    public SingleChoiceQuestionScene(Context context)
    {
        super(context);
    }

    public SingleChoiceQuestionScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SingleChoiceQuestionScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        radioGroup = new RadioGroup(getContext());

        ChoiceAnswerFormat answerFormat = (ChoiceAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();
        final Choice<T>[] choices = answerFormat.getChoices();
        StepResult<T> result = getStepResult();
        T resultValue = result.getResultForIdentifier(StepResult.DEFAULT_KEY);

        for (int i = 0; i < choices.length; i++)
        {
            Choice choice = choices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio,
                    radioGroup, false);
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
            result.setResult(choice.getValue());
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
