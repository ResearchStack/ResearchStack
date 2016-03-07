package org.researchstack.backbone.ui.step.layout;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ResUtils;

public class ConsentVisualStepLayout extends RelativeLayout implements StepLayout
{

    private StepCallbacks callbacks;
    private ConsentVisualStep step;

    public ConsentVisualStepLayout(Context context)
    {
        super(context);
    }

    public ConsentVisualStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentVisualStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentVisualStep) step;
        initializeStep();
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    private void initializeStep()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.step_layout_consent_visual, this, true);

        ConsentSection data = step.getSection();

        // Set Image
        TypedValue typedValue = new TypedValue();
        TypedArray a = getContext().obtainStyledAttributes(typedValue.data,
                new int[] {R.attr.colorAccent});
        int accentColor = a.getColor(0, 0);
        a.recycle();

        ImageView imageView = (ImageView) findViewById(R.id.image);

        String imageName = ! TextUtils.isEmpty(data.getCustomImageName())
                ? data.getCustomImageName()
                : data.getType().getImageName();

        if(! TextUtils.isEmpty(imageName))
        {
            int imageResId = ResUtils.getDrawableResourceId(getContext(), imageName);
            Drawable drawable = getResources().getDrawable(imageResId);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, accentColor);
            imageView.setImageDrawable(drawable);
            imageView.setVisibility(View.VISIBLE);
        }
        else
        {
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

        if (!TextUtils.isEmpty(data.getHtmlContent()))
        {
            if (!TextUtils.isEmpty(data.getCustomLearnMoreButtonTitle()))
            {
                moreInfoView.setText(data.getCustomLearnMoreButtonTitle());
            }
            else
            {
                moreInfoView.setText(data.getType().getMoreInfoResId());
            }

            RxView.clicks(moreInfoView).subscribe(v -> {
                String path = data.getHtmlContent();
                String webTitle = getResources().getString(R.string.rsb_consent_section_more_info);
                Intent webDoc = ViewWebDocumentActivity.newIntent(getContext(), webTitle, path);
                getContext().startActivity(webDoc);
            });
        }
        else
        {
            moreInfoView.setVisibility(View.GONE);
        }

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveTitle(step.getNextButtonString())
                .setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                        step,
                        null));
        submitBar.getNegativeActionView().setVisibility(View.GONE);
    }
}
