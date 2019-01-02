package org.researchstack.foundation.components.survey.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.researchstack.foundation.R;
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks;
import org.researchstack.foundation.components.common.ui.layout.FixedSubmitBarLayout;
import org.researchstack.foundation.components.common.ui.layout.StepLayout;
import org.researchstack.foundation.components.common.ui.views.SubmitBar;
import org.researchstack.foundation.components.singletons.TextViewLinkManager;
import org.researchstack.foundation.components.utils.TextUtils;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

public class InstructionStepLayout extends FixedSubmitBarLayout implements StepLayout {
    private StepCallbacks callbacks;
    private Step step;

    public InstructionStepLayout(Context context) {
        super(context);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstructionStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        this.step = step;
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
        return R.layout.rsf_step_layout_instruction;
    }

    private void initializeStep() {
        if (step != null) {

            // Set Title
            if (!TextUtils.isEmpty(step.getTitle())) {
                TextView title = (TextView) findViewById(R.id.rsf_intruction_title);
                title.setVisibility(View.VISIBLE);
                title.setText(step.getTitle());
            }

            // Set Summary
            if (!TextUtils.isEmpty(step.getText())) {
                TextView summary = (TextView) findViewById(R.id.rsf_intruction_text);
                summary.setVisibility(View.VISIBLE);
                summary.setText(Html.fromHtml(step.getText()));
                summary.setMovementMethod(
                        TextViewLinkManager.Companion.getSharedManager().getHandler(getContext(), step.getTitle())
                );
            }

            // Set Next / Skip
            SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsf_submit_bar);
            submitBar.setPositiveTitle(R.string.rsf_next);
            submitBar.setPositiveAction(v -> callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                    step,
                    null));

            if (step.isOptional()) {
                submitBar.setNegativeTitle(R.string.rsf_step_skip);
                submitBar.setNegativeAction(v -> {
                    if (callbacks != null) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                    }
                });
            } else {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }
    }
}
