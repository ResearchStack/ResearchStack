package co.touchlab.researchstack.core.ui.views;
import android.content.Context;
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
    private TextView submit;
    private TextView exit;

    public SubmitBar(Context context)
    {
        super(context);
        init();
    }

    public SubmitBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SubmitBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.bar_submit, this, true);

        submit = (TextView) findViewById(R.id.bar_submit_postitive);
        exit = (TextView) findViewById(R.id.bar_submit_negative);
    }

    public void setSubmitAction(Action1 submit)
    {
        setSubmitAction(null, submit);
    }

    public void setSubmitAction(int title, Action1 submit)
    {
        setSubmitAction(getResources().getString(title), submit);
    }

    public void setSubmitAction(String title, Action1 submit)
    {
        if (this.submit.getVisibility() != View.VISIBLE)
        {
            this.submit.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(title))
        {
            this.submit.setText(title);
        }

        RxView.clicks(this.submit).subscribe(submit);
    }

    public void hideSubmitAction()
    {
        this.submit.setVisibility(View.GONE);
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
        if (this.exit.getVisibility() != View.VISIBLE)
        {
            this.exit.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(title))
        {
            this.exit.setText(title);
        }

        RxView.clicks(this.exit).subscribe(exit);
    }

    public void hideExitAction()
    {
        this.exit.setVisibility(View.GONE);
    }

}
