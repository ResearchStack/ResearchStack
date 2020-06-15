package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.LocalizationUtils;

public class SubmitBar extends ConstraintLayout {
    private TextView positiveView;
    private TextView negativeView;

    //edit step bottom bar
    private TextView editSaveView;
    private TextView editCancelView;

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

        positiveView = findViewById(R.id.bar_submit_positive);
        positiveView.setText(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_next));

        negativeView = findViewById(R.id.bar_submit_negative);
        negativeView.setText(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_step_skip));

        editSaveView = findViewById(R.id.bar_submit_edit_save);
        editCancelView = findViewById(R.id.bar_submit_edit_cancel);

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
        positiveView.setOnClickListener(action);
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

    public void setPositiveActionEnabled(int color) {
        positiveView.setClickable(true);
        positiveView.setTextColor(color);

    }

    public void setPositiveActionDisabled() {
        positiveView.setClickable(false);
        positiveView.setTextColor(getContext().getColor(R.color.rsb_submit_disabled));

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
        negativeView.setOnClickListener(action);
    }

    public View getNegativeActionView() {
        return negativeView;
    }

    public void setNegativeTitleColor(int color) {
        negativeView.setTextColor(color);
    }

    public void setEditCancelAction(final OnClickListener action) {
        editCancelView.setOnClickListener(action);
    }

    public void setEditSaveAction(final OnClickListener action) {
        editSaveView.setOnClickListener(action);
    }

    public void setEditSaveColor(int color) {
        editSaveView.setTextColor(color);
    }

    public void setEditCancelColor(int color) {
        editCancelView.setTextColor(color);
    }

    public View getEditCancelViewActionView() {
        return editCancelView;
    }

    public View getEditSaveViewActionView() {
        return editSaveView;
    }

    public void updateView(boolean isEditView) {
        if (isEditView) {
            getPositiveActionView().setVisibility(GONE);
            getEditCancelViewActionView().setVisibility(VISIBLE);
            getEditSaveViewActionView().setVisibility(VISIBLE);
        } else {
            getPositiveActionView().setVisibility(VISIBLE);
            getEditCancelViewActionView().setVisibility(INVISIBLE);
            getEditSaveViewActionView().setVisibility(GONE);
        }
    }

    public void setEditSaveActionEnabled(int color) {
        editSaveView.setClickable(true);
        editSaveView.setTextColor(color);
    }

    public void setEditSaveActionDisabled() {
        editSaveView.setClickable(false);
        editSaveView.setTextColor(getContext().getColor(R.color.rsb_submit_disabled));
    }
}
