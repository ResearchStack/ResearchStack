package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.SubmitBar;

public class InstructionStepLayout extends RelativeLayout implements StepLayout
{
    private StepCallbacks callbacks;
    private Step          step;

    public InstructionStepLayout(Context context)
    {
        super(context);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
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
        LayoutInflater.from(getContext()).inflate(R.layout.step_layout_instruction, this, true);

        if(step != null)
        {

            // Set Title
            if (! TextUtils.isEmpty(step.getTitle()))
            {
                TextView title = (TextView) findViewById(R.id.title);
                title.setVisibility(View.VISIBLE);
                title.setText(step.getTitle());
            }

            // Set Summary
            if(! TextUtils.isEmpty(step.getText()))
            {
                TextView summary = (TextView) findViewById(R.id.text);
                summary.setVisibility(View.VISIBLE);
                summary.setText(Html.fromHtml(step.getText()));
                summary.setMovementMethod(new TextViewLinkHandler()
                {
                    @Override
                    public void onLinkClick(String url)
                    {
                        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                                step.getTitle(),
                                url);
                        getContext().startActivity(intent);
                    }
                });
            }

            // Set Next / Skip
            SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
            submitBar.setPositiveTitle(R.string.rsb_next);
            submitBar.setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                    step,
                    null));

            if(step.isOptional())
            {
                submitBar.setNegativeTitle(R.string.rsb_step_skip);
                submitBar.setNegativeAction(v -> {
                    if(callbacks != null)
                    {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                    }
                });
            }
            else
            {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }

        // Init filler code
        View filler = findViewById(R.id.filler);
        View container = findViewById(R.id.content_container);
        addOnLayoutChangeListener(new OnLayoutChangeListener()
        {
            boolean isChangeFromFiller = false;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                LogExt.i("InstructionStepLayout", "Layout Change, Adjusting Filler");

                int stepLayoutHeight = InstructionStepLayout.this.getHeight();
                int contentHeightSansFiller = container.getHeight() - filler.getHeight();

                // Make sure we have layout and height to measure
                if(stepLayoutHeight == 0 || contentHeightSansFiller == 0)
                {
                    return;
                }

                // if the last call to this resulted in filler changing size, ignore
                if(isChangeFromFiller)
                {
                    isChangeFromFiller = false;
                    return;
                }

                // If our content does not take up the entire height of the screen, increase height
                // of the filler space
                if(contentHeightSansFiller < stepLayoutHeight)
                {
                    filler.post(() -> {
                        ViewGroup.LayoutParams params = filler.getLayoutParams();
                        params.height = stepLayoutHeight - contentHeightSansFiller;
                        filler.setLayoutParams(params);

                        isChangeFromFiller = true;
                    });
                }
                else if (contentHeightSansFiller > stepLayoutHeight && filler.getHeight() != 0)
                {
                    filler.post(() -> {
                        ViewGroup.LayoutParams params = filler.getLayoutParams();
                        params.height = 0;
                        filler.setLayoutParams(params);

                        isChangeFromFiller = true;
                    });
                }
            }
        });
    }
}
