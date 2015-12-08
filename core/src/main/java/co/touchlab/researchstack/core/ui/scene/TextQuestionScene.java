package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;

public class TextQuestionScene extends SceneImpl<String>
{

    public TextQuestionScene(Context context, Step step, StepResult result)
    {
        super(context, step, result);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        StepResult<String> result = getStepResult();

        EditText editText = (EditText) inflater.inflate(R.layout.item_edit_text, null);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                result.setResultForIdentifier(StepResult.DEFAULT_KEY, s.toString());
                setStepResult(result);
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

        String stringResult = result.getResultForIdentifier(StepResult.DEFAULT_KEY);
        if (!TextUtils.isEmpty(stringResult))
        {
            editText.setText(stringResult);
        }

        return editText;
    }

}
