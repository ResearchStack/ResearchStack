package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.researchstack.backbone.R;

public class SubmitBar extends LinearLayout {
    private TextView positiveView;
    private TextView negativeView;

    public SubmitBar(Context context) {
        this(context, null);
    }

    public SubmitBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.submitbarStyle);
    }

    public SubmitBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_submitbar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SubmitBar,
                defStyleAttr,
                R.style.Widget_Backbone_SubmitBar);

        setBackground(a.getDrawable(R.styleable.SubmitBar_android_background));

        positiveView = (TextView) findViewById(R.id.bar_submit_postitive);
        positiveView.setText(a.getString(R.styleable.SubmitBar_positiveActionTitle));

        negativeView = (TextView) findViewById(R.id.bar_submit_negative);
        negativeView.setText(a.getString(R.styleable.SubmitBar_negativeActionTitle));

        a.recycle();
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Positive Action Helper Methods
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public void setPositiveTitle(int title) {
        setPositiveTitle(getResources().getString(title));
    }

    public void setPositiveTitle(String title) {
        positiveView.setText(title);
    }

    public void setPositiveAction(final OnClickListener action) {
        positiveView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                action.onClick(view);
            }
        });
    }

    public void clearActions()
    {
        positiveView.setOnClickListener(null);
        negativeView.setOnClickListener(null);
    }

    public View getPositiveActionView() {
        return positiveView;
    }

    public void setPositiveTitleColor(int color) {
        positiveView.setTextColor(color);
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Negative Action Helper Methods
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


    public void setNegativeTitle(int title) {
        setNegativeTitle(getResources().getString(title));
    }

    public void setNegativeTitle(String title) {
        negativeView.setText(title);
    }

    public void setNegativeAction(final OnClickListener action) {
        negativeView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                action.onClick(view);
            }
        });
    }

    public View getNegativeActionView() {
        return negativeView;
    }

    public void setNegativeTitleColor(int color) {
        negativeView.setTextColor(color);
    }
}
