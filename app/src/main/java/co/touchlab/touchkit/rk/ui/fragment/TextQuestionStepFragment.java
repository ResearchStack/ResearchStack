package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.ui.views.TextWatcherAdapter;

public class TextQuestionStepFragment extends StepFragment
{

    public TextQuestionStepFragment()
    {
        super();
    }

    public static Fragment newInstance(QuestionStep step)
    {
        TextQuestionStepFragment fragment = new TextQuestionStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {

        QuestionResult<String> stringResult = (QuestionResult<String>)
                stepResult.getResultForIdentifier(step.getIdentifier());;

        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text, null);
        editText.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                QuestionResult<String> questionResult = new QuestionResult<String>(
                        step.getIdentifier());
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
