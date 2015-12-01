package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.QuestionResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public class TextQuestionScene extends Scene
{

    public TextQuestionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        QuestionResult<String> stringResult = (QuestionResult<String>)
                getStepResult().getResultForIdentifier(getStep().getIdentifier());;

        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text, null);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                QuestionResult<String> questionResult = new QuestionResult<>(getStep().getIdentifier());
                questionResult.setAnswer(s.toString());
                setStepResult(questionResult);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        if (stringResult != null)
        {
            editText.setText(stringResult.getAnswer());
        }

        return editText;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<String>>(stepIdentifier);
    }
}
