package org.researchstack.feature.consent.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.feature.consent.R;
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks;
import org.researchstack.foundation.components.common.ui.layout.FixedSubmitBarLayout;
import org.researchstack.foundation.components.common.ui.layout.StepLayout;
import org.researchstack.foundation.components.common.ui.views.SubmitBar;
import org.researchstack.feature.consent.model.ConsentSection;
import org.researchstack.feature.consent.step.ConsentVisualStep;
import org.researchstack.foundation.components.utils.ResUtils;
import org.researchstack.foundation.components.utils.TextUtils;
import org.researchstack.foundation.components.web.ui.activities.ViewWebDocumentActivity;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

public class ConsentVisualStepLayout extends FixedSubmitBarLayout implements StepLayout {

    private StepCallbacks callbacks;
    private ConsentVisualStep step;

    public ConsentVisualStepLayout(Context context) {
        super(context);
    }

    public ConsentVisualStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConsentVisualStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        this.step = (ConsentVisualStep) step;
        initializeStep();
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsfc_step_layout_consent_visual;
    }

    private void initializeStep() {
        ConsentSection data = step.getSection();

        // Set Image
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data,
                new int[]{R.attr.colorAccent});
        int accentColor = a.getColor(0, 0);
        a.recycle();

        ImageView imageView = (ImageView) findViewById(R.id.image);

        String imageName = !TextUtils.isEmpty(data.getCustomImageName())
                ? data.getCustomImageName()
                : data.getType().getImageName();

        int imageResId = ResUtils.getDrawableResourceId(getContext(), imageName);

        if (imageResId != 0) {
            Drawable drawable = getResources().getDrawable(imageResId);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, accentColor);
            imageView.setImageDrawable(drawable);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        // Set Title
        TextView titleView = (TextView) findViewById(R.id.title);
        String title = TextUtils.isEmpty(data.getTitle()) ? getResources().getString(data.getType()
                .getTitleResId()) : data.getTitle();
        titleView.setText(title);

        // Set Summary
        TextView summaryView = (TextView) findViewById(R.id.summary);
        summaryView.setText(data.getSummary());

        // Set more info
        TextView moreInfoView = (TextView) findViewById(R.id.more_info);

        if (!TextUtils.isEmpty(data.getHtmlContent())) {
            if (!TextUtils.isEmpty(data.getCustomLearnMoreButtonTitle())) {
                moreInfoView.setText(data.getCustomLearnMoreButtonTitle());
            } else {
                moreInfoView.setText(data.getType().getMoreInfoResId());
            }

            moreInfoView.setOnClickListener(v -> {
                String webTitle = getResources().getString(R.string.rsfc_consent_section_more_info);
                Intent webDoc = ViewWebDocumentActivity.newIntentForContent(getContext(), webTitle,
                        TextUtils.isEmpty(data.getContent()) ? data.getHtmlContent() : data.getContent());
                getContext().startActivity(webDoc);
            });
        } else {
            moreInfoView.setVisibility(View.GONE);
        }

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsf_submit_bar);
        submitBar.setPositiveTitle(step.getNextButtonString());
        submitBar.setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                step,
                null));
        submitBar.getNegativeActionView().setVisibility(View.GONE);
    }
}
