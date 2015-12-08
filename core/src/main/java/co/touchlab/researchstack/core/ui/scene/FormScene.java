package co.touchlab.researchstack.core.ui.scene;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TextQuestionResult;
import co.touchlab.researchstack.core.step.FormStep;

public class FormScene extends SceneImpl
{

    public FormScene(Context context, FormStep step, StepResult result)
    {
        super(context, step, result);

        setTitle(getStep().getTitle());
        setSummary(getStep().getText());
        setSkip(getStep().isOptional());
    }

    @Override
    public void onSceneCreated(View scene)
    {
        super.onSceneCreated(scene);

        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);

        int startIndex = getPositionToInsertBody();
        List<FormItem> items = ((FormStep) getStep()).getFormItems();
        for(int i = 0, size = items.size(); i < size; i++)
        {
            FormItem item = items.get(i);
            final TextQuestionResult result = new TextQuestionResult(item.identifier);

            View formItem =  LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_step_form, this, false);

            TextView label = (TextView) formItem.findViewById(R.id.text);
            label.setText(item.text);

            EditText value = (EditText) formItem.findViewById(R.id.value);
            value.setHint(item.placeholder);
            value.setSingleLine(! item.format.isMultipleLines());
            value.setFilters(item.format.getInputFilters());
            value.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    String input = s == null ? "" : s.toString();
                    result.setTextAnswer(input);
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

            if (i == size - 1)
            {
                value.setOnKeyListener((v, keyCode, event) -> {
                    if (event != null && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                    {
                        onNextClicked();
                        return true;
                    }

                    return false;
                });
            }

            stepViewContainer.addView(formItem, startIndex + i);
            ((StepResult<TextQuestionResult>) getStepResult())
                    .setResultForIdentifier(result.getIdentifier(), result);
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        boolean isValid = true;

        List<FormItem> items = ((FormStep)getStep()).getFormItems();
        for(FormItem item : items)
        {
            TextQuestionResult result =  ((StepResult<TextQuestionResult>) getStepResult())
                    .getResultForIdentifier(item.identifier);
            String answer = result.getTextAnswer();
            if (!item.format.isAnswerValidWithString(answer))
            {
                //TODO Move message into xml/strings.xml
                //TODO Throw dialog instead of toast
                Toast.makeText(getContext(), "Invalid answer, double check your answers!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return isValid && super.isAnswerValid();
    }

    public static class FormItem
    {
        private final String identifier;
        private final String text;
        private final TextAnswerFormat format;
        private final String placeholder;

        public FormItem(String identifier, String text, TextAnswerFormat format, String placeholder)
        {
            this.identifier = identifier;
            this.text = text;
            this.format = format;
            this.placeholder = placeholder;
        }
    }

}
