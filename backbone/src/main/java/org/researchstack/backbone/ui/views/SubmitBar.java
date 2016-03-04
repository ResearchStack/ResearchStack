package org.researchstack.backbone.ui.views;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;

import rx.functions.Action1;

public class SubmitBar extends LinearLayout
{
    private TextView positiveView;
    private TextView negativeView;

    public SubmitBar(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public SubmitBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SubmitBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.bar_submit, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SubmitBar,
                defStyleAttr,
                R.style.SubmitBar);

        positiveView = (TextView) findViewById(R.id.bar_submit_postitive);
        setPositiveTitle(a.getString(R.styleable.SubmitBar_positiveActionTitle));

        negativeView = (TextView) findViewById(R.id.bar_submit_negative);
        setNegativeTitle(a.getString(R.styleable.SubmitBar_negativeActionTitle));

        a.recycle();
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Positive Action Helper Methods
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public SubmitBar setPositiveTitle(int title)
    {
        setPositiveTitle(getResources().getString(title));
        return this;
    }

    public SubmitBar setPositiveTitle(String title)
    {
        positiveView.setText(title);
        return this;
    }

    public SubmitBar setPositiveAction(Action1 submit)
    {
        RxView.clicks(this.positiveView).subscribe(submit);
        return this;
    }

    public View getPositiveActionView()
    {
        return positiveView;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Negative Action Helper Methods
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


    public SubmitBar setNegativeTitle(int title)
    {
        setNegativeTitle(getResources().getString(title));
        return this;
    }

    public SubmitBar setNegativeTitle(String title)
    {
        negativeView.setText(title);
        return this;
    }

    public SubmitBar setNegativeAction(Action1 submit)
    {
        RxView.clicks(this.negativeView).subscribe(submit);
        return this;
    }

    public View getNegativeActionView()
    {
        return negativeView;
    }

}
