package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import org.jetbrains.annotations.NotNull;
import org.researchstack.backbone.R;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.LocalizationUtils;
import org.researchstack.backbone.utils.TextUtils;

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
    public void setCancelEditMode(boolean isCancelEdit) {
        //no-op only when user on edit mode inside regular steps
    }

    @Override
    public void setRemoveFromBackStack(boolean removeFromBackStack) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public void isEditView(boolean isEditView) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public void setStepResultTo(@NotNull StepResult originalResult) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public StepResult getStepResult() {
        // This step doesn't have a result, so we're returning null instead
        return null;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_step_layout_instruction;
    }

    private void initializeStep() {
        if (step != null) {
            // Set Title
            if (!TextUtils.isEmpty(step.getTitle())) {
                TextView title = findViewById(R.id.rsb_intruction_title);
                title.setVisibility(View.VISIBLE);
                title.setText(HtmlCompat.fromHtml(step.getTitle(), HtmlCompat.FROM_HTML_MODE_COMPACT));
            }

            // Set Summary
            if (!TextUtils.isEmpty(step.getText())) {
                TextView summary = findViewById(R.id.rsb_intruction_text);
                summary.setVisibility(View.VISIBLE);
                summary.setText(HtmlCompat.fromHtml(step.getText(), HtmlCompat.FROM_HTML_MODE_COMPACT));
                summary.setMovementMethod(new TextViewLinkHandler() {
                    @Override
                    public void onLinkClick(String url) {
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
            final SubmitBar submitBar = findViewById(R.id.rsb_submit_bar);
            submitBar.setPositiveTitle(LocalizationUtils.getLocalizedString(getContext(), R.string.rsb_next));
            submitBar.setPositiveAction(view -> {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                        step,
                        null);
                submitBar.clearActions();
            });

            if (step.isOptional()) {
                submitBar.setNegativeTitle(LocalizationUtils.getLocalizedString(submitBar.getContext(), R.string.rsb_step_skip));
                submitBar.setNegativeAction(view -> {
                    callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
                    submitBar.clearActions();
                });
            } else {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }
    }
}
