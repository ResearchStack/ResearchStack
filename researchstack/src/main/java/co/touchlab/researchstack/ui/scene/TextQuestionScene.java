package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.Step;
import co.touchlab.researchstack.ui.views.TextWatcherAdapter;

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
        editText.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                QuestionResult<String> questionResult = new QuestionResult<>(getStep().getIdentifier());
                questionResult.setAnswer(s.toString());
                setStepResult(questionResult);
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
