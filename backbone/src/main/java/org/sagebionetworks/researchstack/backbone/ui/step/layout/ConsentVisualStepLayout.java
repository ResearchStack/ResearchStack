package org.sagebionetworks.researchstack.backbone.ui.step.layout;

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

import com.jakewharton.rxbinding.view.RxView;

import org.sagebionetworks.researchstack.backbone.R;
import org.sagebionetworks.researchstack.backbone.model.ConsentSection;
import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.step.ConsentVisualStep;
import org.sagebionetworks.researchstack.backbone.step.Step;
import org.sagebionetworks.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.sagebionetworks.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.sagebionetworks.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.sagebionetworks.researchstack.backbone.ui.views.SubmitBar;
import org.sagebionetworks.researchstack.backbone.utils.ResUtils;
import org.sagebionetworks.researchstack.backbone.utils.TextUtils;

public class ConsentVisualStepLayout extends FixedSubmitBarLayout implements StepLayout {

    protected StepCallbacks callbacks;
    protected ConsentVisualStep step;

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
        return R.layout.rsb_step_layout_consent_visual;
    }

    protected void initializeStep() {
        ConsentSection data = step.getSection();

        // Set Image
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data,
                new int[]{R.attr.colorAccent});
        int accentColor = a.getColor(0, 0);
        a.recycle();

        ImageView imageView = findViewById(R.id.image);
        int imageResId = getImageRes();
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
        TextView titleView = findViewById(R.id.title);
        titleView.setText(getTitle());

        // Set Summary
        TextView summaryView = findViewById(R.id.summary);
        summaryView.setText(getSummary());

        // Set more info
        TextView moreInfoView = findViewById(R.id.more_info);
        if (!TextUtils.isEmpty(data.getHtmlContent())) {
            if (!TextUtils.isEmpty(data.getCustomLearnMoreButtonTitle())) {
                moreInfoView.setText(data.getCustomLearnMoreButtonTitle());
            } else {
                moreInfoView.setText(data.getType().getMoreInfoResId());
            }

            RxView.clicks(moreInfoView).subscribe(v -> {
                moreInfoClicked();
            });
        } else {
            moreInfoView.setVisibility(View.GONE);
        }

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        String nextButtonTitle = getContext().getString(R.string.rsb_next);
        // Support for deprecated method
        if (step.getNextButtonString() != null) {
            nextButtonTitle = step.getNextButtonString();
        }
        submitBar.setPositiveTitle(nextButtonTitle);
        submitBar.setPositiveAction(v -> nextButtonClicked());
        submitBar.getNegativeActionView().setVisibility(View.GONE);
    }

    protected void nextButtonClicked() {
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
    }

    protected void moreInfoClicked() {
        ConsentSection data = step.getSection();
        String webTitle = getResources().getString(R.string.rsb_consent_section_more_info);
        Intent webDoc = ViewWebDocumentActivity.newIntentForContent(getContext(), webTitle,
                TextUtils.isEmpty(data.getContent()) ? data.getHtmlContent() : data.getContent());
        getContext().startActivity(webDoc);
    }

    protected int getImageRes() {
        ConsentSection data = step.getSection();
        String imageName = !TextUtils.isEmpty(data.getCustomImageName())
                ? data.getCustomImageName()
                : data.getType().getImageName();

        return ResUtils.getDrawableResourceId(getContext(), imageName);
    }

    protected String getTitle() {
        ConsentSection data = step.getSection();
        return TextUtils.isEmpty(data.getTitle()) ? getResources().getString(data.getType()
                .getTitleResId()) : data.getTitle();
    }

    protected String getSummary() {
        ConsentSection data = step.getSection();
        return data.getSummary();
    }

    /**
     * @return The index of the section within all the visual consent sections
     */
    public int getSectionIndex() {
        return step.getSectionIndex();
    }

    /**
     * @return The total count of the visual consent sections
     */
    public int getSectionCount() {
        return step.getSectionCount();
    }
}
