package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;

public class TextQuestionStepFragment extends QuestionStepFragment
{

    public TextQuestionStepFragment()
    {
        super();
    }

    public static Fragment newInstance(QuestionStep step, StepResult result)
    {
        TextQuestionStepFragment fragment = new TextQuestionStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        args.putSerializable(KEY_STEP_RESULT,
                result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text, null);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                QuestionResult<String> questionResult = new QuestionResult<String>(step.getIdentifier());
                questionResult.setAnswer(s.toString());

                // TODO this is bad and we should feel bad
                Map<String, QuestionResult<String>> results = new HashMap<>();
                results.put(questionResult.getIdentifier(),
                        questionResult);

                stepResult.setResults(results);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        return editText;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<String>>(stepIdentifier);
    }
}
