package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.answerformat.TextChoiceAnswerFormat;
import co.touchlab.researchstack.common.helpers.TextChoice;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.QuestionStep;
import co.touchlab.researchstack.common.step.Step;

public class MultiChoiceQuestionScene <T> extends Scene
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

        QuestionResult<T[]> questionResult = (QuestionResult<T[]>)
                getStepResult().getResultForIdentifier(getStep().getIdentifier());

        results = new ArrayList<>();

        if (questionResult != null)
        {
            results.addAll(Arrays.asList(questionResult.getAnswer()));
        }

        for (int i = 0; i < textChoices.length; i++)
        {
            int position = i;
            TextChoice<T> textChoice = textChoices[position];
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(R.layout.item_checkbox,
                    radioGroup,
                    false);
            checkBox.setText(textChoice.getText());
            checkBox.setId(position);
            radioGroup.addView(checkBox);

            if (results.contains(textChoice.getValue()))
            {
                checkBox.setChecked(true);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                QuestionResult<T[]> questionResult1 = new QuestionResult<T[]>(
                        getStep().getIdentifier());
                if (isChecked)
                {
                    results.add(textChoice.getValue());
                }
                else
                {
                    results.remove(textChoice.getValue());
                }


                questionResult1.setAnswer((T[]) results.toArray());
                setStepResult(questionResult1);
            });
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

        });

        return radioGroup;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
