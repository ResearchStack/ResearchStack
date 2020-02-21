package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;


import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.LocaleUtils;

public class SubmitBar extends LinearLayout {
    private TextView positiveView;
    private TextView negativeView;

    //edit step bottom bar
    private TextView editSaveView;
    private TextView editCancelView;
    private Space editSpaceView;

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
        positiveView.setText(LocaleUtils.getLocalizedString(getContext(),R.string.rsb_next));

        negativeView = (TextView) findViewById(R.id.bar_submit_negative);
        negativeView.setText(LocaleUtils.getLocalizedString(getContext(),R.string.rsb_step_skip));

        editSaveView = (TextView) findViewById(R.id.bar_submit_edit_save);
        editCancelView = (TextView) findViewById(R.id.bar_submit_edit_cancel);

        editSpaceView = (Space) findViewById(R.id.edit_space_view);

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
        positiveView.setOnClickListener(view -> action.onClick(view));
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
        negativeView.setOnClickListener(view -> action.onClick(view));
    }

    public View getNegativeActionView() {
        return negativeView;
    }

    public void setNegativeTitleColor(int color) {
        negativeView.setTextColor(color);
    }

    public void setEditCancelAction(final OnClickListener action) {
        editCancelView.setOnClickListener(view -> action.onClick(view));
    }

    public void setEditSaveAction(final OnClickListener action) {
        editSaveView.setOnClickListener(view -> action.onClick(view));
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

    public View getEditSpaceView() {
        return editSpaceView;
    }

    public void updateView(boolean isEditView) {
        if (isEditView) {
            getPositiveActionView().setVisibility(GONE);
            getEditCancelViewActionView().setVisibility(VISIBLE);
            getEditSaveViewActionView().setVisibility(VISIBLE);
            getEditSpaceView().setVisibility(VISIBLE);
        } else {
            getPositiveActionView().setVisibility(VISIBLE);
            getEditCancelViewActionView().setVisibility(GONE);
            getEditSaveViewActionView().setVisibility(GONE);
            getEditSpaceView().setVisibility(GONE);
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
