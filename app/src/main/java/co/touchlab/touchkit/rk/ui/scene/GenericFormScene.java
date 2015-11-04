package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.result.StepResult;

public class GenericFormScene extends Scene
{

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

    public void setFormItems(List<FormItem> items)
    {
        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);
        int startIndex = getPositionToInsertBody();
        for(int i = 0; i < items.size(); i++)
        {
            FormItem item = items.get(i);

            View formItem =  LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_step_form, this, false);

            TextView label = (TextView) formItem.findViewById(R.id.text);
            label.setText(item.text);

            EditText value = (EditText) formItem.findViewById(R.id.value);
            value.setHint(item.placeholder);

            stepViewContainer.addView(formItem, startIndex + i);
            Log.i(GenericFormScene.class.getSimpleName(), "added form item");
        }
    }

    @Override
    protected StepResult getResult()
    {
        //TODO Result
        return null;
    }

    public static class FormItem
    {
        private final String givenNameIdentifier;
        private final String text;
        private final AnswerFormat format;
        private final String placeholder;

        public FormItem(String givenNameIdentifier, String text, AnswerFormat format, String placeholder)
        {

            this.givenNameIdentifier = givenNameIdentifier;
            this.text = text;
            this.format = format;
            this.placeholder = placeholder;
        }
    }

}
