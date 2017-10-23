package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.ThemeUtils;

import rx.functions.Action1;

public class SubmitBar extends LinearLayout {

    private static final float DEFAULT_DISABLED_OPACITY = 0.4f;

    private TextView positiveView;
    private TextView negativeView;
    private float disabledOpacity = DEFAULT_DISABLED_OPACITY;

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
        positiveView.setTextColor(a.getColor(R.styleable.SubmitBar_positiveActionColor,
                ThemeUtils.getAccentColor(context)));

        negativeView = (TextView) findViewById(R.id.bar_submit_negative);
        negativeView.setText(a.getString(R.styleable.SubmitBar_negativeActionTitle));
        negativeView.setTextColor(a.getColor(R.styleable.SubmitBar_negativeActionColor,
                ContextCompat.getColor(context, R.color.rsb_submit_bar_negative)));

        a.recycle();
    }

    public void setDisabledOpacity(float disabledOpacity) {
        this.disabledOpacity = disabledOpacity;
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

    public void setPositiveAction(Action1 submit) {
        RxView.clicks(this.positiveView).subscribe(submit);
    }

    public View getPositiveActionView() {
        return positiveView;
    }

    public void setPositiveActionViewEnabled(boolean enabled) {
        positiveView.setEnabled(enabled);
        positiveView.setAlpha(enabled ? 1.0f : disabledOpacity);
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

    public void setNegativeAction(Action1 submit) {
        RxView.clicks(this.negativeView).subscribe(submit);
    }

    public View getNegativeActionView() {
        return negativeView;
    }

    public void setNegativeActionViewEnabled(boolean enabled) {
        negativeView.setEnabled(enabled);
        negativeView.setAlpha(enabled ? 1.0f : disabledOpacity);
    }
}
