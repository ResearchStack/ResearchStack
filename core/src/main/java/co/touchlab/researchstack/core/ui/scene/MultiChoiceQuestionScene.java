package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.TextChoice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;

public class MultiChoiceQuestionScene<T> extends SceneImpl<T[]>
{

    private List<T> results;

    public MultiChoiceQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        // TODO this whole thing needs a lot of refactoring, plus it could probably just be combined
        // TODO with single choice questions
        TextChoiceAnswerFormat answerFormat = (TextChoiceAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();
        RadioGroup radioGroup = new RadioGroup(getContext());
        final TextChoice<T>[] textChoices = answerFormat.getTextChoices();

        results = new ArrayList<>();

        StepResult<T[]> result = getStepResult();

        for (int i = 0; i < textChoices.length; i++)
        {
            int position = i;

            TextChoice<T> textChoice = textChoices[position];
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(
                    R.layout.item_checkbox, radioGroup, false);
            checkBox.setText(textChoice.getText());
            checkBox.setId(position);
            radioGroup.addView(checkBox);

            String valueString = String.valueOf(textChoice.getValue());

            if (result.getResultForIdentifier(valueString) != null)
            {
                checkBox.setChecked(true);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked)
                {
                    results.add(textChoice.getValue());
                }
                else
                {
                    results.remove(textChoice.getValue());
                }

                result.setResultForIdentifier(StepResult.DEFAULT_KEY, (T[])results.toArray());
                setStepResult(result);
            });
        }

        return radioGroup;
    }

    @Override
    public StepResult<T[]> createNewStepResult(String stepIdentifier)
    {
        return new StepResult<>(stepIdentifier);
    }

    @Override
    public boolean isAnswerValid()
    {
        return !getStepResult().isEmpty();
    }
}