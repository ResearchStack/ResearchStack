package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.TextAnswerFormat;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TextQuestionResult;
import co.touchlab.touchkit.rk.common.step.FormStep;
import co.touchlab.touchkit.rk.ui.views.TextWatcherAdapter;

public class FormScene extends Scene
{

    private StepResult<TextQuestionResult> stepResult;
    private FormStep step;

    public FormScene(Context context)
    {
        super(context);
    }

    public FormScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FormScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public FormScene(Context context, FormStep step)
    {
        super(context);

        this.step = step;
        this.stepResult = new StepResult<>(step.getIdentifier());

        setTitle(step.getTitle());
        setSummary(step.getText());
        setSkip(step.isOptional());

        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);

        int startIndex = getPositionToInsertBody();
        List<FormItem> items = step.getFormItems();
        for(int i = 0, size = items.size(); i < size; i++)
        {
            FormItem item = items.get(i);
            TextQuestionResult result = new TextQuestionResult(item.identifier);

            View formItem =  LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_step_form, this, false);

            TextView label = (TextView) formItem.findViewById(R.id.text);
            label.setText(item.text);

            EditText value = (EditText) formItem.findViewById(R.id.value);
            value.setHint(item.placeholder);
            value.setSingleLine(! item.format.isMultipleLines());
            value.setFilters(item.format.getInputFilters());
            value.addTextChangedListener(new TextWatcherAdapter()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    String input = s == null ? "" : s.toString();
                    result.setTextAnswer(input);
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
            stepResult.setResultForIdentifier(result.getIdentifier(), result);
        }
    }

    @Override
    public boolean isAnswerValid()
    {
        boolean isValid = true;

        List<FormItem> items = step.getFormItems();
        for(FormItem item : items)
        {
            TextQuestionResult result = stepResult.getResultForIdentifier(item.identifier);
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

    @Override
    public StepResult<TextQuestionResult> getResult()
    {
        return stepResult;
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
