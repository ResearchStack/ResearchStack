package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TextQuestionResult;
import co.touchlab.touchkit.rk.common.step.FormStep;
import co.touchlab.touchkit.rk.ui.views.TextWatcherAdapter;

public class GenericFormScene extends Scene
{

    private StepResult<TextQuestionResult> stepResult;

    public GenericFormScene(Context context)
    {
        super(context);
    }

    public GenericFormScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GenericFormScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public GenericFormScene(Context context, FormStep step)
    {
        super(context);

        setTitle(step.getTitle());
        setSummary(step.getText());
        setSkip(step.isOptional());

        stepResult = new StepResult<>(step.getIdentifier());

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
            value.addTextChangedListener(new TextWatcherAdapter()
            {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    result.setTextAnswer(s.toString());
                }
            });

            stepViewContainer.addView(formItem, startIndex + i);
            stepResult.setResultForIdentifier(result.getIdentifier(), result);
        }
    }

    @Override
    public StepResult getResult()
    {
        return stepResult;
    }

    public static class FormItem
    {
        private final String identifier;
        private final String text;
        private final AnswerFormat format;
        private final String placeholder;

        public FormItem(String identifier, String text, AnswerFormat format, String placeholder)
        {

            this.identifier = identifier;
            this.text = text;
            this.format = format;
            this.placeholder = placeholder;
        }
    }

}
