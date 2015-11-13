package co.touchlab.touchkit.rk.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.TextChoiceAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.step.Step;

public class SingleChoiceQuestionScene<T> extends Scene
{

    public SingleChoiceQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        TextChoiceAnswerFormat answerFormat = (TextChoiceAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();
        RadioGroup radioGroup = new RadioGroup(getContext());
        final TextChoice<T>[] textChoices = answerFormat.getTextChoices();

        QuestionResult<Boolean> questionResult = (QuestionResult<Boolean>)
                getStepResult().getResultForIdentifier(getStep().getIdentifier());

        for (int i = 0; i < textChoices.length; i++)
        {
            TextChoice textChoice = textChoices[i];
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio,
                                                                     radioGroup, false);
            radioButton.setText(textChoice.getText());
            radioButton.setId(i);
            radioGroup.addView(radioButton);

            if (questionResult != null)
            {
                radioButton.setChecked(questionResult.getAnswer() == textChoice.getValue());
            }
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            TextChoice<T> textChoice = textChoices[checkedId];
            QuestionResult<T> questionResult1 = new QuestionResult<T>(
                    getStep().getIdentifier());
            questionResult1.setAnswer(textChoice.getValue());
            setStepResult(questionResult1);
        });

        return radioGroup;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
