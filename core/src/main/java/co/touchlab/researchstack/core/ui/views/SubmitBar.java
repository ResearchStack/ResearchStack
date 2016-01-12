package co.touchlab.researchstack.core.ui.views;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.R;
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

        String positiveText = a.getString(R.styleable.SubmitBar_positiveActionTitle);
        positiveView = (TextView) findViewById(R.id.bar_submit_postitive);
        positiveView.setText(positiveText);

        String negativeText = a.getString(R.styleable.SubmitBar_negativeActionTitle);
        negativeView = (TextView) findViewById(R.id.bar_submit_negative);
        negativeView.setText(negativeText);

        a.recycle();
    }

    public void setPositiveAction(Action1 submit)
    {
        setPositiveAction(null, submit);
    }

    public void setPositiveAction(int title, Action1 submit)
    {
        setPositiveAction(getResources().getString(title), submit);
    }

    public void setPositiveAction(String title, Action1 submit)
    {
        if (this.positiveView.getVisibility() != View.VISIBLE)
        {
            this.positiveView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(title))
        {
            this.positiveView.setText(title);
        }

        RxView.clicks(this.positiveView).subscribe(submit);
    }

    public void hideSubmitAction()
    {
        this.positiveView.setVisibility(View.GONE);
    }

    public void setExitAction(Action1 submit)
    {
        setExitAction(null, submit);
    }

    public void setExitAction(int title, Action1 exit)
    {
        setExitAction(getResources().getString(title), exit);
    }

    public void setExitAction(String title, Action1 exit)
    {
        if (this.negativeView.getVisibility() != View.VISIBLE)
        {
            this.negativeView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(title))
        {
            this.negativeView.setText(title);
        }

        RxView.clicks(this.negativeView).subscribe(exit);
    }

    public void hideExitAction()
    {
        this.negativeView.setVisibility(View.GONE);
    }

}
