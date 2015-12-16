package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;

public class MultiChoiceQuestionScene<T> extends SceneImpl<T[]>
{
    private List<T> results;

    public MultiChoiceQuestionScene(Context context)
    {
        super(context);
    }

    public MultiChoiceQuestionScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MultiChoiceQuestionScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initializeScene()
    {
        StepResult<T[]> result = getStepResult();

        results = new ArrayList<>();

        if (result != null)
        {
            T[] resultArray = result.getResultForIdentifier(StepResult.DEFAULT_KEY);
            if (resultArray != null && resultArray.length > 0)
            {
                results.addAll(Arrays.asList(resultArray));
            }
        }

        super.initializeScene();
    }

    /**
     * TODO this whole thing needs a lot of refactoring -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * It could probably just be combined with single choice questions
     */
    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        RadioGroup radioGroup = new RadioGroup(getContext());
        StepResult<T[]> result = getStepResult();

        ChoiceAnswerFormat answerFormat = (ChoiceAnswerFormat) ((QuestionStep) getStep()).getAnswerFormat();
        final Choice<T>[] choices = answerFormat.getChoices();

        for (int i = 0; i < choices.length; i++)
        {
            Choice<T> item = choices[i];

            // Create & add the View to our body-view
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(
                    R.layout.item_checkbox, radioGroup, false);
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

                // TODO Move the following out of here -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                // it should be set in a "finalizeStepResult" method that is called when
                // onNextClicked is called.
                result.setResultForIdentifier(StepResult.DEFAULT_KEY, (T[])results.toArray());
                setStepResult(result);
            });
        }

        return radioGroup;
    }

    @Override
    public boolean isAnswerValid()
    {
        return !getStepResult().isEmpty();
    }
}