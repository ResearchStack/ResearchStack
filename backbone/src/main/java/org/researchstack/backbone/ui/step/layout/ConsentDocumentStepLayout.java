package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.SubmitBar;

/**
 * Implement saved state for the following objects:
 * {@link #step}
 * {@link #stepResult}
 * {@link #confirmationDialogBody}
 */
public class ConsentDocumentStepLayout extends LinearLayout implements StepLayout
{
    private StepCallbacks callbacks;

    private String confirmationDialogBody;
    private String htmlContent;

    private ConsentDocumentStep step;
    private StepResult<Boolean> stepResult;

    public ConsentDocumentStepLayout(Context context)
    {
        super(context);
    }

    public ConsentDocumentStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentDocumentStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentDocumentStep) step;
        this.confirmationDialogBody = ((ConsentDocumentStep) step).getConfirmMessage();
        this.htmlContent = ((ConsentDocumentStep) step).getConsentHTML();
        this.stepResult = result;

        if(stepResult == null)
        {
            stepResult = new StepResult<>(step);
        }

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
        stepResult.setResult(false);
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, stepResult);
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

    private void initializeStep()
    {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_step_layout_consent_doc, this, true);

        WebView pdfView = (WebView) findViewById(R.id.webview);
        pdfView.loadData(htmlContent, "text/html; charset=UTF-8", null);

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveAction(v -> showDialog());
        submitBar.setNegativeAction(v -> callbacks.onCancelStep());
    }

    private void showDialog()
    {
        new AlertDialog.Builder(getContext()).setTitle(R.string.rsb_consent_review_alert_title)
                .setMessage(confirmationDialogBody)
                .setCancelable(false)
                .setPositiveButton(R.string.rsb_agree, (dialog, which) -> {
                    stepResult.setResult(true);
                    callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
                })
                .setNegativeButton(R.string.rsb_consent_review_cancel, (dialog, which) -> {
                    // Gives them a chance to read it again
                })
                .show();
    }
}
