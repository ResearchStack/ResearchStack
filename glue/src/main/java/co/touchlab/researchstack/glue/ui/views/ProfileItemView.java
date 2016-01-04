package co.touchlab.researchstack.glue.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import co.touchlab.researchstack.glue.R;

/**
 * Created by bradleymcdermott on 11/9/15.
 */
public class ProfileItemView extends LinearLayout
{
    private AppCompatTextView label;
    private AppCompatEditText value;

    public ProfileItemView(Context context)
    {
        super(context);
    }

    public ProfileItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProfileItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ProfileItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        label = (AppCompatTextView) findViewById(R.id.label);
        value = (AppCompatEditText) findViewById(R.id.value);
    }

    public void setLabel(int labelId)
    {
        this.label.setText(labelId);
    }

    public void setValue(String value)
    {
        this.value.setText(value);
    }
}
