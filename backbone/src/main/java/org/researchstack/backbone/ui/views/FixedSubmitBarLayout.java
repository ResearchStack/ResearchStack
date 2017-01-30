package org.researchstack.backbone.ui.views;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.layout.StepLayout;

public abstract class FixedSubmitBarLayout extends AlertFrameLayout implements StepLayout
{
    protected LayoutInflater layoutInflater;
    protected SubmitBar submitBar;
    protected ViewGroup contentContainer;

    public FixedSubmitBarLayout(Context context)
    {
        super(context);
        init();
    }

    public FixedSubmitBarLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FixedSubmitBarLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public FixedSubmitBarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public abstract int getContentResourceId();

    private void init()
    {
        // Init root
        layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.rsb_layout_fixed_submit_bar, this, true);

        // Add contentContainer to the layout
        contentContainer = (ViewGroup) findViewById(R.id.rsb_content_container);
        View content = layoutInflater.inflate(getContentResourceId(), contentContainer, false);
        contentContainer.addView(content, 0);

        // Init scrollview and submit bar guide positioning
        final View submitBarGuide = findViewById(R.id.rsb_submit_bar_guide);
        submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.rsb_content_container_scrollview);
        scrollView.setScrollListener(scrollY -> onScrollChanged(scrollView, submitBarGuide, submitBar));
        scrollView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Set submitBarGuide the same height as submitBar
                        if(submitBarGuide.getHeight() != submitBar.getHeight())
                        {
                            submitBarGuide.getLayoutParams().height = submitBar.getHeight();
                            submitBarGuide.requestLayout();
                        }

                        onScrollChanged(scrollView, submitBarGuide, submitBar);
                    }
                });
    }

    private void onScrollChanged(ScrollView scrollView, View submitBarGuide, View submitBar)
    {
        int scrollY = scrollView.getScrollY();
        int guidePosition = submitBarGuide.getTop() - scrollY;
        int guideHeight = submitBarGuide.getHeight();
        int yLimit = scrollView.getHeight() - guideHeight;

        if(guidePosition <= yLimit)
        {
            ViewCompat.setTranslationY(submitBar, 0);
        }
        else
        {
            int translationY = guidePosition - yLimit;
            ViewCompat.setTranslationY(submitBar, translationY);
        }
    }

    public SubmitBar getSubmitBar() {
        return submitBar;
    }
}