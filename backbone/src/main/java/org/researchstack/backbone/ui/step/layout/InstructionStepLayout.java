package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.TextUtils;

public class InstructionStepLayout extends FixedSubmitBarLayout implements StepLayout
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

    @Override
    public void receiveIntentExtraOnResult(int requestCode, Intent intent) {

    }

    @Override
    public int getContentResourceId()
    {
        return R.layout.rsb_step_layout_instruction;
    }

    private void initializeStep()
    {
        if(step != null)
        {

            // Set Title
            if (! TextUtils.isEmpty(step.getTitle()))
            {
                TextView title = (TextView) findViewById(R.id.rsb_instruction_title);
                title.setVisibility(View.VISIBLE);
                title.setText(step.getTitle());
            }

            // Set Summary
            if(! TextUtils.isEmpty(step.getText()))
            {
                TextView summary = (TextView) findViewById(R.id.rsb_instruction_text);
                summary.setVisibility(View.VISIBLE);
                summary.setText(Html.fromHtml(step.getText()));
                summary.setMovementMethod(new TextViewLinkHandler()
                {
                    @Override
                    public void onLinkClick(String url)
                    {
                        String path = ResourcePathManager.getInstance().
                                generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML, url);
                        Intent intent = ViewWebDocumentActivity.newIntentForPath(getContext(),
                                step.getTitle(),
                                path);
                        getContext().startActivity(intent);
                    }
                });
            }

            // Set Next / Skip
            SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
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
    }
}
