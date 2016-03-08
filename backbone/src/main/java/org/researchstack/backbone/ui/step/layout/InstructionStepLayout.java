package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
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

        // Set Title
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(step.getTitle());

        // Set Summary
        TextView summaryView = (TextView) findViewById(R.id.summary);
        summaryView.setText(step.getText());

        // Set Next
        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.getNegativeActionView().setVisibility(View.GONE);
        submitBar.setPositiveTitle(R.string.rsb_next);
        submitBar.setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                        step,
                        null));
    }
}
