package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.LocaleUtils;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;

import java.net.URL;

public class ConsentVisualStepLayout extends FixedSubmitBarLayout implements StepLayout {

    private StepCallbacks callbacks;
    private ConsentVisualStep step;
    private int colorPrimary;
    private int colorSecondary;
    private int principalTextColor;
    private int secondaryTextColor;

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

    public void initialize(Step step, StepResult result, int colorPrimary, int colorSecondary, int principalTextColor, int secondaryTextColor) {
        this.step = (ConsentVisualStep) step;
        this.colorPrimary = colorPrimary;
        this.colorSecondary = colorSecondary;
        this.principalTextColor = principalTextColor;
        this.secondaryTextColor = secondaryTextColor;
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

    private void initializeStep() {
        final ConsentSection data = step.getSection();

        ImageView imageView = (ImageView) findViewById(R.id.image);

        String imageName = !TextUtils.isEmpty(data.getCustomImageName())
                ? data.getCustomImageName()
                : data.getType().getImageName();

        int imageResId = ResUtils.getDrawableResourceId(getContext(), imageName);

        Drawable drawable = null;

        if (imageResId != 0) {
            drawable = getResources().getDrawable(imageResId);
        }
        else if(imageName != null && imageResId == 0)
        {
            drawable = Drawable.createFromPath(imageName);
        }

        if (drawable != null)
        {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, colorSecondary);
            imageView.setImageDrawable(drawable);
            imageView.setVisibility(View.VISIBLE);
        }
        else
        {
            imageView.setVisibility(View.GONE);
        }

        // Set Title
        TextView titleView = (TextView) findViewById(R.id.title);
        if (TextUtils.isEmpty(data.getTitle())) {
            titleView.setText(LocaleUtils.getLocalizedString(getContext(), data.getType().getTitleResId()));
        } else {
            titleView.setText(data.getTitle());
        }
        titleView.setTextColor(principalTextColor);

        // Set Summary
        TextView summaryView = (TextView) findViewById(R.id.summary);
        summaryView.setText(data.getSummary());

        // Set more info
        TextView moreInfoView = (TextView) findViewById(R.id.more_info);

        if (data.getContentUrl() != null || !TextUtils.isEmpty(data.getContent()) || !TextUtils.isEmpty(data.getHtmlContent()))
        {
            if (!TextUtils.isEmpty(data.getCustomLearnMoreButtonTitle()))
            {
                moreInfoView.setText(data.getCustomLearnMoreButtonTitle());
            }
            else
            {
                moreInfoView.setText(LocaleUtils.getLocalizedString(moreInfoView.getContext(), data.getType().getMoreInfoResId()));
            }

            moreInfoView.setOnClickListener(view -> {
                String webTitle = LocaleUtils.getLocalizedString(moreInfoView.getContext(), R.string.rsb_consent_section_more_info);
                URL contentUrl = data.getContentUrl();
                Intent webDoc;
                if(contentUrl != null)
                {
                     webDoc = ViewWebDocumentActivity.newIntentForContent(getContext(), webTitle, contentUrl, true);
                }
                else
                {
                    webDoc = ViewWebDocumentActivity.newIntentForContent(getContext(), webTitle,
                            !TextUtils.isEmpty(data.getHtmlContent()) ? data.getHtmlContent() : data.getContent());
                }

                ViewWebDocumentActivity.addThemeColors(webDoc, step.getPrimaryColor(), step.getColorPrimaryDark());
                getContext().startActivity(webDoc);
            });

        } else {
            moreInfoView.setVisibility(View.GONE);
        }

        final SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        submitBar.setPositiveTitle(LocaleUtils.getLocalizedString(getContext(), step.getNextButtonString()));
        submitBar.setNegativeTitleColor(colorPrimary);
        submitBar.setPositiveTitleColor(colorSecondary);
        submitBar.setPositiveAction(new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                                            submitBar.clearActions();
                                        }
                                    });
        submitBar.getNegativeActionView().setVisibility(View.GONE);
    }
}
