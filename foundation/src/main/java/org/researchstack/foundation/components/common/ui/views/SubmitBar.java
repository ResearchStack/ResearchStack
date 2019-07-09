package org.researchstack.foundation.components.common.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.foundation.R;
import org.researchstack.foundation.components.utils.ThemeUtils;

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

        LayoutInflater.from(getContext()).inflate(R.layout.rsf_view_submitbar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SubmitBar,
                defStyleAttr,
                R.style.Widget_Foundation_SubmitBar);

        setBackground(a.getDrawable(R.styleable.SubmitBar_android_background));

        positiveView = (TextView) findViewById(R.id.bar_submit_postitive);
        positiveView.setText(a.getString(R.styleable.SubmitBar_positiveActionTitle));
        positiveView.setTextColor(a.getColor(R.styleable.SubmitBar_positiveActionColor,
                ThemeUtils.getAccentColor(context)));

        negativeView = (TextView) findViewById(R.id.bar_submit_negative);
        negativeView.setText(a.getString(R.styleable.SubmitBar_negativeActionTitle));
        negativeView.setTextColor(a.getColor(R.styleable.SubmitBar_negativeActionColor,
                ContextCompat.getColor(context, R.color.rsf_submit_bar_negative)));

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

    public void setPositiveAction(OnClickListener listener) {
        this.positiveView.setOnClickListener(listener);
    }

    public View getPositiveActionView() {
        return positiveView;
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

    public void setNegativeAction(OnClickListener listener) {
        this.negativeView.setOnClickListener(listener);
    }

    public View getNegativeActionView() {
        return negativeView;
    }

}
